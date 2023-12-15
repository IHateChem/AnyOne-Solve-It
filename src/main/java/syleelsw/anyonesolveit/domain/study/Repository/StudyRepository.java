package syleelsw.anyonesolveit.domain.study.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import syleelsw.anyonesolveit.domain.study.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
