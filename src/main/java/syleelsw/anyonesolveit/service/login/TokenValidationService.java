package syleelsw.anyonesolveit.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.domain.login.RefreshEntity;
import syleelsw.anyonesolveit.domain.login.Respository.RefreshRedisRepository;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.TokenType;
import syleelsw.anyonesolveit.service.login.dto.google.GoogleInfoResponse;
import syleelsw.anyonesolveit.service.login.dto.google.GoogleRequest;
import syleelsw.anyonesolveit.service.login.dto.google.GoogleResponse;
import syleelsw.anyonesolveit.aops.Timer;
import syleelsw.anyonesolveit.service.login.dto.naver.NaverInfo;
import syleelsw.anyonesolveit.service.login.dto.naver.NaverRequest;
import syleelsw.anyonesolveit.service.login.dto.naver.NaverResponse;

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


    @Value("${spring.naver.client_id}")
    String n_clientId;
    @Value("${spring.naver.client_secret}")
    String n_clientSecret;
    @Timer("Naver Authcode")
    public ResponseEntity<NaverInfo> getResponseFromNaver(String authCode, RestTemplate restTemplate, String authState){
        log.info("authcode: {}", authCode);
        NaverRequest googleOAuthRequestParam = NaverRequest
                .builder()
                .client_id(n_clientId)
                .client_secret(n_clientSecret)
                .code(authCode)
                .state(authState)
                //.redirectUri("postmessage")
                .grant_type("code").build();

        ResponseEntity<NaverResponse> response = restTemplate.postForEntity("https://nid.naver.com/oauth2.0/token",
                googleOAuthRequestParam, NaverResponse.class);
        String jwtToken = response.getBody().getAccess_token();
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization","Bearer " + jwtToken);
        HttpEntity request = new HttpEntity(headers);
        return restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverInfo.class);
    }


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
        headers.add("Access", Gauth);
        headers.add("RefreshToken", refresh);
        return headers;
    }
}
