package syleelsw.anyonesolveit.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.login.Provider;
import syleelsw.anyonesolveit.api.login.dto.LoginBody;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.domain.login.RefreshShort;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshRedisRepository;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshShortRedisRepository;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.service.login.dto.github.GithubInfo;
import syleelsw.anyonesolveit.service.login.dto.github.GithubTokenResponse;
import syleelsw.anyonesolveit.service.login.dto.google.GoogleInfoResponse;
import syleelsw.anyonesolveit.service.login.dto.kakao.KakaoInfo;
import syleelsw.anyonesolveit.service.login.dto.kakao.KakaoTokenRequest;
import syleelsw.anyonesolveit.service.login.dto.naver.NaverInfo;
import syleelsw.anyonesolveit.service.user.UserService;
import syleelsw.anyonesolveit.service.user.dto.RankAndSolvedProblem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service @RequiredArgsConstructor @Slf4j
public class LoginService {
    private final JwtTokenProvider provider;
    private final RefreshShortRedisRepository refreshShortRedisRepository;
    private final RefreshRedisRepository refreshRedisRepository;
    private final TokenValidationService tokenValidationService;
    private final UserRepository userRepository;
    private final UserService userService;

    String kakaoUrl = "https://kauth.kakao.com/oauth";
    @Value("${spring.kakao.client_id}")
    String kakao_id;
    @Value("${spring.kakao.client_secret}")
    String kakao_secret;
    @Value("${spring.kakao.redirect_uri}")
    String  kakao_redirect_url;
    @Value("${spring.github.client_id}")
    String github_id;
    @Value("${spring.github.client_secret}")
    String github_secret;
    @Transactional
    public ResponseEntity updateRefreshToken(String jwt){
        Long id = provider.getUserId(jwt);
        log.info("Expried Token.. , id: {}", id);

        if(tokenValidationService.checkRefreshToken(jwt, id)){
            log.info("Valid Expried Token.., id: {}", id);
            String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(id);
            HttpHeaders jwtHeaders = tokenValidationService.getJwtHeaders(id, refresh);
            refreshShortRedisRepository.save(new RefreshShort(jwt, refresh, jwtHeaders.getFirst("Access")));
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
        if(userInfo == null) { userInfo = join(email, username, Provider.test, "123");}
        findUserAndJoin(email, username, Provider.test, "");
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());
        return tokenValidationService.getJwtHeaders(userInfo.getId(), refresh);
    }


    public ResponseEntity findUserAndJoin(String email, String username,Provider provider, String picture) {
        UserInfo userInfo = userRepository.findUserByEmail(email);
        if(userInfo == null) { userInfo = join(email, username, provider, picture);}
        if(!userInfo.getProvider().equals(provider)) return new ResponseEntity(Map.of("provider", userInfo.getProvider()), HttpStatus.BAD_REQUEST);
        if(userInfo.getBjname()!=null){
            RankAndSolvedProblem rankAndSolveProblem = userService.getRankAndSolveProblem(userInfo.getBjname());
            userInfo.setRank(rankAndSolveProblem.rank);
            userInfo.setSolvedProblem(new ArrayList<>(rankAndSolveProblem.solvedProblemDto.getSolvedProblems()));
            userInfo.setSolved((long) rankAndSolveProblem.solvedProblemDto.getSolvedProblems().size());
        }
        userRepository.save(userInfo);
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());
        return new ResponseEntity<>(Map.of("username", username, "imageUrl", picture, "email", email, "isFirst", userInfo.isFirst()), tokenValidationService.getJwtHeaders(userInfo.getId(), refresh), HttpStatus.OK);
    }

    public ResponseEntity googleLogin(String authCode,Provider authProvider){
        RestTemplate restTemplate = new RestTemplate();
        //트라이 익셉션.
        ResponseEntity<GoogleInfoResponse> infoResponse = tokenValidationService.getResponseFromGoogle(authCode, restTemplate);

        GoogleInfoResponse googleInfoResponse = infoResponse.getBody();
        String email = googleInfoResponse.getEmail();
        String username = googleInfoResponse.getName();
        String picture = googleInfoResponse.getPicture();
        return findUserAndJoin(email, username, authProvider, picture);
    }

    private ResponseEntity naverLogin(String authCode, String authState, Provider auth_provider) {
        RestTemplate restTemplate = new RestTemplate();
        //트라이 익셉션.
        ResponseEntity<NaverInfo> infoResponse = tokenValidationService.getResponseFromNaver(authCode, restTemplate, authState);

        NaverInfo googleInfoResponse = infoResponse.getBody();
        log.info("Nave user Info: {}", googleInfoResponse.toString());
        String email = googleInfoResponse.getEmail();
        String username = googleInfoResponse.getName();
        String picture = googleInfoResponse.getProfile_image();
        return findUserAndJoin(email, username, auth_provider, picture);
    }

    public UserInfo join(String email, String username, Provider provider, String picture){
        log.info("Join {}", email);
        UserInfo userInfo = UserInfo.builder()
                .email(email)
                .username(email)
                .name(username)
                .email(email)
                .isFirst(true)
                .picture(picture)
                .provider(provider)
                .build();
        return userRepository.save(userInfo);
    }

    @Transactional
    public ResponseEntity login(LoginBody loginBody) {
        Provider provider = loginBody.getProvider();
        String authCode = loginBody.getAuthCode();
        String authState = loginBody.getAuthState();
        return loginClassifier(authCode, provider, authState);
    }

    private ResponseEntity loginClassifier(String authCode, Provider provider, String authState) {
        switch (provider){
            case GOOGLE:
                return googleLogin(authCode, provider);
            case NAVER:
                return naverLogin(authCode, authState, provider);
            case KAKAO:
                return kakaoLogin();
            case GITHUB:
                return new ResponseEntity<>(gitHubLogin(),HttpStatus.OK);
            default:
                throw new IllegalStateException("잘못된 Provider 입니다.");
        }
    }

    public String gitHubLogin() {
        String url = "https://github.com/login/oauth/authorize?client_id="
                + github_id +"&scope=user:email"
                +"&redirect_uri=http://localhost:8080/api/login/github";
        RestTemplate restTemplate = new RestTemplate();
        log.info(url);
        return restTemplate.getForObject(url, String.class);
    }

    public ResponseEntity logout(String access) {
        Long userId = provider.getUserId(access);
        refreshRedisRepository.deleteById(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity kakaoLogin() {
        RestTemplate restTemplate = new RestTemplate();
        URI redirectUri = null;
        try {
            redirectUri = new URI(kakaoUrl +"/authorize?response_type=code&client_id="+ kakao_id + "&redirect_uri=" + kakao_redirect_url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    public ResponseEntity getkakaoLogin(String code) {
        String url = kakaoUrl + "/token";
        RestTemplate restTemplate = new RestTemplate();
        KakaoInfo kakaoResponse = tokenValidationService.getResponseFromKakao(KakaoTokenRequest.builder()
                .redirect_uri(kakao_redirect_url)
                .grant_type("authorization_code")
                .client_id(kakao_id)
                .client_secret(kakao_secret)
                .code(code)
                .build(), url);
        log.info("CODe : {}", code);
        log.info(kakaoResponse.toString());
        String email = kakaoResponse.getKakao_account().getEmail();
        String username =kakaoResponse.getKakao_account().getProfile().getNickname();
        String picture = kakaoResponse.getKakao_account().getProfile().getProfile_image_url();
        findUserAndJoin(email, username, Provider.KAKAO, picture);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("localhost:3000/login?email="+email+"&username=" + username + "&picture=" + picture));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    public ResponseEntity gitHubLogin(String code) {
        String url = "https://github.com/login/oauth/access_token?"
                +"client_id=" + github_id
                +"&client_secret=" + github_secret
                +"&code=" + code;
        RestTemplate restTemplate = new RestTemplate();
        log.info("tokenLoginUrl :{}", url);
        ResponseEntity<GithubTokenResponse> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST,
                null,
                GithubTokenResponse.class);
        log.info("token response: {}", responseEntity);
        GithubTokenResponse githubTokenResponse = responseEntity.getBody();
        String baseUrl = "https://api.github.com/user";

        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "token "+githubTokenResponse.getAccess_token());
        HttpEntity entity = new HttpEntity<>(header);
        ResponseEntity<GithubInfo> infoResponseEntity = restTemplate.exchange(baseUrl,
                HttpMethod.GET,
                entity,
                GithubInfo.class);
        log.info("infoResponse {} ", infoResponseEntity);
        GithubInfo githubInfo = infoResponseEntity.getBody();
        log.info("info: {}", githubInfo);
        String email = (String) githubInfo.getEmail();
        String username = (String) githubInfo.getName();
        String picture = (String) githubInfo.getAvatar_url();
        if(email.equals(null)) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        return findUserAndJoin(email, username, Provider.GITHUB, picture);
    }
}
