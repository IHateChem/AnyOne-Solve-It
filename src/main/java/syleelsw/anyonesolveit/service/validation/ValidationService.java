package syleelsw.anyonesolveit.service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.user.dto.SolvedacUserInfoDto;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.validation.dto.UserSearchDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @Slf4j @RequiredArgsConstructor
public class ValidationService {
    private String solvedacAPI = "https://solved.ac/api/v3";
    private final StudyRepository studyRepository;
    private boolean isValidateBJId(String bjId){
        String url = solvedacAPI + "/search/user?query=" + bjId;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        // HTTP POST 요청 보내기
        ResponseEntity<UserSearchDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                UserSearchDto.class
        );
        log.info(response.toString());
        Long count = response.getBody().getCount();
        log.info("{}", count > 0);
        return count != null && count > 0;
    }


    private Integer getUserRankFromAPI(String bjName){
        String url = solvedacAPI + "/user/show?handle=" + bjName;
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
        log.info(response.getBody().toString());
        return response.getBody().getRank();
    }

    public Integer isValidateBJIdAndGetRank(String bjName) {
        if(!isValidateBJId(bjName)){
            return null;
        }
        return getUserRankFromAPI(bjName);
    }

    public void validateLocations(Locations location, String city){
        List cities;
        switch (location){
            case ALL -> {
                return;
            }
            case 서울, 서울특별시 -> cities = List.of(
                    "강남구",
                    "강동구",
                    "강북구",
                    "강서구",
                    "관악구",
                    "광진구",
                    "구로구",
                    "금천구",
                    "노원구",
                    "도봉구",
                    "동대문구",
                    "동작구",
                    "마포구",
                    "서대문구",
                    "서초구",
                    "성동구",
                    "성북구",
                    "송파구",
                    "양천구",
                    "영등포구",
                    "용산구",
                    "은평구",
                    "종로구",
                    "중구",
                    "중랑구");
            case 경기도 -> cities = List.of(
                    "수원시",
                    "성남시",
                    "의정부시",
                    "안양시",
                    "부천시",
                    "광명시",
                    "평택시",
                    "동두천시",
                    "안산시",
                    "고양시",
                    "과천시",
                    "구리시",
                    "남양주시",
                    "오산시",
                    "시흥시",
                    "군포시",
                    "의왕시",
                    "하남시",
                    "용인시",
                    "파주시",
                    "이천시",
                    "안성시",
                    "김포시",
                    "화성시",
                    "광주시",
                    "양주시",
                    "포천시",
                    "여주시",
                    "연천군",
                    "가평군",
                    "양평군");
            default -> cities = new ArrayList();
        }
        if(!cities.stream().anyMatch(t -> t.equals(city))){
            throw new IllegalArgumentException("잘못된 도시명 입니다");
        }
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
}
