package cluster.security.securityservice.service;


import cluster.security.securityservice.dao.UserInfoJpaRepo;
import cluster.security.securityservice.model.entity.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoJpaRepo userInfoJpaRepo;

    @Transactional
    protected void saveUserInfo(UserInfo userInfo) {
        userInfoJpaRepo.save(userInfo);
    }
}
