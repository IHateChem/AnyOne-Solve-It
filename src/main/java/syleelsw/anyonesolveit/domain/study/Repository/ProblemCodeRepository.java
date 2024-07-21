package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import syleelsw.anyonesolveit.domain.study.ProblemDetail;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.Optional;

public interface ProblemCodeRepository extends JpaRepository<ProblemDetail, Long> {
    Optional<ProblemDetail> findByStudyAndProblemNumber(Study study, Long problemNumber);
}