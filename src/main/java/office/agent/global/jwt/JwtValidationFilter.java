package office.agent.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import office.agent.member.dto.MemberAuthDto;
import office.agent.security.oauth2.CustomOAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static office.agent.global.jwt.JwtConstant.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final JwtUtil jwtUtil;

    //TODO: 현재 jwt 토큰에 username,nickname,role 을 저장하고 있다. 이를 redis에 UUID로 저장된 object를 조회하는식으로 변경하기
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = extractAccessToken(request.getCookies());
            if (accessToken != null) {
                Claims claims = jwtUtil.getClaims(accessToken);
                String username = claims.get(USER_NAME_CLAIM_KEY, String.class);
                String nickname = claims.get(NICKNAME_CLAIM_KEY, String.class);
                String role = claims.get(ROLE_CLAIM_KEY, String.class);
                MemberAuthDto authDto = new MemberAuthDto(username, nickname, role);
                CustomOAuth2User userDetails = new CustomOAuth2User(authDto);
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(token);
                this.securityContextHolderStrategy.setContext(context);
                log.info("jwt authenticate successfully");
            }
        } catch (JwtException e) {
            log.error("jwt error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Error");
            return;
        }
        filterChain.doFilter(request,response);
    }

    public String extractAccessToken(final Cookie[] cookies) {
        String result = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ACCESS_TOKEN_NAME)) {
                result = cookie.getValue();
                break;
            }
        }
        return result;
    }
}
