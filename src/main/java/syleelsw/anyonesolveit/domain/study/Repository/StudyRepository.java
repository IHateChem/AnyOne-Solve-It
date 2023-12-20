package syleelsw.anyonesolveit.domain.study.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            "and (:level = 'ALL' or s.level = :level)" +
            "and(:area = 'ALL' or s.area = :area)" +
            " order by s.modifiedDateTime DESC", nativeQuery = false)
    Optional<List<Study>> searchStudyDefaultOrderBy1(
            @Param("language") LanguageTypes language,
            @Param("level") GoalTypes level,
            @Param("area") Locations area
    );
}
