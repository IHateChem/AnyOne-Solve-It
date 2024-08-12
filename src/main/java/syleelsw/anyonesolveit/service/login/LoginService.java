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
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.ProblemDetail;
import syleelsw.anyonesolveit.domain.study.Repository.*;
import syleelsw.anyonesolveit.domain.study.Study;
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
import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class LoginService {
    private final JwtTokenProvider provider;
    private final RefreshShortRedisRepository refreshShortRedisRepository;
    private final RefreshRedisRepository refreshRedisRepository;
    private final TokenValidationService tokenValidationService;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final StudyRepository studyRepository;
    private final UserService userService;
    private final StudyProblemRepository studyProblemRepository;
    private final ParticipationRepository participationRepository;
    private final ProblemDetailRepository problemDetailRepository;
    private final ProblemCodeRepository problemCodeRepository;

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
        if(tokenValidationService.checkRefreshToken(jwt, id)){
            String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(id);
            HttpHeaders jwtHeaders = tokenValidationService.getJwtHeaders(id, refresh);
            // 짧은시간 2회 요청 대비용
            refreshShortRedisRepository.save(new RefreshShort(jwt, jwtHeaders.get("Access").get(0)));
            return new ResponseEntity<>(jwtHeaders, HttpStatus.OK);
        }else{
            // 짧은시간 2회 요청 대비용
            Optional<RefreshShort> byId = refreshShortRedisRepository.findById(jwt);
            if(byId.isPresent()){
                String refresh = byId.get().getRefreshToken();
                String access = byId.get().getAccess();
                HttpHeaders headers = tokenValidationService.makeJwtHeaders(access, refresh);
                return new ResponseEntity<>(headers, HttpStatus.OK);
            }
            tokenValidationService.deleteRedisRepository(id);
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    public HttpHeaders test(String email){
        log.info("Test Login..., {}", userRepository.findUserByEmail(email));
        Optional<UserInfo> byEmail = userRepository.findUserByEmail(email);
        UserInfo userInfo;
        String username = "dltjrdn";
        if(byEmail.isEmpty()) { userInfo = join(email, username, Provider.test, "123");}
        else {userInfo= byEmail.get();}
        findUserAndJoin(email, username, Provider.test, "");
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());
        return tokenValidationService.getJwtHeaders(userInfo.getId(), refresh);
    }


    public ResponseEntity findUserAndJoin(String email, String username,Provider provider, String picture) {
        Optional<UserInfo> OptionalUserInfo = userRepository.findUserByEmail(email);
        UserInfo userInfo = null;
        if(OptionalUserInfo.isEmpty()) { userInfo = join(email, username, provider, picture);}
        else{ userInfo = OptionalUserInfo.get();}
        if(!userInfo.getProvider().equals(provider)) return new ResponseEntity(Map.of("provider", userInfo.getProvider()), HttpStatus.BAD_REQUEST);
        if(userInfo.getBjname()!=null){
            RankAndSolvedProblem rankAndSolveProblem = userService.getRankAndSolveProblem(userInfo.getBjname());
            userInfo.setRank(rankAndSolveProblem.rank);
            userInfo.setSolvedProblem(new ArrayList<>(rankAndSolveProblem.solvedProblemDto.getSolvedProblems()));
            userInfo.setSolved((long) rankAndSolveProblem.solvedProblemDto.getSolvedProblems().size());
        }
        userRepository.save(userInfo);
        String refresh = tokenValidationService.makeRefreshTokenAndSaveToRedis(userInfo.getId());
        int notices = noticeRepository.findAllByToUserOrderByModifiedDateTimeDesc(userInfo).get().size();
        return new ResponseEntity<>(Map.of("username", username, "imageUrl", picture, "email", email, "isFirst", userInfo.isFirst(), "notices" , notices), tokenValidationService.getJwtHeaders(userInfo.getId(), refresh), HttpStatus.OK);
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
        String email = googleInfoResponse.getEmail();
        String username = googleInfoResponse.getName();
        String picture = googleInfoResponse.getProfile_image();
        return findUserAndJoin(email, username, auth_provider, picture);
    }

    public UserInfo join(String email, String username, Provider provider, String picture){
        UserInfo userInfo = UserInfo.builder()
                .email(email)
                .username(email)
                .name(username)
                .email(email)
                .isFirst(true)
                .picture(picture)
                .provider(provider)
                .solved(0l)
                .rank(0)
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
        ResponseEntity<GithubTokenResponse> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST,
                null,
                GithubTokenResponse.class);
        GithubTokenResponse githubTokenResponse = responseEntity.getBody();
        String baseUrl = "https://api.github.com/user";

        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "token "+githubTokenResponse.getAccess_token());
        HttpEntity entity = new HttpEntity<>(header);
        ResponseEntity<GithubInfo> infoResponseEntity = restTemplate.exchange(baseUrl,
                HttpMethod.GET,
                entity,
                GithubInfo.class);
        GithubInfo githubInfo = infoResponseEntity.getBody();
        String email = (String) githubInfo.getEmail();
        String username = (String) githubInfo.getName();
        String picture = (String) githubInfo.getAvatar_url();
        if(email.equals(null)) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        return findUserAndJoin(email, username, Provider.GITHUB, picture);
    }

    @Transactional
    public ResponseEntity withdraw(String access) {
        Long userId = provider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        List<Study> studiesByMember = studyRepository.findStudiesByMember(user);
        // 멤버가 1명인 스터디이거나, 본인이 관리자가 아닌 것들만 남아있어야 한다.
        if(studiesByMember.stream().filter(study -> study.getMembers().size() == 1 || study.getUser() != user).toArray().length
            != studiesByMember.size()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        participationRepository.deleteAllByUser(user);
        noticeRepository.deleteAllByUserAndToUser(user);
        //탈퇴처리를 한다.
        studiesByMember.stream().forEach(study ->{
            if(study.getMembers().size() == 1){
                List<ProblemDetail> allByStudy = problemDetailRepository.findAllByStudy(study);
                allByStudy.forEach( pd -> problemCodeRepository.deleteAll(pd.getProblemCodes()));
                problemDetailRepository.deleteAllByStudy(study);
                studyProblemRepository.deleteAllByStudy(study);
                studyRepository.delete(study);
            }else{
                study.getMembers().remove(user);
                studyRepository.save(study);
            }
        });
        userRepository.delete(user);
        return new ResponseEntity(HttpStatus.OK);
    }


}
