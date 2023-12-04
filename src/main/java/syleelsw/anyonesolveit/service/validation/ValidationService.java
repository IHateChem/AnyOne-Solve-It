package syleelsw.anyonesolveit.service.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.service.validation.dto.UserSearchDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service @Slf4j
public class ValidationService {
    private String solvedacAPI = "https://solved.ac/api/v3";
    public boolean isValidateBJId(String bjId){
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
}
