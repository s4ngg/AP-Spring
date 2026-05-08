package co.kr.allpick.global.config;

import co.kr.allpick.global.filter.JwtAuthFilter;
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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/members/check-email").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admins/login").permitAll()
                .requestMatchers("/api/seller/auth/login").permitAll()
                .requestMatchers("/api/sms/**").permitAll()
                .requestMatchers("/api/terms/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admins").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admins/*/status").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers("/api/admin/sellers/**").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/products/*/approve").hasAuthority("ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/products/*/reject").hasAuthority("ROLE_SUPER_ADMIN")
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
