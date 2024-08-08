package syleelsw.anyonesolveit.service.study.tools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import syleelsw.anyonesolveit.api.study.dto.SolvedacItem;
import syleelsw.anyonesolveit.api.study.dto.SolvedacPageItem;
import syleelsw.anyonesolveit.api.user.dto.SolvedacUserInfoDto;
import syleelsw.anyonesolveit.domain.study.ProblemTag;
import syleelsw.anyonesolveit.domain.study.Repository.ProblemTagRepository;
import syleelsw.anyonesolveit.service.study.dto.SolvedacTag;
import syleelsw.anyonesolveit.service.study.dto.SolvedacTagDTO;

@RequiredArgsConstructor @Component @Slf4j
public class  ProblemTagsUpdator {

    private static final String solved_dac_url = "https://solved.ac/api/v3/tag/list?page=";
    private final ProblemTagRepository problemTagRepository;

    public void update(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SolvedacTagDTO> tagInfo = restTemplate.getForEntity(solved_dac_url+1, SolvedacTagDTO.class);
        if(!tagInfo.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get tags from solved.ac");
        }
        if (problemTagRepository.howMany() < tagInfo.getBody().getCount() ){
            log.info("Update Tags...");
            for(SolvedacTag tag: getTags(tagInfo.getBody().getCount())){
                problemTagRepository.save(ProblemTag.of(tag));
            }
        }

    }
    public static List<SolvedacTag> getTags(int count) {
        List<SolvedacTag> allKeys = new ArrayList<>();
        int page = 1;
        RestTemplate restTemplate = new RestTemplate();
        for(int i = 0; i < count/30+1; i++) {
            ResponseEntity<SolvedacTagDTO> tagResponse = restTemplate.getForEntity(solved_dac_url+ (i+1), SolvedacTagDTO.class);
            if(!tagResponse.getStatusCode().is2xxSuccessful()){
                throw new RuntimeException("Failed to get tags from solved.ac");
            }
            for(SolvedacTag tag: tagResponse.getBody().getItems()){
                allKeys.add(tag);
            }
        }
        return allKeys;
    }

}

