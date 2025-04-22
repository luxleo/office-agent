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
        response.addCookie(createCookie("access-token",accessToken, 60*60));
        response.addCookie(createCookie("refresh-token", refreshToken, 24 * 60 * 60));

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
