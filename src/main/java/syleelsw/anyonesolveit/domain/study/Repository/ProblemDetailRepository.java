package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.ProblemDetail;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.Optional;

public interface ProblemDetailRepository extends JpaRepository<ProblemDetail, Long> {
    Optional<ProblemDetail> findByStudyAndProblemNumber(Study study, Long problemNumber);
}