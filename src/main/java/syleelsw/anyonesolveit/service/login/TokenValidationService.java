package syleelsw.anyonesolveit.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.domain.login.RefreshEntity;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshRedisRepository;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.TokenType;
import syleelsw.anyonesolveit.service.login.dto.GoogleInfoResponse;
import syleelsw.anyonesolveit.service.login.dto.GoogleRequest;
import syleelsw.anyonesolveit.service.login.dto.GoogleResponse;
import syleelsw.anyonesolveit.aops.Timer;

import java.util.*;

@Slf4j @RequiredArgsConstructor @Service
public class TokenValidationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider provider;
    @Autowired
    private final RefreshRedisRepository refreshRedisRepository;
    @Value("${spring.google.client_id}")
    String clientId;
    @Value("${spring.google.client_secret}")
    String clientSecret;
    @Timer("Google Authcode")
    public ResponseEntity<GoogleInfoResponse> getResponseFromGoogle(String authCode, RestTemplate restTemplate){
        log.info("authcode: {}", authCode);
        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(authCode)
                .redirectUri("postmessage")
                .grantType("authorization_code").build();

        ResponseEntity<GoogleResponse> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);
        String jwtToken = response.getBody().getId_token();
        Map<String, String> map=new HashMap<>();
        map.put("id_token",jwtToken);
        return restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfoResponse.class);
    }
    public void deleteRedisRepository(Long key){
        refreshRedisRepository.deleteById(key);
    }
    @Timer("Checking RefreshToken")
    public boolean checkRefreshToken(String jwt,Long key){
        Optional<RefreshEntity> refreshEntity = refreshRedisRepository.findById(key);
        if(refreshEntity.isPresent()){
            log.info("refresh Token data... {}", refreshEntity.get().toString());
            RefreshEntity data = refreshEntity.get();
            if(!data.getRefreshToken().equals(jwt) || data.getExpired() > System.currentTimeMillis()){
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    @Timer("saving to Redis")
    //todo: User로 빠구기
    public String makeRefreshTokenAndSaveToRedis(Long id) {
        String refreshToken = provider.createJwt(id, TokenType.REFRESH);
        RefreshEntity refreshEntity = new RefreshEntity(id, refreshToken);
        refreshRedisRepository.save(refreshEntity);
        return refreshToken;
    }
    @Timer("getHeadersFromDB")
    public HttpHeaders getJwtHeaders(Long id, String refreshToken){
        Optional<UserInfo> user = userRepository.findById(id);
        String accessToken = provider.createJwt(id, TokenType.ACCESS);
        return makeJwtHeaders(accessToken, refreshToken);
    }

    public HttpHeaders makeJwtHeaders(String Gauth, String refresh){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Gauth", Gauth);
        headers.add("RefreshToken", refresh);
        return headers;
    }
}
