package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository  extends JpaRepository<Notice, Long> {
    @Modifying
    @Query(value = "UPDATE Notice n SET n.toUser = :nextUser WHERE n.toUser = :user AND n.noticeType IN (5, 6)")
    void changeAllStudyManagerNotice(UserInfo user, UserInfo nextUser);
}
