package com.example.whereshouldwego.config;

import com.example.whereshouldwego.jwt.JWTFilter;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.oauth2.CustomSuccessHandler;
import com.example.whereshouldwego.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProps.class)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final CorsProps corsProps;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration c = new CorsConfiguration();

                c.setAllowedOrigins(corsProps.getAllowedOrigins());
                c.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                c.setAllowedHeaders(Collections.singletonList("*"));
                c.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
                c.setAllowCredentials(true);
                c.setMaxAge(3600L);
                return c;
            }
        }));

        // CSRF / Form / Basic 비활성화
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // JWTFilter 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // oauth2
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(cfg -> cfg.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        // 경로별 인가 작업
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/guest").permitAll()
                .requestMatchers("/ws/**", "/ws-stomp/**").permitAll()
                .anyRequest().permitAll()
        );

        // 세션 설정 STATELESS
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
