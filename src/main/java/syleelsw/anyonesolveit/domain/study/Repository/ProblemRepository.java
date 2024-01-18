package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
}
