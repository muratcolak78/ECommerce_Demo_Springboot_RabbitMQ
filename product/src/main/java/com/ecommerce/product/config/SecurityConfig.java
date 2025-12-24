package com.ecommerce.product.config;

import com.ecommerce.product.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ REST'te login sayfası çıkmasın
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ PUBLIC READ (ana sayfa ürün listesi)
                        .requestMatchers(HttpMethod.GET, "/api/ecommerce/product").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ecommerce/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                        // 🔒 WRITE işlemler JWT ister (admin)
                        .requestMatchers(HttpMethod.POST, "/api/ecommerce/product/**").authenticated()

                        .anyRequest().authenticated()
                )
                .build();
    }
}
