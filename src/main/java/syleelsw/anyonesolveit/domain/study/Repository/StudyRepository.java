package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Optional<List<Study>> findAllByUser(UserInfo user);
    @Query("SELECT s FROM Study s JOIN s.members m WHERE m = :userInfo")
    Optional<List<Study>> findStudiesByMember(@Param("userInfo") UserInfo userInfo);
    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:term is null or s.title LIKE %:term%)" +
            " order by s.modifiedDateTime DESC ", nativeQuery = false)
    Optional<List<Study>> searchStudyDefaultOrderBy1(
                @Param("language") LanguageTypes language,
                @Param("level") GoalTypes level,
                @Param("area") Locations area,
                @Param("term") String term,
                Pageable pageable);
    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:term is null or s.title LIKE %:term%)" +
            " order by s.avg_rank DESC", nativeQuery = false)
    Optional<List<Study>> searchStudyDefaultOrderBy2(
            @Param("language") LanguageTypes language,
            @Param("level") GoalTypes level,
            @Param("area") Locations area,
            @Param("term") String term,
            Pageable pageable);

    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:term is null or s.title LIKE %:term%)" +
            " order by s.avg_solved DESC", nativeQuery = false)
    Optional<List<Study>> searchStudyDefaultOrderBy3(
            @Param("language") LanguageTypes language,
            @Param("level") GoalTypes level,
            @Param("area") Locations area,
            @Param("term") String term,
            Pageable pageable);}
