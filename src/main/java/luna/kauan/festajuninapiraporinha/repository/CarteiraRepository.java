package luna.kauan.festajuninapiraporinha.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import luna.kauan.festajuninapiraporinha.domain.entity.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, UUID> {
    // Lock pessimista acionado no momento da leitura do saldo da carteira
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "3000") // Timeout de 3 segundos
    })
    @Query("SELECT c FROM Carteira c WHERE c.tokenAutorizacao = :token")
    Optional<Carteira> findByTokenAutorizacaoForUpdate(String token);

    // NOVO MÉTODO: Para ser usado APENAS PARA LEITURA visual (no UsuarioController)
    Optional<Carteira> findByTokenAutorizacao(String tokenAutorizacao);

    Boolean existsByTokenAutorizacao(String token);
}
