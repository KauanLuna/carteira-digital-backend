package luna.kauan.festajuninapiraporinha.service;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.entity.Carteira;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.domain.enums.Role;
import luna.kauan.festajuninapiraporinha.repository.CarteiraRepository;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UserRepository userRepository;
    private final CarteiraRepository carteiraRepository;
    private final PasswordEncoder passwordEncoder;

    // SecureRandom é thread-safe e criptograficamente mais seguro que o Random comum
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public Usuario cadastrarCliente(String nome, String cpf, String senha) {

        // 1. Validação de regra de negócio: CPF único
        if (userRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado em nosso sistema.");
        }

        // 2. Criação da entidade Usuário com senha convertida em Hash (BCrypt)
        Usuario usuario = Usuario.builder()
                .nome(nome)
                .cpf(cpf)
                .senha(passwordEncoder.encode(senha))
                .role(Role.ROLE_CLIENTE) // Define a Role restrita de cliente
                .build();

        // 3. Geração da Carteira com Token Estático de 6 dígitos
        String token = gerarTokenUnico();

        Carteira carteira = Carteira.builder()
                .saldo(BigDecimal.ZERO)
                .usuario(usuario)
                .tokenAutorizacao(token)
                .build();

        // 4. Vincula a carteira ao usuário (relacionamento bidirecional)
        usuario.setCarteira(carteira);

        // O CascadeType.ALL na entidade Usuario salvará a Carteira automaticamente
        return userRepository.save(usuario);
    }

    @Transactional
    public Usuario cadastrarOperador(String nome, String cpf, String senha, Role role) {
        if (userRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado em nosso sistema.");
        }

        Usuario operador = Usuario.builder()
                .nome(nome)
                .cpf(cpf)
                .senha(passwordEncoder.encode(senha))
                .role(role)
                .build();

        // Gera a carteira e o token de 6 dígitos para o operador também
        String token = gerarTokenUnico();
        Carteira carteira = Carteira.builder()
                .saldo(BigDecimal.ZERO)
                .usuario(operador)
                .tokenAutorizacao(token)
                .build();

        operador.setCarteira(carteira);

        return userRepository.save(operador);
    }

    /**
     * Gera um token numérico de exatos 6 dígitos (100000 a 999999).
     * Garante unicidade checando contra o banco de dados.
     */
    private String gerarTokenUnico() {
        String token;
        boolean tokenJaExiste;

        do {
            // Gera um número aleatório entre 100000 e 999999
            int numeroAleatorio = 100000 + secureRandom.nextInt(900000);
            token = String.valueOf(numeroAleatorio);

            // Vai ao banco checar se por azar alguém já tem esse token
            tokenJaExiste = carteiraRepository.existsByTokenAutorizacao(token);
        } while (tokenJaExiste);

        return token;
    }
}
