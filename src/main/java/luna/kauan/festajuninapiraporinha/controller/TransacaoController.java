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

    // --- MÉTODOS AUXILIARES ---

    private UUID obterIdUsuarioLogado() {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String cpfLogado = usuarioLogado.getCpf();

        return userRepository.findByCpf(cpfLogado)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado."));
    }

    private TransacaoResponse mapearParaResponse(Transacao t) {
        return new TransacaoResponse(
                t.getId().toString(),
                t.getValor(),
                t.getTipo().name(),
                t.getDataHora().toString()
        );
    }

    private List<TransacaoResponse> mapearParaResponse(List<Transacao> transacaoList) {
        List<TransacaoResponse> responseList = new ArrayList<>();

        for (Transacao t : transacaoList) {
            responseList.add(
                    new TransacaoResponse(
                    t.getId().toString(),
                    t.getValor(),
                    t.getTipo().name(),
                    t.getDataHora().toString()
                    )
            );
        }
    }
}
