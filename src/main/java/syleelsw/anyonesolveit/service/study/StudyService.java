package syleelsw.anyonesolveit.service.study;

import com.sun.net.httpserver.HttpsServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.Problem;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.study.dto.StudyResponse;
import syleelsw.anyonesolveit.service.study.dto.StudyResponseMember;
import syleelsw.anyonesolveit.service.study.tools.ProblemSolvedCountUpdater;
import syleelsw.anyonesolveit.service.validation.ValidationService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Service @Slf4j @RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserStudyJoinRepository userStudyJoinRepository;
    private final ProblemSolvedCountUpdater problemSolvedCountUpdater;
    private final ValidationService validationService;
    private Map<Long, Problem> storeProblem;
    @Value("${anyone.page}")
    private Integer maxPage;
    //todo
    public ResponseEntity getMyStudy(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<List<Study>> studies = studyRepository.findStudiesByMember(user);
        if(studies.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(studies.get(), HttpStatus.OK) ;
    }

    @PostConstruct
    public void init(){
        storeProblem = new ConcurrentHashMap();
    }
    private Set<UserInfo> ValidateAndReturnMembers(List<Long> members){
        if(members.size() == 0){return new HashSet<UserInfo>();}
        List<UserInfo> users = userRepository.findAllById(members);
        if(users==null || users.size() < members.size()){
            throw new IllegalArgumentException("존재하지 않는 유저가 있습니다.");
        }
        log.info("유효한 유저입니다. ");
        return users.stream().collect(Collectors.toSet());
    }
    @Transactional
    public ResponseEntity createStudy(String access, StudyDto studyDto){
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Set<UserInfo> members;

        //잘못된 유저가 있는지 체크
        try{
            members = ValidateAndReturnMembers(studyDto.getMembers());
        }catch (IllegalArgumentException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        //잘못된 지역구 체크

        try{
            validationService.validateLocations(studyDto.getArea(), studyDto.getCity());
        }catch (IllegalArgumentException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Study study = Study.of(studyDto, members);
        study.setUser(user);
        //스터디 평균 랭크, 푼 문제수 등 계산
        problemSolvedCountUpdater.update(study);
        study = studyRepository.save(study);
        UserStudyJoin userStudyJoin = UserStudyJoin.builder().study(study).user(user).build();
        //serStudyJoinRepository.save(userStudyJoin);
        return new ResponseEntity(study, HttpStatus.OK);
    }
    //todo: aop 걸어서 업데이트 하기.
    public ResponseEntity getStudy(Long id) {
        Study study = studyRepository.findById(id).get();
        return new ResponseEntity(study, HttpStatus.OK);
    }
    private List listSplitter(List t,int page){
        if(t.size() > (page-1)*maxPage){
            return t.subList((page-1)*maxPage, min(page*maxPage, t.size()));
        }else{
            return new ArrayList();
        }
    }


    public ResponseEntity findStudy(Integer orderBy, Integer page, LanguageTypes language, GoalTypes level, String locations, String term) {
        List<Study> studies = null;
        String[] split = locations.split(" ");
        String city;
        if(split.length >2 || (split.length == 1 && !split[0].equals("ALL") || split.length==0)){
            log.info("잘못된 Locations: {}", split);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }else if(split.length == 1){
            city =  "ALL";
        }else{
            city = split[1];
        }
        Locations area = Locations.valueOf(split[0]);
        log.info("Area: {}", area);
        log.info("City: {}", city);
        try{
            validationService.validateLocations(area, city);
        }catch (IllegalArgumentException e){
            log.info("validationLocation 에서 걸림.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        PageRequest pageRequest = PageRequest.of((page-1)*maxPage, maxPage);
        switch (orderBy) {
            case 1 -> studies = studyRepository.searchStudyDefaultOrderBy1(language, level, area, city, term, pageRequest).get();
            case 2 -> studies =  studyRepository.searchStudyDefaultOrderBy2(language, level, area, city, term, pageRequest).get();
            case 3 -> studies =  studyRepository.searchStudyDefaultOrderBy3(language, level, area, city, term, pageRequest).get();
            default ->  new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        log.info("스터디t: {}", studies);
        return new ResponseEntity(studies.stream().map(study -> StudyResponse.of(study)).collect(Collectors.toList()), HttpStatus.OK);
    }

    public ResponseEntity getStudies(Integer orderBy, String term, Integer page, LanguageTypes language, GoalTypes level,Locations area) {
        return null;
    }
    public void updateStudy(Study study, StudyDto studyDto, Set<UserInfo> members){
        study.setTitle(studyDto.getTitle());
        study.setDescription(studyDto.getDescription());
        study.setLanguage(studyDto.getLanguage());
        study.setLevel(studyDto.getLevel());
        study.setArea(studyDto.getArea());
        study.setMeeting_type(studyDto.getMeeting_type());
        study.setPeriod(studyDto.getPeriod());
        study.setMembers(members);
        study.setFrequency(studyDto.getFrequency());
        study.setStudy_time(studyDto.getStudy_time());
    }
    public ResponseEntity putStudy(Long id, StudyDto studyDto) {
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(studyOptional.isEmpty()){return new ResponseEntity(HttpStatus.BAD_REQUEST);}
        Study study = studyOptional.get();
        List<UserInfo> users = userRepository.findAllById(studyDto.getMembers());
        updateStudy(study, studyDto, users.stream().collect(Collectors.toSet()));
        return new ResponseEntity(HttpStatus.OK);
    }
    private boolean validateDeleteStudy(String access, Long id){
        Long userId = jwtTokenProvider.getUserId(access);
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(!studyOptional.isPresent()){
            return false;
        }
        Study study = studyOptional.get();
        return study.getUser().getId() == userId;
    }

    public ResponseEntity delStudy(String access, Long id) {
        if(!validateDeleteStudy(access, id)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Study study = studyRepository.findById(id).get();
        studyRepository.delete(study);
        return new ResponseEntity(HttpStatus.OK);
    }
    private void saveProblem(Long id, SolvedacItem problem){
        Problem forStoreProblem = Problem.of(ProblemResponse.of(problem));
        storeProblem.put(id, forStoreProblem);
    }

    public ResponseEntity getStudyProblem(Long id, Integer problem) {
        String url = "https://solved.ac/api/v3/problem/show?problemId=" + problem;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        // HTTP POST 요청 보내기
        try{
            ResponseEntity<SolvedacItem> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    SolvedacItem.class
            );
            SolvedacItem solvedacItem = response.getBody();
            saveProblem(id, solvedacItem);
            return new ResponseEntity(ProblemResponse.of(solvedacItem), HttpStatus.OK);
        }catch (HttpClientErrorException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity getSuggestion(Long id) {
        Problem problem = storeProblem.get(id);
        //todo: 빈거처리 정해지면
        //if(problem == null) return new ResponseEntity()
        return new ResponseEntity(problem.toResponse(), HttpStatus.OK);
    }

    public ResponseEntity getMyStudySelf(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<List<Study>> studies = studyRepository.findAllByUser(user);
        if(studies.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(studies.get(), HttpStatus.OK) ;
    }
}
