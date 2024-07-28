package syleelsw.anyonesolveit.domain.login.Respository;


import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import syleelsw.anyonesolveit.domain.login.RefreshEntity;
public interface RefreshRedisRepository extends CrudRepository<RefreshEntity, Long> {
}
