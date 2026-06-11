package luna.kauan.festajuninapiraporinha.repository;

import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCpf(String cpf);
}
