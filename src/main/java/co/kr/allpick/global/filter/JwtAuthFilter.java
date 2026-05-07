package co.kr.allpick.global.filter;

import java.io.IOException;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthFilter.class);
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/seller/auth/login")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/signup")
                || path.startsWith("/api/admins/login")
                || path.startsWith("/api/members/login")
                || path.startsWith("/api/members/signup")
                || (request.getMethod().equals("GET") && path.equals("/api/products"))
                || (request.getMethod().equals("GET") && path.matches("/api/products/\\d+"))
                || path.startsWith("/api/categories")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtProvider.validateToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }

        Long memberId = jwtProvider.getMemberIdFromToken(token);
        String email = jwtProvider.getEmailFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        if (role != null) {
            Admin.AdminRole adminRole;
            try {
                adminRole = Admin.AdminRole.valueOf(role);
            } catch (IllegalArgumentException e) {
                logger.warn("유효하지 않은 role 값 - role: {}", role);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
            AdminJwtUserInfoDto adminUserInfo = new AdminJwtUserInfoDto(memberId, email, adminRole);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            adminUserInfo,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("관리자 인증 성공 - adminId: {}", memberId);
            filterChain.doFilter(request, response);
            return;
        }

        JwtUserInfoDto userInfo = new JwtUserInfoDto(memberId, email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userInfo,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("인증 성공 - memberId: {}", memberId);
        filterChain.doFilter(request, response);
    }
}
