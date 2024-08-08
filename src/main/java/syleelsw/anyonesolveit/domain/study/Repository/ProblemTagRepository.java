package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syleelsw.anyonesolveit.domain.study.ProblemTag;

import java.util.List;

public interface ProblemTagRepository extends JpaRepository<ProblemTag, String> {
    @Query("SELECT COUNT(p) FROM ProblemTag p")
    public Long howMany();
    // problemCount가 큰 순서대로 정렬하여 반환
    List<ProblemTag> findAllByOrderByProblemCountDesc();
}
