package office.agent.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import office.agent.global.jwt.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static office.agent.global.jwt.JwtConstant.ACCESS_TOKEN_NAME;
import static office.agent.global.jwt.JwtConstant.REFRESH_TOKEN_NAME;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(
                principal.getUserName(),
                principal.getName(),
                principal.getAuthorities().iterator().next().getAuthority());
        String refreshToken = jwtUtil.generateRefreshToken(principal.getUserName());

        int ACCESS_TOKEN_COOKIE_EXPIRATION_SEC = 60 * 60; // 1 hour
        int REFRESH_TOKEN_COOKIE_EXPIRATION_SEC = 24*60 * 60; // 1 day
        response.addCookie(createCookie(ACCESS_TOKEN_NAME,accessToken, ACCESS_TOKEN_COOKIE_EXPIRATION_SEC));
        response.addCookie(createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_COOKIE_EXPIRATION_SEC));

        response.sendRedirect("http://localhost:5173");
    }

    private Cookie createCookie(final String key, final String value,final int seconds) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(seconds);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
