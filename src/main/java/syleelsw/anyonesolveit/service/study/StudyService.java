package syleelsw.anyonesolveit.service.study;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.*;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Repository.*;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.*;
import syleelsw.anyonesolveit.service.study.dto.StudyResponse;
import syleelsw.anyonesolveit.service.study.tools.ProblemSolvedCountUpdater;
import syleelsw.anyonesolveit.service.validation.ValidationService;

import java.time.LocalDateTime;
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
    private final NoticeService noticeService;
    private final StudyUpdater studyUpdater;
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
        log.info("유효한 유저입니다. ");
        return new HashSet<>(users);
    }
    @Transactional
    public ResponseEntity<StudyResponse> createStudy(String access, StudyDto studyDto){
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Set<UserInfo> members;
        log.info("{} studyDto is.. .", studyDto);


        try{
            //잘못된 유저가 있는지 체크
            members = ValidateAndReturnMembers(studyDto.getMembers());
            //잘못된 지역구 체크
            StaticValidator.validateLocations(studyDto.getArea(), studyDto.getCity());
        }catch (IllegalArgumentException e){
            return getBadResponse();
        }

        //study가 온라인일때 Location 체크 
        if(checkOnlineAreaValidity(studyDto)){
            return getBadResponse();
        }

        Study study = Study.of(studyDto, members);
        study.setUser(user);
        Set<UserInfo> memberSet = study.getMembers();
        memberSet.add(user);
        study.setMembers(memberSet);
        //스터디 평균 랭크, 푼 문제수 등 계산
        problemSolvedCountUpdater.update(study);
        study = studyRepository.save(study);
        study.setRecruiting(true);
        log.info("study: {}", study);
        UserStudyJoin userStudyJoin = UserStudyJoin.builder().study(study).user(user).build();
        //serStudyJoinRepository.save(userStudyJoin);
        return new ResponseEntity(StudyResponse.of(study), HttpStatus.OK);
    }

    private static boolean checkOnlineAreaValidity(StudyDto studyDto) {
        return studyDto.getMeeting_type().equals("온라인") && studyDto.getArea().equals("ALL");
    }
    @Transactional
    public ResponseEntity<StudyResponse> getStudy(String access, Long id) {
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(studyOptional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Study study = studyOptional.get();
        study.setPopularity(study.getPopularity()+1);
        studyUpdater.updateUser(study.getMembers().stream().collect(Collectors.toList()));
        if(access != null) {
            Long userId = jwtTokenProvider.getUserId(access);
            UserInfo user = userRepository.findById(userId).get();
            return new ResponseEntity(StudyResponse.of(study, user), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(StudyResponse.of(study), HttpStatus.OK);
        }
    }
    private List listSplitter(List t,int page){
        if(t.size() > (page-1)*maxPage){
            return t.subList((page-1)*maxPage, min(page*maxPage, t.size()));
        }else{
            return new ArrayList();
        }
    }


    public ResponseEntity findStudy(Integer orderBy, Integer page, LanguageTypes language, GoalTypes level, Locations area, String city, Boolean onlineOnly, Boolean recruitingOnly, String term) {
        if(term != null && term.length()>20){return new ResponseEntity(HttpStatus.BAD_REQUEST);}
        List<Study> studies = null;
        try{
            StaticValidator.validateLocations(area, city);
        }catch (IllegalArgumentException e){
            log.info("validationLocation 에서 걸림.");
            return getBadResponse();
        }

        PageRequest pageRequest = PageRequest.of((page-1)*maxPage, maxPage);
        switch (orderBy) {
            case 1 -> studies = studyRepository.searchStudyDefaultOrderBy1(language, level, area, city, term, pageRequest, onlineOnly, recruitingOnly);
            case 2 -> studies =  studyRepository.searchStudyDefaultOrderBy2(language, level, area, city, term, pageRequest, onlineOnly, recruitingOnly);
            case 3 -> studies =  studyRepository.searchStudyDefaultOrderBy3(language, level, area, city, term, pageRequest, onlineOnly, recruitingOnly);
            default ->  getBadResponse();
        }
        log.info("스터디: {}", studies);
        return new ResponseEntity(studies.stream().map(StudyResponse::of).collect(Collectors.toList()), HttpStatus.OK);
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
        if(studyOptional.isEmpty()){return getBadResponse();}
        Study study = studyOptional.get();
        List<UserInfo> users = userRepository.findAllById(studyDto.getMembers());
        updateStudy(study, studyDto, new HashSet<>(users));
        return new ResponseEntity(StudyResponse.of(study), HttpStatus.OK);
    }
    private boolean validateDeleteStudy(String access, Long id){
        Long userId = jwtTokenProvider.getUserId(access);
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(!studyOptional.isPresent()){
            return false;
        }
        Study study = studyOptional.get();
        return study.getUser().getId().equals(userId);
    }
    @Transactional
    public ResponseEntity delStudy(String access, Long id) {
        if(!validateDeleteStudy(access, id)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Study study = studyRepository.findById(id).get();

        //알림생성
        studyRepository.delete(study);
        noticeService.deleteAllByStudy(study);
        study.getMembers().forEach(t-> noticeService.createNoticeType1to4(study, t, 4));
        return getGoodResponse();
    }
    private void saveProblem(Long id, SolvedacItem problem){
        Problem forStoreProblem = Problem.of(ProblemResponse.of(problem));
        storeProblem.put(id, forStoreProblem);
    }

    private Problem getProblemInfoFromSolvedAc(Integer problemId){
        log.info("없는 문제.. {}", problemId);
        String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        // HTTP POST 요청 보내기
        ResponseEntity<SolvedacItem> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SolvedacItem.class
        );
        SolvedacItem solvedacItem = response.getBody();
        return problemRepository.save(Problem.of( ProblemResponse.of(solvedacItem)));
    }
    //todo? id와 스터디 검증?
    public ResponseEntity getStudyProblem(Long id, Integer problemId) {
        Problem problem;
        try {
            problem = problemRepository.findById(problemId).orElse(getProblemInfoFromSolvedAc(problemId));
        }catch(HttpClientErrorException e){
            return getBadResponse();
        }
        ProblemResponse problemResponse = ProblemResponse.of(problem);
        Optional<Study> studyOptional = studyRepository.findById(id);
        StudyProblemEntity studyProblemEntity = StudyProblemEntity.builder().id(id +"_"+problemId).problem(problem).study(studyOptional.get()).build();
        studyProblemRepository.save(studyProblemEntity);
        return new ResponseEntity(problemResponse, HttpStatus.OK);


    }

    public ResponseEntity getSuggestion(Long id) {
        Optional<List<StudyProblemEntity>> top10ByIdDesc = studyProblemRepository.findTop10ByStudyIdOrderByStudyIdDesc(id);
        if(top10ByIdDesc.isEmpty()){
            return getBadResponse();
        }else{
            return new ResponseEntity(top10ByIdDesc.get().stream().map(StudyProblemResponse::of).collect(Collectors.toList()), HttpStatus.OK);
        }

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
        return getGoodResponse();
    }
    @Transactional
    public ResponseEntity<String> makeParticipation(String access, ParticipationDTO participationDTO) {
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

        //성공시 알림 생성
        log.info("참가 신청 성공, {}", participationDTO);
        noticeService.createNotice56(study, study.getUser(),5 ,user);

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
        return getGoodResponse();
    }

    @Transactional
    public ResponseEntity confirmParticipation(String access, String participationId, Boolean confirm) {
        log.info("participationId: {}", participationId);
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();

        try {
            validationService.isValidParticipationRequest(participationId, user);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Participation participation = participationRepository.findById(participationId).get();

        if(confirm){
            Long studyId = participation.getStudy().getId();
            Study study = studyRepository.findById(studyId).get();
            study.getMembers().add(participation.getUser());
            studyRepository.save(study);
        }

        ParticipationStates state = confirm ? ParticipationStates.승인 :  ParticipationStates.거절;
        participation.setState(state);
        int noticeType = confirm ? 1 : 2;
        participationRepository.deleteById(participationId);
        noticeService.createNoticeType1to4(participation.getStudy(), participation.getUser(), noticeType);
        return getGoodResponse();
    }
    public ResponseEntity getMyStudy(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        List<Study> studies = studyRepository.findStudiesByMember(user);
        //if(studies.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(getStudyResponse(studies), HttpStatus.OK) ;
    }

    public ResponseEntity getMyStudyAll(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();

        List<Study> managedStudies = studyRepository.findAllByUser(user);
        List<Study> participatedStudies = studyRepository.findStudiesByMember(user)
                .stream().filter(
                        participation -> !participation.getUser().getId().equals(userId)
                ).toList();
        return new ResponseEntity(Map.of("managements", getStudyResponse(managedStudies), "participations", getStudyResponse(participatedStudies)), HttpStatus.OK);
    }

    public ResponseEntity getMyStudySelf(String access) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        List<Study> studies = studyRepository.findAllByUser(user).stream().filter(study->study.getMembers().size() > 1).collect(Collectors.toList());
        //if(studies.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(getStudyResponse(studies), HttpStatus.OK) ;
    }

    private static List<StudyResponse> getStudyResponse(List<Study> studies) {
        return studies.stream().map(StudyResponse::of).collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity studyOut(String access, Long id) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<Study> studyOptional = studyRepository.findById(id);
        log.info("Study 나가기. Study: {}", studyOptional.get().getMembers());
        if(studyOptional.isEmpty()) return getBadResponse();
        Study study = studyOptional.get();

        //study에 없는 사람이 요청시 400
        if(!study.getMembers().contains(user)) return getBadResponse();

        if(study.getUser().equals(user)){
            // 스터디원이 한명밖에 없는경우
            if(study.getUser() == user) {
                studyRepository.delete(study);
            }
            return getGoodResponse(Map.of("isManager", true));
        }

        study.getMembers().remove(user);
        studyRepository.save(study);

        log.info("스터디원 제거 성공 {}", study.getMembers().contains(user));
        noticeService.createNotice56(study, study.getUser(), 6, user);
        return getGoodResponse(Map.of("isManager", false));
    }

    private static ResponseEntity getGoodResponse() {
        return new ResponseEntity(HttpStatus.OK);
    }
    private static ResponseEntity getGoodResponse(Object o) {
        return new ResponseEntity(o, HttpStatus.OK);
    }

    private static ResponseEntity getBadResponse() {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity delAllSuggestion(String access, Long id) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<Study> studyOptional = studyRepository.findById(id);
        log.info("이문제 어때요 전부 삭제. Study: {}", studyOptional.get());
        if(studyOptional.isEmpty()) return getBadResponse();
        Study study = studyOptional.get();
        studyProblemRepository.deleteAllByStudy(study);
        return getGoodResponse();
    }


    public ResponseEntity changeManger(String access, Long id, Long nextUserId) {
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Optional<Study> studyOptional = studyRepository.findById(id);
        log.info("스터디장 변경. Study: {}", studyOptional.get());
        if(studyOptional.isEmpty()) return getBadResponse();
        Study study = studyOptional.get();

        //스터디장 변경 유효성 확인
        if(!study.getUser().equals(user) || !study.getMembers().stream().anyMatch(t-> t.getId().equals(nextUserId))){
            return getBadResponse();
        }
        UserInfo nextUser = userRepository.findById(nextUserId).get();
        study.setUser(nextUser);

        //기존 스터디장이 받아야 하는 알림 변경
        noticeService.changeStudyManager(user, nextUser, study);
        return getGoodResponse();
    }

    public ResponseEntity<SearchProblemDto> getSearchProblem(Long studyId, Integer problemId) {
        Optional<Study> studyOptional = studyRepository.findById(studyId);
        if(studyOptional.isEmpty()) return getBadResponse();
        Study study = studyOptional.get();
        boolean isExist = true;
        try {
            problemRepository.findById(problemId).orElse(getProblemInfoFromSolvedAc(problemId));
        }catch(HttpClientErrorException e){
            isExist = false;
        }
        return new ResponseEntity(SearchProblemDto.of(isExist, study, problemId), HttpStatus.OK);
    }

    public ResponseEntity changeRecruiting(String access, Long id, boolean recruiting) {
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(studyOptional.isEmpty()) return getBadResponse();
        Study study = studyOptional.get();
        study.setRecruiting(recruiting);
        studyRepository.save(study);
        return new ResponseEntity(HttpStatus.OK);
    }
}
