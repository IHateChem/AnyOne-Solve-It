package syleelsw.anyonesolveit.domain.study.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository  extends JpaRepository<Participation, String> {
    Optional<List<Participation>> findAllByUser(UserInfo user);

    @Query(value = "select p from Participation p where (p.study.user = :user) order by p.modifiedDateTime DESC")
    Optional<List<Participation>> findMyParticipationsByUser(UserInfo user);
}
