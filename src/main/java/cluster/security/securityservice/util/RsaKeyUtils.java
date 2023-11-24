package cluster.security.securityservice.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@ConfigurationProperties(prefix = "rsa")
public record RsaKeyUtils(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
}
