package office.agent.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static office.agent.global.jwt.JwtConstant.*;

@Slf4j
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    private SecretKey key;
    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(final String username, final String nickName, final String role) {
        Map<String, String> claims = new HashMap<>();
        claims.put(USER_NAME_CLAIM_KEY, username);
        claims.put(NICKNAME_CLAIM_KEY, nickName);
        claims.put(ROLE_CLAIM_KEY, role);
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String authToken) throws JwtException, IllegalArgumentException{
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload();
    }
}
