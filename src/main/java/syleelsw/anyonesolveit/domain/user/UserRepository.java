package syleelsw.anyonesolveit.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findUserByEmail(String email);
}
