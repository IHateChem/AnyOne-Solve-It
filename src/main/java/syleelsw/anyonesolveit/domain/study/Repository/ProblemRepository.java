package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.Study;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
}
