package syleelsw.anyonesolveit.service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.user.dto.SolvedacUserInfoDto;
import syleelsw.anyonesolveit.domain.etc.BaekjoonInformation;
import syleelsw.anyonesolveit.domain.etc.BaekjoonInformationRepository;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.validation.dto.UserSearchDto;
import syleelsw.anyonesolveit.service.validation.dto.ValidateResponse;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @Slf4j @RequiredArgsConstructor
public class ValidationService {
    private String solvedacAPI = "https://solved.ac/api/v3";
    private final StudyRepository studyRepository;
    private final ParticipationRepository participationRepository;
    private final BaekjoonInformationRepository baekjoonInformationRepository;
    public boolean isValidateBJId(String bjId){
        if(bjId.length()>20){return false;}
        Optional<BaekjoonInformation> bjInfo = baekjoonInformationRepository.findById(bjId);
        if(bjInfo.isPresent() && bjInfo.get().getModifiedDateTime().isAfter(LocalDateTime.now().minusDays(1))){ //캐쉬 가능
            return true;
        }

        try{
            log.info("Solvedac에 요청 보내는중... Id : {}", bjId);
            ResponseEntity<SolvedacUserInfoDto> response = getSolvedacUserInfoDtoResponseEntity(bjId);
            BaekjoonInformation myBjInfo = BaekjoonInformation.builder().bjname(bjId).rank(response.getBody().getRank()).solved(response.getBody().getSolvedCount()).build();

            baekjoonInformationRepository.save(myBjInfo);
            return true;
        }catch (HttpClientErrorException.NotFound notFound){
            return false;
        }catch (HttpClientErrorException otherError){
            throw new IllegalStateException("SolvedDac 서버 확인하세요");
        }
    }

    public ResponseEntity<SolvedacUserInfoDto> getSolvedacUserInfoDtoResponseEntity(String bjId) {
        String url = solvedacAPI + "/user/show?handle=" + bjId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        // HTTP POST 요청 보내기
        ResponseEntity<SolvedacUserInfoDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SolvedacUserInfoDto.class
        );
        log.info("{}", response);
        return response;
    }


    private Integer getUserRankFromAPI(String bjName){
        BaekjoonInformation baekjoonInformation = baekjoonInformationRepository.findById(bjName).get();
        return baekjoonInformation.getRank();
    }

    public Integer isValidateBJIdAndGetRank(String bjName) {
        if(!isValidateBJId(bjName)){
            return null;
        }
        return getUserRankFromAPI(bjName);
    }

    public void validateUserInStudy(UserInfo user, Long id) throws IllegalAccessException {
        Optional<Study> studyOptional = studyRepository.findById(id);
        if(! (studyOptional.isPresent() && studyOptional.get().getMembers().contains(user))){
            throw new IllegalAccessException("삭제 권한이 없습니다.");
        }
    }

    public void isValidStudy(Long studyId) throws IllegalAccessException {
        if(studyRepository.findById(studyId).isEmpty()){
            throw new IllegalAccessException("존재하지 않는 스터디 입니다.");
        }
    }

    public void isValidParticipationRequest(String participationId, UserInfo user) throws IllegalAccessException {
        Optional<Participation> byId = participationRepository.findById(participationId);
        if(! (byId.isPresent() && byId.get().getStudy().getUser().equals(user)
                && !byId.get().getState().equals(ParticipationStates.대기중))){
            // 아이디가 존재하지 않거나,참가 승인할 사람이 권한이 없거나.  참가신청 상태가 대기중이아니거나
            throw new IllegalAccessException("잘못된 승인 요청입니다.");
        }
    }

    public ResponseEntity validateBaekjoonIds(String id) {
        return new ResponseEntity(
                ValidateResponse.builder()
                .valid(isValidateBJId(id))
                .bjname(id)
                .build(), HttpStatus.OK);

    }
}
