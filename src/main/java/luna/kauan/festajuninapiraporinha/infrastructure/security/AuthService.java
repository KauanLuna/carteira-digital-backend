package luna.kauan.festajuninapiraporinha.infrastructure.security;

import lombok.RequiredArgsConstructor;
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

        luna.kauan.festajuninapiraporinha.domain.entity.Usuario usuario;

        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
        } else {
            usuario = userRepository.findByNome(cpfOrNome);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }
        }

        return new User(
                usuario.getCpf(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }
}