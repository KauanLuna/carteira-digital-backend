package luna.kauan.festajuninapiraporinha.repository;

import luna.kauan.festajuninapiraporinha.domain.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
}
