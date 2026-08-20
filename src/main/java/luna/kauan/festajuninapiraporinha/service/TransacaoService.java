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
import java.util.List;
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

    @Transactional
    public Transacao depositar(String cpfCliente, BigDecimal valorRecarga, UUID idOperadorCaixa) {
        if (valorRecarga.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor de recarga deve ser maior que zero.");
        }

        Usuario operador = usuarioRepository.findById(idOperadorCaixa)
                .orElseThrow(() -> new IllegalArgumentException("Operador de caixa não encontrado."));

        Usuario cliente = usuarioRepository.findByCpf(cpfCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Carteira carteira = cliente.getCarteira();

        // Adiciona o saldo
        carteira.setSaldo(carteira.getSaldo().add(valorRecarga));
        carteiraRepository.save(carteira);

        // Gera transação de auditoria
        Transacao transacao = Transacao.builder()
                .carteira(carteira)
                .valor(valorRecarga)
                .tipo(TipoTransacao.DEPOSITO)
                .operador(operador)
                .build();

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao reembolsar(String cpfCliente, UUID idOperadorCaixa) {
        Usuario operador = usuarioRepository.findById(idOperadorCaixa)
                .orElseThrow(() -> new IllegalArgumentException("Operador de caixa não encontrado."));

        Usuario cliente = usuarioRepository.findByCpf(cpfCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Carteira carteira = cliente.getCarteira();
        BigDecimal saldoRestante = carteira.getSaldo();

        if (saldoRestante.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("O cliente já possui saldo zero.");
        }

        // Zera o saldo da carteira para o reembolso
        carteira.setSaldo(BigDecimal.ZERO);
        carteiraRepository.save(carteira);

        // Registra o débito de fechamento
        Transacao transacao = Transacao.builder()
                .carteira(carteira)
                .valor(saldoRestante)
                .tipo(TipoTransacao.REEMBOLSO)
                .operador(operador)
                .build();

        return transacaoRepository.save(transacao);
    }

    public List<Transacao> buscarTransacoesBarraca(UUID idOperador) {
        Usuario operador = usuarioRepository.findById(idOperador)
                .orElseThrow(() -> new IllegalArgumentException("Operador da barraca não encontrado."));

        return transacaoRepository.findByOperador(operador);
    }
}
