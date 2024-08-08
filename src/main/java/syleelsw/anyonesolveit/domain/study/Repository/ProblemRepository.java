package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syleelsw.anyonesolveit.domain.study.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    // 가장 큰 id 를 반환하는 함수.
    @Query("SELECT MAX (p.id) FROM Problem p")
    Long findMaxId();
}
