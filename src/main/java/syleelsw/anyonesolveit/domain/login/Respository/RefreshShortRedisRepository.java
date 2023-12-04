package syleelsw.anyonesolveit.domain.login.Respository;

import org.springframework.data.repository.CrudRepository;
import syleelsw.anyonesolveit.domain.login.RefreshShort;

public interface RefreshShortRedisRepository extends CrudRepository<RefreshShort, String> {
}
