package syleelsw.anyonesolveit.api.study;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.service.study.StudyService;

@RestController @RequestMapping("/api/studies")
@RequiredArgsConstructor
@Slf4j
public class StudyController {
    private final StudyService studyService;
    @PostMapping
    public ResponseEntity createStudy(@RequestHeader String Access, @RequestBody StudyDto studyDto){
        return studyService.createStudy(Access, studyDto);
    }
}
