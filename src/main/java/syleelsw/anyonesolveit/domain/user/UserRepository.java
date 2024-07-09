package syleelsw.anyonesolveit.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findUserByEmail(String email);

    @Query(value = "SELECT u.email FROM UserInfo u WHERE u.email LIKE %:userId%")
    List<String> searchByEmail(String userId);



    @Query(value = "SELECT u.id FROM UserInfo u WHERE u.bjname = :bjId")
    Optional<Long> findIdByBjName(String bjId);
}
