package syleelsw.anyonesolveit.domain.join;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.Study;

public interface UserStudyJoinRepository extends JpaRepository<UserStudyJoin, Long> {
}
