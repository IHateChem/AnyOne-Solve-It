package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syleelsw.anyonesolveit.domain.study.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    // 가장 큰 id를 반환하는 함수.
    Long findTop1ByIdDesc();
}
