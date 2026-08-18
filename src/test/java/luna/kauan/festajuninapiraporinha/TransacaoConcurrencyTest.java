package luna.kauan.festajuninapiraporinha;

import luna.kauan.festajuninapiraporinha.domain.entity.Carteira;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.domain.enums.Role;
import luna.kauan.festajuninapiraporinha.repository.CarteiraRepository;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import luna.kauan.festajuninapiraporinha.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransacaoConcurrencyTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    private Usuario operador;
    private Carteira carteiraCliente;

    @BeforeEach
    void setup() {
        // Prepara os dados de teste no banco em memória (H2) ou banco de testes
        operador = userRepository.save(Usuario.builder()
                .nome("Operador Barraca")
                .cpf("00000000000")
                .senha("senha123")
                .role(Role.ROLE_BARRACA)
                .build());

        Usuario cliente = userRepository.save(Usuario.builder()
                .nome("Cliente Teste")
                .cpf("11111111111")
                .senha("senha123")
                .role(Role.ROLE_CLIENTE)
                .build());

        carteiraCliente = carteiraRepository.save(Carteira.builder()
                .usuario(cliente)
                .saldo(new BigDecimal("50.00")) // Saldo inicial de R$ 50
                .tokenAutorizacao("123456")
                .build());
    }

    @Test
    void deveImpedirGastoDuploComRequisicoesSimultaneas() throws InterruptedException {
        int numeroDeRequisicoesSimultaneas = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeRequisicoesSimultaneas);
        CountDownLatch latch = new CountDownLatch(1); // Trava para soltar todas as threads juntas
        CountDownLatch doneLatch = new CountDownLatch(numeroDeRequisicoesSimultaneas); // Trava para aguardar o fim

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        BigDecimal valorCobrado = new BigDecimal("50.00"); // Tentando gastar tudo de uma vez

        for (int i = 0; i < numeroDeRequisicoesSimultaneas; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Aguarda o sinal para começar
                    transacaoService.debitarConsumoBarraca(carteiraCliente.getTokenAutorizacao(), valorCobrado, operador.getId());
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    falhas.incrementAndGet(); // Se deu erro (saldo insuficiente ou lock timeout), conta como falha
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); // Libera todas as threads no exato mesmo instante
        doneLatch.await(); // Aguarda todas terminarem

        // Busca o saldo final da carteira atualizado no banco
        Carteira carteiraAtualizada = carteiraRepository.findById(carteiraCliente.getId()).orElseThrow();

        // Validações Críticas
        // 1. Apenas UMA transação pode ter sucesso
        assertEquals(1, sucessos.get(), "Apenas uma transação deve ser aprovada.");

        // 2. As outras duas DEVEM falhar (saldo insuficiente após a primeira)
        assertEquals(2, falhas.get(), "As outras transações devem falhar.");

        // 3. O saldo final DEVE ser 0.00 (e não negativo)
        assertEquals(0, carteiraAtualizada.getSaldo().compareTo(BigDecimal.ZERO), "O saldo não pode ficar negativo.");
    }
}
