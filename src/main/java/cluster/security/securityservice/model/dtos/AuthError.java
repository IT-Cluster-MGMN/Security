package cluster.security.securityservice.model.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class AuthError {

    private int status;
    private String message;
    private long timestamp;
}
