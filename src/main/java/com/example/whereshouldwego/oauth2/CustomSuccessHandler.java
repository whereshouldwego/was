package com.example.whereshouldwego.oauth2;

import com.example.whereshouldwego.config.CorsProps;
import com.example.whereshouldwego.domain.Refresh;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CorsProps corsProps;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // jwt 토큰 생성
        String access = jwtUtil.createJwt("access", userId, role, 3600000L);  // 1시간
        String refresh = jwtUtil.createJwt("refresh", userId, role, 1209600000L); // 14일

        // refresh 토큰 저장
        Refresh savedRefresh = Refresh.builder()
                .userId(userId)
                .refresh(refresh)
                .expiration(LocalDateTime.now().plusSeconds(1209600000L / 1000))
                .build();
        refreshRepository.save(savedRefresh);

        // refresh 토큰을 쿠키에 담아 반환
        response.addCookie(createCookie("refresh", refresh));

        // 브라우저를 생성된 URL로 리디렉션 
        String origin = request.getHeader("Origin");
        List<String> allowed = corsProps.getAllowedOrigins();
        String target = allowed.contains(origin) ? origin : allowed.get(0);

        // 액세스 토큰을 URL 파라미터로 추가
        String redirectUrl = target + "?accessToken=" + access;
        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 14);
        cookie.setPath("/");

        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }
}
