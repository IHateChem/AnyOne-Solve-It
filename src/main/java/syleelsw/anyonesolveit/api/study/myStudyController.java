package syleelsw.anyonesolveit.api.study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import syleelsw.anyonesolveit.service.study.StudyService;

@RestController @Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class myStudyController {
    private final StudyService studyService;
    @GetMapping("/mystudies")
    public ResponseEntity getMyStudyAll(@RequestHeader String Access){
        return studyService.getMyStudyAll(Access);
    }
    @GetMapping("/mystudies/management")
    public ResponseEntity getMyStudySelf(@RequestHeader String Access){
        return studyService.getMyStudySelf(Access);
    }
    @GetMapping("/mystudies/participation")
    public ResponseEntity getMyStudy(@RequestHeader String Access){
        return studyService.getMyStudy(Access);
    }
}
