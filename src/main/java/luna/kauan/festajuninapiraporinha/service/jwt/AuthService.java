package luna.kauan.festajuninapiraporinha.service.jwt;

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
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        var usuario = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("CPF não encontrado"));

        return new User(
                usuario.getCpf(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }
}