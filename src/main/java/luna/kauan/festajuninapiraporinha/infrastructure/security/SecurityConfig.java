package luna.kauan.festajuninapiraporinha.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 1. Endpoints Públicos
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/cadastrar").permitAll()

                        // 2. Permissões de CLIENTE (Consulta de Saldo e Transações)
                        .requestMatchers(HttpMethod.GET, "/carteira/saldo").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/transacoes/minhas").hasRole("CLIENTE")

                        // 3. Permissões de BARRACA (Cobrança)
                        .requestMatchers(HttpMethod.GET, "/usuarios/buscar").hasAnyRole("BARRACA", "CAIXA")
                        .requestMatchers(HttpMethod.POST, "/transacoes/debitar").hasAnyRole("BARRACA", "CAIXA")
                        .requestMatchers(HttpMethod.GET, "/transacoes/barraca").hasRole("BARRACA")

                        // 4. Permissões de CAIXA (Depósito e Reembolso)
                        .requestMatchers(HttpMethod.POST, "/transacoes/depositar").hasRole("CAIXA")
                        .requestMatchers(HttpMethod.POST, "/transacoes/reembolsar").hasRole("CAIXA")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
