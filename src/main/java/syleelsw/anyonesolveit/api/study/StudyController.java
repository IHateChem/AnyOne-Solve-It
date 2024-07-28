package syleelsw.anyonesolveit.api.study;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.aops.IgnoreValidation;
import syleelsw.anyonesolveit.api.login.dto.OtherProblemDTO;
import syleelsw.anyonesolveit.api.study.dto.*;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;
import syleelsw.anyonesolveit.service.study.StudyService;

import java.util.List;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StudyController {
    private final StudyService studyService;
    @GetMapping("/studies")
    public ResponseEntity getStudies(@RequestParam Integer order_by, @RequestParam String term, @RequestParam Integer page,
                                     @RequestParam LanguageTypes language, @RequestParam GoalTypes level, @RequestParam Locations area,  @RequestParam String city,  @RequestParam Boolean onlineOnly, @RequestParam Boolean recruitingOnly){

        return studyService.findStudy(order_by, page, language, level, area, city, onlineOnly, recruitingOnly, term);
    }
    @PostMapping("/studies")
    public ResponseEntity createStudy(@RequestHeader String Access, @Validated @RequestBody StudyDto studyDto){
        return studyService.createStudy(Access, studyDto);
    }
    @GetMapping("/studies/{id}") @IgnoreValidation()
    public ResponseEntity getStudy(@RequestHeader(required = false) String Access, @PathVariable Long id){
        return studyService.getStudy(Access, id);
    }

    @PutMapping("/studies/{id}")
    public ResponseEntity putStudy(@RequestHeader String Access, @PathVariable Long id, @RequestBody StudyDto studyDto){
        return studyService.putStudy(id, studyDto);
    }

    @DeleteMapping("/studies/{id}")
    public ResponseEntity delStudy(@RequestHeader String Access, @PathVariable Long id){
        return studyService.delStudy(Access, id);
    }

    @GetMapping("/studies/{id}/search/{problem}")
    public ResponseEntity getSearchProblem(@RequestHeader String Access, @PathVariable Long id, @PathVariable Integer problem){
        return studyService.getSearchProblem(id, problem);
    }

    @DeleteMapping("/studies/{id}/suggestions")
    public ResponseEntity delAllSuggestion(@RequestHeader String Access, @PathVariable Long id){
        return studyService.delAllSuggestion(Access, id);
    }

    @PostMapping("/studies/{id}/other/suggestion")
    public ResponseEntity postOtherStudyProblem(@RequestHeader String Access, @PathVariable Long id,@RequestBody OtherProblemDTO problemDTO){
        return studyService.postOtherStudyProblem(id, problemDTO);
    }

    @PostMapping("/studies/{id}/suggestion/{problem}")
    public ResponseEntity getStudyProblem(@RequestHeader String Access, @PathVariable Long id, @PathVariable Integer problem){
        return studyService.getStudyProblem(id, problem);
    }
    @DeleteMapping("/studies/{id}/suggestion/{problem}")
    public ResponseEntity deleteStudyProblem(@RequestHeader String Access, @PathVariable Long id, @PathVariable Integer problem){
        return studyService.deleteStudyProblem(Access, id, problem);
    }
    @PatchMapping("/studies/{id}/manager")
    public ResponseEntity changeManger(@RequestHeader String Access, @PathVariable Long id, @RequestBody ChangeMangerRequest changeMangerRequest){
        return studyService.changeManger(Access, id, changeMangerRequest.getUserId());
    }


    @PatchMapping("/studies/{id}/recruiting")
    public ResponseEntity changeRecruiting(@RequestHeader String Access,@PathVariable Long id, @RequestBody ChangeRecruitingRequest changeRecruitingRequest){
        return studyService.changeRecruiting(Access, id, changeRecruitingRequest.isRecruiting());
    }

    @PostMapping("/studies/{id}/out")
    public ResponseEntity studyOut(@RequestHeader String Access, @PathVariable Long id){
        return studyService.studyOut(Access, id);
    }

    @GetMapping("/studies/{id}/suggestion")
    public ResponseEntity getSuggestion(@RequestHeader String Access, @PathVariable Long id){
        return studyService.getSuggestion(id);
    }
    @PostMapping("/participation")
    public ResponseEntity makeParticipation(@RequestHeader String Access, @RequestBody @Validated ParticipationDTO participationDTO){
        return studyService.makeParticipation(Access, participationDTO);
    }

    @DeleteMapping("/participation")
    public ResponseEntity deleteParticipation(@RequestHeader String Access, @RequestParam Long studyId){
        return studyService.deleteParticipation(Access, studyId);
    }

    @PostMapping("/participation/confirm")
    public ResponseEntity confirmParticipation(@RequestHeader String Access, @RequestBody ParticipationRequestDTO participationRequestDTO){
        String participationId = participationRequestDTO.getParticipationId();
        boolean confirm = participationRequestDTO.isConfirm();
        return studyService.confirmParticipation(Access, participationId, confirm);
    }



    @GetMapping("/studies/{id}/problems/{problem}")
    public ResponseEntity getProblemDetail(@RequestHeader String Access, @PathVariable Long id, @PathVariable Long problem){
        return studyService.getProblemDetail(id, problem);
    }


    @PostMapping("/studies/{id}/problems/{problem}")
    public ResponseEntity postProblemCode(@RequestHeader String Access, @PathVariable Long id, @PathVariable Long problem, @RequestBody ProblemCodeDTO problemCode){
        return studyService.postProblemCode(id, problem, problemCode);
    }

    @PutMapping("/studies/{id}/problems/{problem}")
    public ResponseEntity putProblemCode(@RequestHeader String Access, @PathVariable Long id, @PathVariable Long problem, @RequestBody ProblemCodeDTO problemCode){
        return studyService.putProblemCode(id, problem, problemCode);
    }

    @DeleteMapping("/studies/{id}/problems/{problem}")
    public ResponseEntity delProblemCode(@RequestHeader String Access, @PathVariable Long id, @PathVariable Long problem, @RequestParam Long codeId){
        return studyService.delProblemCode(id, problem, codeId);
    }

    @GetMapping("/problem/tags")
    public ResponseEntity getTags(@RequestHeader String Access){
        return studyService.getTags();
    }


    @GetMapping("/studies/{id}/problem/search")
    public ResponseEntity searchProblem(@RequestHeader String Access,@PathVariable Long id,@RequestParam String range, @RequestParam String minSolved,  @RequestParam String query, @RequestParam Boolean notSolved,@RequestParam List<String> tags){
        return studyService.searchProblem(id,range, minSolved, query, notSolved, tags);
    }

    @GetMapping("/search/tags")
    public ResponseEntity searchTag(@RequestHeader String Access, @RequestParam String query){
        return studyService.searchTag(query);
    }
}
