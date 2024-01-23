package syleelsw.anyonesolveit.service.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.Repository.NoticeRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

@Service @RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    @Transactional
    public void createNoticeType1to4(Study study, UserInfo user, int noticeType) {
        noticeRepository.save(Notice.builder().toUser(user).study(study).noticeType(noticeType).build());
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
}
