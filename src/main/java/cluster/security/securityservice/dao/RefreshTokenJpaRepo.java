package cluster.security.securityservice.dao;

import cluster.security.securityservice.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenJpaRepo extends JpaRepository<RefreshToken, String> {
}
