package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;

import java.util.List;
import java.util.Optional;

public interface StudyProblemRepository extends JpaRepository<StudyProblemEntity, String> {

    Optional<List<StudyProblemEntity>> findTop10ByStudyIdOrderByStudyIdDesc(Long id);
}
