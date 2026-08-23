package luna.kauan.festajuninapiraporinha.infrastructure.security;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String cpfOrNome) throws UsernameNotFoundException {
        // Tenta localizar pelo CPF primeiro; se não achar, tenta pelo nome (para login de barracas)
        var usuarioOpt = userRepository.findByCpf(cpfOrNome);

        Usuario usuario;

        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
        } else {
            usuario = userRepository.findByNome(cpfOrNome);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }
        }

        // O username usado pelo UserDetails não pode ser nulo. Usar CPF quando disponível, senão o nome.
        String username = (usuario.getCpf() != null && !usuario.getCpf().isBlank()) ? usuario.getCpf() : usuario.getNome();

        return new User(
                username,
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }
}