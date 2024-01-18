package syleelsw.anyonesolveit.api.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.api.user.dto.MyPageDto;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.service.user.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @PostMapping("/profile")
    public ResponseEntity setProfile(@RequestHeader String Access, @Validated @RequestBody UserProfileDto userProfile){
        return userService.setProfile(Access, userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity putProfile(@RequestHeader String Access, @Validated @RequestBody UserProfileDto userProfile){
        return userService.putProfile(Access, userProfile);
    }

    @GetMapping("/mypage")
    public ResponseEntity getMyPage(@RequestHeader String Access){
        return userService.getMyPage(Access);
    }



    @PostMapping("/mypage")
    public ResponseEntity createMyPage(@RequestHeader String Access, @Validated @RequestBody MyPageDto myPage){

        return userService.createMyPage(Access, myPage);
    }
    @PutMapping("/mypage")
    public ResponseEntity updateMyPage(@RequestHeader String Access, @Validated @RequestBody MyPageDto myPage){
        return userService.updateMyPage(Access, myPage);
    }

    @GetMapping("/mypage/apply") //내가 스터디에 참가신청한것들 목록보기
    public ResponseEntity getMyApply(@RequestHeader String Access){
        return userService.getMyApply(Access);
    }

    @GetMapping("/mypage/participation") //내가 스터디에 참가신청한것들 목록보기
    public ResponseEntity getMyParticipation(@RequestHeader String Access){
        return userService.getMyParticipation(Access);
    }
}
