package syleelsw.anyonesolveit.domain.login.Respository;

import org.springframework.data.repository.CrudRepository;
import syleelsw.anyonesolveit.domain.login.RefreshCnt;
import syleelsw.anyonesolveit.domain.login.RefreshShort;

public interface RefreshCntRedisRepository extends CrudRepository<RefreshCnt, String> {
}
