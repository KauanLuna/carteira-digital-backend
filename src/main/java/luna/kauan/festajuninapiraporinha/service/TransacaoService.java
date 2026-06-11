package luna.kauan.festajuninapiraporinha.service;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.entity.Carteira;
import luna.kauan.festajuninapiraporinha.domain.entity.Transacao;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.domain.enums.TipoTransacao;
import luna.kauan.festajuninapiraporinha.repository.CarteiraRepository;
import luna.kauan.festajuninapiraporinha.repository.TransacaoRepository;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final CarteiraRepository carteiraRepository;
    private final TransacaoRepository transacaoRepository;
    private final UserRepository usuarioRepository;

    @Transactional
    public Transacao debitarConsumoBarraca(String tokenAutorizacao, BigDecimal valorCobrado, UUID idOperador) {

        // 1. Busca quem está operando a barraca (garantia de rastreabilidade)
        Usuario operador = usuarioRepository.findById(idOperador)
                .orElseThrow(() -> new IllegalArgumentException("Operador da barraca não encontrado."));

        // 2. Busca e TRAVA a linha da carteira no banco usando o token numérico
        Carteira carteira = carteiraRepository.findByTokenAutorizacaoForUpdate(tokenAutorizacao)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido. Verifique o código com o cliente."));

        // 3. Verifica Regra de Negócio (Evita saldo negativo)
        if (carteira.getSaldo().compareTo(valorCobrado) < 0) {
            throw new IllegalStateException("Saldo insuficiente para a transação.");
        }

        // 4. Executa a dedução do saldo
        carteira.setSaldo(carteira.getSaldo().subtract(valorCobrado));
        carteiraRepository.save(carteira);

        // 5. Gera log da transação (Auditoria Imutável)
        Transacao novaTransacao = Transacao.builder()
                .carteira(carteira)
                .valor(valorCobrado)
                .tipo(TipoTransacao.DEBITO)
                .operador(operador)
                .build();

        return transacaoRepository.save(novaTransacao);
    }
}
