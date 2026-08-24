package luna.kauan.festajuninapiraporinha.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recuperarToken(request);

        if (token != null) {
            var subject = tokenService.validarToken(token);

            if (!subject.isEmpty()) {
                Usuario usuario = userRepository.findByCpf(subject).orElse(null);

                if (usuario == null) {
                    usuario = userRepository.findByNome(subject);
                }

                if (usuario == null) {
                    throw new RuntimeException("Usuário não encontrado na validação do token");
                }

                // Mapeia a Role do usuário para o formato que o Spring Security entende
                var authorities = List.of(new SimpleGrantedAuthority(usuario.getRole().name()));

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
