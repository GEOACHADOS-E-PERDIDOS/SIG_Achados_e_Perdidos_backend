package ifb.edu.br.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/registrar").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/recuperar-senha").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/trocar-senha").authenticated()
                                .requestMatchers("/objetos/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/categorias/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")
                                .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/uploads/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/postos/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/postos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/postos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/postos/**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    /*
     * @Bean
     * public PasswordEncoder passwordEncoder() {
     * return new BCryptPasswordEncoder();
     * }
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}