package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
import java.util.Optional;

public interface StudyProblemRepository extends JpaRepository<StudyProblemEntity, String> {

    Optional<List<StudyProblemEntity>> findTop10ByStudyIdOrderByStudyIdDesc(Long id);

    @Modifying
    @Query("delete from StudyProblemEntity c where c.study = :study")
    void deleteAllByStudy(@Param("study") Study study);
}
