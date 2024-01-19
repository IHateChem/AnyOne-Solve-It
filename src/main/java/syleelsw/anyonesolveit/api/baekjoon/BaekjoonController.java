package syleelsw.anyonesolveit.api.baekjoon;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import syleelsw.anyonesolveit.service.validation.ValidationService;

import java.util.List;

@Controller @Slf4j @RequestMapping("/api")
@RequiredArgsConstructor
public class BaekjoonController {
    private final ValidationService validationService;
    @GetMapping("/validate/baekjoon")
    public ResponseEntity validateBaekjoonIds(@RequestParam List<String> ids){
        return validationService.validateBaekjoonIds(ids);
    }

}
