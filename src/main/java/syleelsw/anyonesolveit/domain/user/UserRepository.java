package syleelsw.anyonesolveit.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    public UserInfo findUserByEmail(String email);
}
