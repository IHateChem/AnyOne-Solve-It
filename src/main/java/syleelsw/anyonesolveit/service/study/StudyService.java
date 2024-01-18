package syleelsw.anyonesolveit.service.study;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.ParticipationDTO;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.Repository.ProblemRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyProblemRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.*;
import syleelsw.anyonesolveit.service.study.dto.StudyResponse;
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
    private final ProblemRepository problemRepository;
    private final ValidationService validationService;
    private final StudyProblemRepository studyProblemRepository;
    private final ParticipationRepository participationRepository;
    private Map<Long, Problem> storeProblem;
    @Value("${anyone.page}")
    private Integer maxPage;
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
            StaticValidator.validateLocations(studyDto.getArea(), studyDto.getCity());
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
            StaticValidator.validateLocations(area, city);
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
    @Transactional
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
    @Transactional
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
    //todo? id와 스터디 검증?
    public ResponseEntity getStudyProblem(Long id, Integer problemId) {

        Optional<Problem> problem = problemRepository.findById(problemId);
        Optional<Study> studyOptional = studyRepository.findById(id);
        ProblemResponse problemResponse;
        if(problem.isEmpty()){
            log.info("없는 문제.. {}", problemId);
            String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;
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
                problemResponse = ProblemResponse.of(solvedacItem);
                problem = Optional.of(problemRepository.save(Problem.of(problemResponse)));
            }catch (HttpClientErrorException e){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }else {
            problemResponse = ProblemResponse.of(problem.get());
        }
        StudyProblemEntity studyProblemEntity = StudyProblemEntity.builder().id(id +"_"+problemId).problem(problem.get()).study(studyOptional.get()).build();
        studyProblemRepository.save(studyProblemEntity);
        return new ResponseEntity(problemResponse, HttpStatus.OK);


    }

    public ResponseEntity getSuggestion(Long id) {
        Optional<List<StudyProblemEntity>> top10ByIdDesc = studyProblemRepository.findTop10ByStudyIdOrderByStudyIdDesc(id);
        if(top10ByIdDesc.isEmpty()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity(top10ByIdDesc.get(), HttpStatus.OK);
        }

    }

    public ResponseEntity getMyStudySelf(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<List<Study>> studies = studyRepository.findAllByUser(user);
        if(studies.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(studies.get(), HttpStatus.OK) ;
    }
    @Transactional
    public ResponseEntity deleteStudyProblem(String access, Long id, Integer problem) {
        Long userId = jwtTokenProvider.getUserId(access);
        Optional<UserInfo> user = userRepository.findById(userId);

        //스터디 원이 아닌 사람이 요청하면 권한 없음
        try{
            validationService.validateUserInStudy(user.get(), id);
        }catch (IllegalAccessException e){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<StudyProblemEntity> byId = studyProblemRepository.findById(id + "_" + problem);

        //올라간 적 없는 문제 삭제 요청시 BadRequest
        if(byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        studyProblemRepository.delete(byId.get());
        return new ResponseEntity(HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity makeParticipation(String access, ParticipationDTO participationDTO) {
        Long userId = jwtTokenProvider.getUserId(access);
        //스터디가 존재해야 신청할 수 있다.
        Long studyId = participationDTO.getStudyId();
        UserInfo user = userRepository.findById(userId).get();
        Study study = studyRepository.findById(studyId).get();

        try {
            validationService.isValidStudy(studyId);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(study.getUser().equals(user)){
            //스터디를 만든 사람은 스터디를 신청할 수 없다.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Participation save = participationRepository.save(Participation.builder()
                .id(userId+"_"+studyId)
                .message(participationDTO.getMessage())
                .user(user)
                .study(study)
                .state(ParticipationStates.대기중)
                .build());

        return new ResponseEntity(save.getId(), HttpStatus.OK);

    }
    @Transactional
    public ResponseEntity deleteParticipation(String access, Long studyId) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        //스터디가 존재해야 신청할 수 있다.
        try {
            validationService.isValidStudy(studyId);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Participation> byId = participationRepository.findById(userId + "_" + studyId);
        if(byId.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        participationRepository.delete(byId.get());
        return new ResponseEntity(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity confirmParticipation(String access, String participationId, Boolean confirm) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();

        try {
            validationService.isValidParticipationRequest(participationId, user);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Participation participation = participationRepository.findById(participationId).get();
        ParticipationStates state = confirm ? ParticipationStates.승인 :  ParticipationStates.거절;
        participation.setState(state);
        return new ResponseEntity(HttpStatus.OK);
    }
}
