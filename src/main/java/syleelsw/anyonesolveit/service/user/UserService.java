package syleelsw.anyonesolveit.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.service.validation.ValidationService;

import java.net.MalformedURLException;

@Service @RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ValidationService validationService;
    public ResponseEntity setProfile(String Access, UserProfileDto userProfile){
        if(!validationService.isValidateBJId(userProfile.getBjname())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        userProfile.setFirst(false);
        Long id = tokenProvider.getUserId(Access);
        userRepository.save(userProfile.toUser(id));
        return new ResponseEntity(HttpStatus.OK);
    }
}
