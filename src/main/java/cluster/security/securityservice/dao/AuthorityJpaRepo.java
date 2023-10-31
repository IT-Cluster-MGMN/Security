package cluster.security.securityservice.dao;

import cluster.security.securityservice.model.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityJpaRepo extends JpaRepository<Authority, String> {
}
