package syleelsw.anyonesolveit.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.login.Provider;
import syleelsw.anyonesolveit.api.login.dto.LoginBody;
import syleelsw.anyonesolveit.domain.login.RefreshShort;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshShortRedisRepository;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.service.login.dto.GoogleInfoResponse;

import java.util.Map;
import java.util.Optional;

@Service @RequiredArgsConstructor @Slf4j
public class LoginService {
    private final JwtTokenProvider provider;
    private final RefreshShortRedisRepository refreshShortRedisRepository;
    private final TokenValidationService tokenValidationService;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity updateRefreshToken(String jwt){
        Long id = provider.getUserId(jwt);
        log.info("Expried Token.. , id: {}", id);

        if(tokenValidationService.checkRefreshToken(jwt, id)){
            log.info("Valid Expried Token.., id: {}", id);
            String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(id);
            HttpHeaders jwtHeaders = tokenValidationService.getJwtHeaders(id, refresh);
            refreshShortRedisRepository.save(new RefreshShort(jwt, refresh, jwtHeaders.getFirst("Gauth")));
            return new ResponseEntity<>(jwtHeaders, HttpStatus.OK);
        }else{
            Optional<RefreshShort> byId = refreshShortRedisRepository.findById(jwt);
            if(byId.isPresent()){
                return new ResponseEntity<>(tokenValidationService.makeJwtHeaders(byId.get().getAccess(), byId.get().getNewRefresh()), HttpStatus.OK);
            }
            tokenValidationService.deleteRedisRepository(id);
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    public HttpHeaders test(String email){
        UserInfo userInfo = userRepository.findUserByEmail(email);
        String username = "dltjrdn";
        if(userInfo == null) { userInfo = join(email, username);}
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());
        return tokenValidationService.getJwtHeaders(userInfo.getId(), refresh);
    }

    @Transactional
    public ResponseEntity googleLogin(String authCode){
        RestTemplate restTemplate = new RestTemplate();
        //트라이 익셉션.
        ResponseEntity<GoogleInfoResponse> infoResponse = tokenValidationService.getResponseFromGoogle(authCode, restTemplate);

        GoogleInfoResponse googleInfoResponse = infoResponse.getBody();
        String email = googleInfoResponse.getEmail();
        String username = googleInfoResponse.getName();
        UserInfo userInfo = userRepository.findUserByEmail(email);
        if(userInfo == null) { userInfo = join(email, username);}
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());

        return new ResponseEntity<>(Map.of("username", username), tokenValidationService.getJwtHeaders(userInfo.getId(), refresh), HttpStatus.OK);
    }

    public UserInfo join(String email, String username){
        log.info("Join {}", email);
        UserInfo userInfo = UserInfo.builder()
                .email(email)
                .username(username)
                .build();
        return userRepository.save(userInfo);
    }

    public ResponseEntity login(LoginBody loginBody) {
        Provider provider = loginBody.getProvider();
        String authCode = loginBody.getAuthCode();
        return loginClassifier(authCode, provider);
    }

    private ResponseEntity loginClassifier(String authCode, Provider provider) {
        switch (provider){
            case GOOGLE:
                return googleLogin(authCode);
            default:
                throw new IllegalStateException("잘못된 Provider 입니다.");
        }
    }
}
