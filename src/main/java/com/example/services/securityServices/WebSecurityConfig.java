package com.example.services.securityServices;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.services.securityServices.jwt.JwtRequestFilter;
import com.example.services.securityServices.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public RepositoryUserDetailsService userDetailService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración de CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir el origen de tu aplicación Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        
        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permitir todos los headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir el envío de credenciales (cookies, tokens)
        configuration.setAllowCredentials(true);
        
        // Configurar las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors() // Habilitar CORS
            .and()
            .authenticationProvider(authenticationProvider())
            .securityMatcher("/api/**")
            .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt))
            .authorizeHttpRequests(authorize -> authorize

             .requestMatchers(HttpMethod.GET, "/api/users/me/subjects").hasRole("USER")
                .anyRequest().permitAll() // Permitir todas las solicitudes
            )
            .formLogin(formLogin -> formLogin.disable()) // Desactivar autenticación de formulario
            .csrf(csrf -> csrf.disable()) // Desactivar protección CSRF
            .httpBasic(httpBasic -> httpBasic.disable()) // Desactivar autenticación básica
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sesiones sin estado
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Agregar filtro JWT

        return http.build();
    }
}
