package cluster.security.securityservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtServiceImplTest {

    @Test
    void getAccessTokenAndSetAllTokens() {
    }

    @Test
    void updatedAccessToken() {
    }

    @Test
    void getPublicKey() {
    }

    @Test
    void getUsernameFromToken() {
    }

    @Test
    void removeTokensFromCookie() {
    }

    @Test
    void isLoggedIn() {
    }
}