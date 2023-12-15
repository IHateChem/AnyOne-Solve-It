package syleelsw.anyonesolveit.service.study;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.join.UserStudyJoinRepository;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.domain.user.UserRepository;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;

import java.util.Optional;

@Service @Slf4j @RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserStudyJoinRepository userStudyJoinRepository;
    @Transactional
    public ResponseEntity createStudy(String access, StudyDto studyDto){
        Long userId = jwtTokenProvider.getUserId(access);
        UserInfo user = userRepository.findById(userId).get();
        Study study = Study.of(studyDto);
        study.setUser(user);
        study = studyRepository.save(study);
        UserStudyJoin userStudyJoin = UserStudyJoin.builder().study(study).user(user).build();
        //serStudyJoinRepository.save(userStudyJoin);
        return new ResponseEntity(study, HttpStatus.OK);
    }
}
