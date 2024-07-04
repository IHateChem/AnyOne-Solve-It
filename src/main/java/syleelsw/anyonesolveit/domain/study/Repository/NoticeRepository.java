package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import syleelsw.anyonesolveit.domain.study.Notice;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository  extends JpaRepository<Notice, Long> {
    @Modifying
    @Query(value = "UPDATE Notice n SET n.toUser = :nextUser WHERE n.toUser = :user AND n.noticeType IN (5, 6)")
    void changeAllStudyManagerNotice(UserInfo user, UserInfo nextUser);


    @Modifying
    @Query(value = "Select n FROM Notice n WHERE n.user = :user AND n.study = :study AND n.toUser = :toUser")
    Optional<Notice> findByUserAndStudyIdAndToUser(UserInfo user, UserInfo toUser, Study study);

    Optional<List<Notice>> findAllByToUserOrderByModifiedDateTimeDesc(UserInfo userInfo);
    @Modifying
    @Query(value = "DELETE FROM Notice n WHERE n.study = :study")
    void deleteAllByStudy(@Param("study") Study study);
}
