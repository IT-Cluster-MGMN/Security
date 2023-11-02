package cluster.security.securityservice.config;


import cluster.security.securityservice.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(TokenMessage.TOKEN_HEADER);
        String username = null;
        String jwt = null;

        if ((authHeader != null) && (authHeader.startsWith(TokenMessage.TOKEN_PREFIX))) {
            jwt = authHeader.substring(7);
            try {
                username = jwtTokenUtils.getUsernameFromToken(jwt);
            } catch (ExpiredJwtException e) {
                log.debug(TokenMessage.TOKEN_NOT_ACTUAL);
            } catch (SignatureException e) {
                log.debug(TokenMessage.TOKEN_WRONG_SIGNATURE);
            }
        }

        if ((username != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getAuthoritiesFromToken(jwt).stream()
                            .map(SimpleGrantedAuthority::new).toList()
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }

    private static class TokenMessage {
        private final static String TOKEN_HEADER = "Authorization";
        private final static String TOKEN_PREFIX = "Bearer ";
        private final static String TOKEN_NOT_ACTUAL = "Jwt is not actual";
        private final static String TOKEN_WRONG_SIGNATURE = "Wrong signature";
    }
}
