package com.br.fasipe.compras.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Para APIs REST, é comum desabilitar a proteção contra CSRF
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(authorize -> authorize
                // Libera o acesso ao H2 Console
                .requestMatchers("/h2-console/**").permitAll() 
                // Exige autenticação para QUALQUER requisição dentro de /api/
                .requestMatchers("/api/**").authenticated() 
                // Permite todas as outras requisições (ex: página inicial, se houver)
                .anyRequest().permitAll()
            )
            // IMPORTANTE: Define a política de sessão como STATELESS (sem estado)
            // Isso força a autenticação a cada requisição, típico de APIs
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Habilita a autenticação HTTP Basic em vez do formulário de login
            .httpBasic(withDefaults());

        // Mantém as configurações para o H2 Console funcionar
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}