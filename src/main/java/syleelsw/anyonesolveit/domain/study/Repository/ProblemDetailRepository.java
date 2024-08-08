package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.ProblemDetail;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.List;
import java.util.Optional;

public interface ProblemDetailRepository extends JpaRepository<ProblemDetail, Long> {
    List<ProblemDetail> findAllByStudy(Study study);
    Optional<ProblemDetail> findByStudyAndProblemNumber(Study study, Long problemNumber);

    void deleteAllByStudy(Study study);
}