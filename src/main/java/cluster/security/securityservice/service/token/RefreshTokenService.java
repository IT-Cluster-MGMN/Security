package cluster.security.securityservice.service.token;


import cluster.security.securityservice.dao.RefreshTokenJpaRepo;
import cluster.security.securityservice.exception.RefreshTokenException;
import cluster.security.securityservice.model.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenJpaRepo refreshTokenJpaRepo;


    @Transactional
    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepo.save(refreshToken);
    }

    @Transactional
    public boolean update(RefreshToken refreshToken) {
        final RefreshToken findRefreshToken;

        try {
            findRefreshToken = findById(refreshToken.getUsername());
        } catch (RefreshTokenException e) {
            return false;
        }

        findRefreshToken.setToken(refreshToken.getToken());
        refreshTokenJpaRepo.save(findRefreshToken);

        return true;
    }

    public RefreshToken findById(String username) {
        return refreshTokenJpaRepo.findById(username)
                .orElseThrow(() -> new RefreshTokenException(
                        String.format("Token with username '%s' does not exist", username)));
    }

}
