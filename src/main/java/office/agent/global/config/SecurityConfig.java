package office.agent.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import office.agent.global.jwt.JwtValidationFilter;
import office.agent.security.oauth2.CustomOAuth2MemberService;
import office.agent.security.oauth2.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomOAuth2MemberService oAuth2MemberService;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtValidationFilter jwtValidationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(req -> req
                        .anyRequest().authenticated())
                .oauth2Login(auth -> auth
                        .userInfoEndpoint(config -> config
                                .userService(oAuth2MemberService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173")); // TODO: 환경별로 어떻게 처리할 것인지 고안하기.
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true); // 이 설정을 해주어야 클라이언트에서 credential 요청을 담아 보낼때 허용할 수 있다.
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(Collections.singletonList("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .build();
    }

}
