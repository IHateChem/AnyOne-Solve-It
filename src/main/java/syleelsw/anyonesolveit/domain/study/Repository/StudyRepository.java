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
    List<Study> findAllByUser(UserInfo user);

    @Query("SELECT s FROM Study s JOIN s.members m WHERE m = :userInfo")
    List<Study> findStudiesByMember(@Param("userInfo") UserInfo userInfo);
    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:city = 'ALL' or s.city = :city)" +
            " and (:onlineOnly = false or s.meeting_type = '온라인' or s.meeting_type = '온·오프라인')" +
            " and (:recruitingOnly = false or s.recruiting = true)" +
            " and (:term is null or :term ='' or s.title LIKE %:term%)" +
            " order by s.modifiedDateTime DESC ", nativeQuery = false)
    List<Study> searchStudyDefaultOrderBy1(
                @Param("language") LanguageTypes language,
                @Param("level") GoalTypes level,
                @Param("area") Locations area,
                @Param("city") String city,
                @Param("term") String term,
                Pageable pageable,
                @Param("onlineOnly") Boolean onlineOnly,
                @Param("recruitingOnly") Boolean recruitingOnly
                );
    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:city = 'ALL' or s.city = :city)" +
            " and (:onlineOnly = false or s.meeting_type = '온라인' or s.meeting_type = '온·오프라인')" +
            " and (:recruitingOnly = false or s.recruiting = true)" +
            " and (:term is null or :term ='' or s.title LIKE %:term%)" +
            " order by s.avg_rank DESC", nativeQuery = false)
    List<Study> searchStudyDefaultOrderBy2(
            @Param("language") LanguageTypes language,
            @Param("level") GoalTypes level,
            @Param("area") Locations area,
            @Param("city") String city,
            @Param("term") String term,
            Pageable pageable,
            @Param("onlineOnly") Boolean onlineOnly,
            @Param("recruitingOnly") Boolean recruitingOnly
    );

    @Query(value = "select s from Study s" +
            " where (:language = 'ALL' or s.language = :language)" +
            " and (:level = 'ALL' or s.level = :level)" +
            " and (:area = 'ALL' or s.area = :area)" +
            " and (:city = 'ALL' or s.city = :city)" +
            " and (:onlineOnly = false or s.meeting_type = '온라인' or s.meeting_type = '온·오프라인')" +
            " and (:recruitingOnly = false or s.recruiting = true)" +
            " and (:term is null or :term ='' or s.title LIKE %:term%)" +
            " order by s.avg_solved DESC", nativeQuery = false)
    List<Study> searchStudyDefaultOrderBy3(
            @Param("language") LanguageTypes language,
            @Param("level") GoalTypes level,
            @Param("area") Locations area,
            @Param("city") String city,
            @Param("term") String term,
            Pageable pageable,
            @Param("onlineOnly") Boolean onlineOnly,
            @Param("recruitingOnly") Boolean recruitingOnly
    );}
