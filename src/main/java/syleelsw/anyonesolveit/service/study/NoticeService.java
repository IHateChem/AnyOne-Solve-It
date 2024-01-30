package syleelsw.anyonesolveit.service.study;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.api.user.dto.NoticeResponse;
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.Repository.NoticeRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    @Transactional
    public void createNoticeType1to4(Study study, UserInfo toUser, int noticeType) {
        noticeRepository.save(Notice.builder().toUser(toUser).study(study).noticeType(noticeType).build());
    }
    @Transactional
    public void createNoticeType3(Study study, UserInfo user) {
        noticeRepository.save(Notice.builder().toUser(user).study(study).noticeType(3).build());
    }
    @Transactional
    public void createNotice56(Study study, UserInfo to, int noticeType, UserInfo user) {
        noticeRepository.save(Notice.builder().toUser(to).study(study).noticeType(noticeType).user(user).build());
    }
    @Transactional
    public void changeStudyManager(UserInfo user, UserInfo nextUser, Study study) {
        noticeRepository.changeAllStudyManagerNotice(user, nextUser);
        createNoticeType3(study, nextUser);
    }

    public ResponseEntity<NoticeResponse> getNoticesByUser(UserInfo user) {
        List<Notice> allByToUser = noticeRepository.findAllByToUserOrderByModifiedDateTimeDesc(user).get();
        return new ResponseEntity(new NoticeResponse(allByToUser), HttpStatus.OK);
    }

    @Transactional
    public Optional<Notice> findById(Long id){
        return noticeRepository.findById(id);
    }

    public void delById(Long id) {
        noticeRepository.deleteById(id);
    }

    public void deleteAllByStudy(Study study) {
        noticeRepository.deleteAllByStudy(study);
    }
}
