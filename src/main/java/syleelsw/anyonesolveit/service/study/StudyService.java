package syleelsw.anyonesolveit.service.study;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
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
    private Map<Long, Problem> storeProblem;
    @Value("${anyone.page}")
    private Integer maxPage;

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

        Study study = Study.of(studyDto, members);
        study.setUser(user);
        study = studyRepository.save(study);
        UserStudyJoin userStudyJoin = UserStudyJoin.builder().study(study).user(user).build();
        //serStudyJoinRepository.save(userStudyJoin);
        return new ResponseEntity(study, HttpStatus.OK);
    }
    //todo
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

    public ResponseEntity findStudy(Integer orderBy, Integer page, LanguageTypes language, GoalTypes level, Locations area) {
        List<Study> studies;
        //todo: Repository에서 쿼리로 가져오게 해야할듯.
        switch (orderBy) {
            case 1:
                studies = listSplitter(studyRepository.searchStudyDefaultOrderBy1(language, level, area).get(), page);
                break;
            default:
                studies = null;
        }
        return new ResponseEntity(studies, HttpStatus.OK);
    }

    public ResponseEntity getStudies(Integer orderBy, String term, Integer page, LanguageTypes language, GoalTypes level,Locations area) {
        return null;
    }

    public ResponseEntity putStudy(Long id, StudyDto studyDto) {
        return null;
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
}
