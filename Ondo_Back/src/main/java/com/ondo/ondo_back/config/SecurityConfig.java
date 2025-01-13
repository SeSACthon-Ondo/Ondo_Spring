package com.ondo.ondo_back.config;

import com.ondo.ondo_back.auth.jwt.JWTFilter;
import com.ondo.ondo_back.auth.jwt.JWTUtil;
import com.ondo.ondo_back.auth.repository.RefreshRepository;
import com.ondo.ondo_back.auth.service.MemberDetailService;
import com.ondo.ondo_back.auth.service.TokenBlacklistService;
import com.ondo.ondo_back.common.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberDetailService memberDetailServie;
    private final TokenBlacklistService tokenBlacklistService;

    public SecurityConfig(

            AuthenticationConfiguration authenticationConfiguration,
            JWTUtil jwtUtil,
            RefreshRepository refreshRepository,
            MemberDetailService memberDetailService,
            TokenBlacklistService tokenBlacklistService
    ) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.memberDetailServie = memberDetailService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailServie);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        if (authenticationManager instanceof ProviderManager) {

            ((ProviderManager) authenticationManager).getProviders().add(authenticationProvider());
        }

        return authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberRepository memberRepository) throws Exception {

        http.csrf(csrf -> {
            csrf.disable();
        });

        http.formLogin(formLogin -> {
            formLogin.disable();
        });

        http.httpBasic(httpBasic -> {
            httpBasic.disable();
        });

        http.authorizeHttpRequests(authz -> {
            authz.requestMatchers("/login").permitAll()
                    .anyRequest().authenticated();
        });

        http.addFilterBefore(
                new JWTFilter(jwtUtil, memberRepository, tokenBlacklistService),
                UsernamePasswordAuthenticationFilter.class
        );

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();

            corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setMaxAge(3600L);
            corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
            return corsConfiguration;
        }));

        return http.build();
    }
}
