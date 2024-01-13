package syleelsw.anyonesolveit.api.study;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.study.StudyService;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StudyController {
    private final StudyService studyService;
    @GetMapping("/studies")
    public ResponseEntity getStudies(@RequestParam Integer order_by, @RequestParam String term, @RequestParam Integer page,
                                     @RequestParam LanguageTypes language, @RequestParam GoalTypes level, @RequestParam Locations area){

        return studyService.findStudy(order_by, page, language, level,  area, term);
    }
    @PostMapping("/studies")
    public ResponseEntity createStudy(@RequestHeader String Access, @Validated @RequestBody StudyDto studyDto, BindingResult bindingResult){
        return studyService.createStudy(Access, studyDto);
    }
    @GetMapping("/studies/{id}")
    public ResponseEntity getStudy(@RequestHeader String Access, @PathVariable Long id){
        return studyService.getStudy(id);
    }

    @PutMapping("/studies/{id}")
    public ResponseEntity putStudy(@RequestHeader String Access, @PathVariable Long id, @RequestBody StudyDto studyDto){
        return studyService.putStudy(id, studyDto);
    }

    @DeleteMapping("/studies/{id}")
    public ResponseEntity delStudy(@RequestHeader String Access, @PathVariable Long id){
        return studyService.delStudy(Access, id);
    }

    @PostMapping("/studies/{id}/suggestion/{problem}")
    public ResponseEntity getStudyProblem(@RequestHeader String Access, @PathVariable Long id, @PathVariable Integer problem){
        return studyService.getStudyProblem(id, problem);
    }

    @GetMapping("/studies/{id}/suggestion")
    public ResponseEntity getSuggestion(@RequestHeader String Access, @PathVariable Long id){
        return studyService.getSuggestion(id);
    }


}
