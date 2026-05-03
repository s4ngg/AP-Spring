package co.kr.allpick.global.config;

import co.kr.allpick.global.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admins/login").permitAll()
                .requestMatchers("/api/seller/auth/login").permitAll()
                .requestMatchers("/api/sms/**").permitAll()
                .requestMatchers("/api/terms/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admins").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admins/*/status").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/faqs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/faqs/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/faqs/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/faqs/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/faqs/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/notices/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/notices/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/notices/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/notices/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/inquiries/admin").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/inquiries/*/answers/admin").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_CS_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
