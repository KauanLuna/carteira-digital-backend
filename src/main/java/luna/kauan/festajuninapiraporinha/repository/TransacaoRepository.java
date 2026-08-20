package luna.kauan.festajuninapiraporinha.repository;

import luna.kauan.festajuninapiraporinha.domain.entity.Transacao;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
    List<Transacao> findByOperador(Usuario operador);
}
