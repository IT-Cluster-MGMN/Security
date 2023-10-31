package cluster.security.securityservice.dao;

import cluster.security.securityservice.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoJpaRepo extends JpaRepository<UserInfo, String> {
}
