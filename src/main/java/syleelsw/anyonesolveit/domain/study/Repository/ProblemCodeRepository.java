package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.ProblemCode;
import syleelsw.anyonesolveit.domain.study.ProblemDetail;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.Optional;

public interface ProblemCodeRepository extends JpaRepository<ProblemCode, Long> {
}