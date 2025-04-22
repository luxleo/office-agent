package office.agent.global.jwt;

import io.jsonwebtoken.Claims;
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

@Slf4j
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    private final Long ACCESS_TOKEN_EXPIRATION_MS = 60*60*1000L; // 1시간
    private final Long REFRESH_TOKEN_EXPIRATION_MS = 24*60*60*1000L; // 1일
    private SecretKey key;
    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(final String username, final String nickName, final String role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("nickName", nickName);
        claims.put("role", role);
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
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String authToken) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload();
    }
}
