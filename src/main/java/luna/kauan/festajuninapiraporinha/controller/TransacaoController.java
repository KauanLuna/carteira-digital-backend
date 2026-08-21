package luna.kauan.festajuninapiraporinha.controller;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.dtos.DebitoRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.DepositoRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.ReembolsoRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.TransacaoResponse;
import luna.kauan.festajuninapiraporinha.domain.entity.Transacao;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.repository.CarteiraRepository;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import luna.kauan.festajuninapiraporinha.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final UserRepository userRepository;
    private final CarteiraRepository carteiraRepository;

    // --- ROTA DE BARRACA E CAIXA ---

    @PostMapping("/debitar")
    public ResponseEntity<TransacaoResponse> debitar(@RequestBody DebitoRequest request) {
        UUID idOperador = obterIdUsuarioLogado();

        Transacao transacao = transacaoService.debitarConsumoBarraca(
                request.tokenAutorizacao(),
                request.valor(),
                idOperador
        );

        return ResponseEntity.ok(mapearParaResponse(transacao));
    }

    // --- ROTAS EXCLUSIVAS DE CAIXA ---

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponse> depositar(@RequestBody DepositoRequest request) {
        UUID idOperador = obterIdUsuarioLogado();
        Transacao transacao = transacaoService.depositar(request.cpfCliente(), request.valor(), idOperador);
        return ResponseEntity.ok(mapearParaResponse(transacao));
    }

    @PostMapping("/reembolsar")
    public ResponseEntity<TransacaoResponse> reembolsar(@RequestBody ReembolsoRequest request) {
        UUID idOperador = obterIdUsuarioLogado();
        Transacao transacao = transacaoService.reembolsar(request.cpfCliente(), idOperador);
        return ResponseEntity.ok(mapearParaResponse(transacao));
    }

    @GetMapping("/barraca")
    public ResponseEntity<List<TransacaoResponse>> buscarTransacoesBarraca() {
        UUID idOperador = obterIdUsuarioLogado();
        List<Transacao> transacaoList = transacaoService.buscarTransacoesBarraca(idOperador);
        return ResponseEntity.ok(mapearParaResponse(transacaoList));
    }

    @GetMapping("/caixa")
    public ResponseEntity<List<TransacaoResponse>> buscarTransacoesCaixa() {
        UUID idOperador = obterIdUsuarioLogado();
        List<Transacao> transacaoList = transacaoService.buscarTransacoesCaixa(idOperador);
        return ResponseEntity.ok(mapearParaResponse(transacaoList));
    }

    @GetMapping("/convidado")
    public ResponseEntity<List<TransacaoResponse>> buscarTransacoesConvidado() {
        UUID idOperador = obterIdUsuarioLogado();
        List<Transacao> transacaoList = transacaoService.buscarTransacoesConvidado(idOperador);
        return ResponseEntity.ok(mapearParaResponse(transacaoList));
    }

    // --- MÉTODOS AUXILIARES ---

    private UUID obterIdUsuarioLogado() {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String cpfLogado = usuarioLogado.getCpf();

        return userRepository.findByCpf(cpfLogado)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado."));
    }

    private TransacaoResponse mapearParaResponse(Transacao t) {
        String nomeCliente = (t.getCarteira() != null && t.getCarteira().getUsuario() != null)
                ? t.getCarteira().getUsuario().getNome()
                : "Cliente";

        String nomeOperador = t.getOperador() != null
                ? t.getOperador().getNome()
                : "Operador";

        return new TransacaoResponse(
                t.getId().toString(),
                t.getValor(),
                t.getTipo().name(),
                t.getDataHora(),
                nomeCliente,
                nomeOperador
        );
    }

    private List<TransacaoResponse> mapearParaResponse(List<Transacao> transacaoList) {
        List<TransacaoResponse> responseList = new ArrayList<>();

        for (Transacao t : transacaoList) {
            String nomeCliente = (t.getCarteira() != null && t.getCarteira().getUsuario() != null)
                    ? t.getCarteira().getUsuario().getNome()
                    : "Cliente";

            String nomeOperador = t.getOperador() != null
                    ? t.getOperador().getNome()
                    : "Operador";

            responseList.add(
                    new TransacaoResponse(
                            t.getId().toString(),
                            t.getValor(),
                            t.getTipo().name(),
                            t.getDataHora(),
                            nomeCliente,
                            nomeOperador
                    )
            );
        }

        return responseList;
    }
}
