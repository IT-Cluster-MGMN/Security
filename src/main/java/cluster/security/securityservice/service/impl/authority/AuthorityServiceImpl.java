package cluster.security.securityservice.service.impl.authority;


import cluster.security.securityservice.dao.AuthorityJpaRepo;
import cluster.security.securityservice.model.entity.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityJpaRepo authorityJpaRepo;

    @Transactional
    public void saveAuthority(Authority authority) {
        authorityJpaRepo.save(authority);
    }
}
