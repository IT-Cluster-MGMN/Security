package cluster.security.securityservice;

import cluster.security.securityservice.config.AccessRsaKeyConfig;
import cluster.security.securityservice.config.RefreshRsaKeyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AccessRsaKeyConfig.class, RefreshRsaKeyConfig.class})
public class SecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}

}
