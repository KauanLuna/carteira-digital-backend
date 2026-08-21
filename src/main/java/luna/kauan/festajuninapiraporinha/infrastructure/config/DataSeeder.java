package luna.kauan.festajuninapiraporinha.infrastructure.config;

import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.domain.enums.Role;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se o Caixa principal já existe para não duplicar
            if (userRepository.findByCpf("00000000000").isEmpty()) {
                Usuario caixaAdmin = Usuario.builder()
                        .nome("Caixa Principal")
                        .cpf("00000000000")
                        .senha(passwordEncoder.encode("123456")) // Senha numérica rápida[cite: 1]
                        .role(Role.ROLE_CAIXA)
                        .build();

                userRepository.save(caixaAdmin);
                System.out.println("✅ Usuário CAIXA PRINCIPAL gerado com sucesso! CPF: 00000000000 / Senha: 123456");
            }

            if (userRepository.findByCpf("99999999999").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .cpf("99999999999")
                        .senha(passwordEncoder.encode("admin123")) // Senha de administrador
                        .role(Role.ROLE_ADMIN)
                        .build();

                userRepository.save(admin);
                System.out.println("Admin Criado com sucesso!");
            }
        };
    }
}
