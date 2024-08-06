package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyProblemRepository extends JpaRepository<StudyProblemEntity, String> {

    Optional<List<StudyProblemEntity>> findTop10ByStudyIdOrderByModifiedDateTimeDesc(Long id);


    void deleteAllByStudy(Study study);

    @Modifying
    @Query("SELECT spe FROM StudyProblemEntity spe " +
            "WHERE spe.study = :study " +
            "AND (spe.problem.title LIKE %:query% OR spe.problem.id = :query) " +
            "AND spe.createdDateTime BETWEEN :startDate AND :endDate " +
            "AND spe.problem.rank BETWEEN :startRank AND :endRank"
    )
    List<StudyProblemEntity> findByStudyAndQueryAndDateAndRank(
            @Param("study") Study study,
            @Param("query") String query,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("startRank") Integer startRank,
            @Param("endRank") Integer endRank
    );
}
