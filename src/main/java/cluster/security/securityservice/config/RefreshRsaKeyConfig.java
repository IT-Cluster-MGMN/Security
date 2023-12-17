package cluster.security.securityservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@ConfigurationProperties(prefix = "rsa.refresh")
public record RefreshRsaKeyConfig(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
}
