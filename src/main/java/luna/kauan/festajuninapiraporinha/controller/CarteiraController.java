package luna.kauan.festajuninapiraporinha.controller;

import luna.kauan.festajuninapiraporinha.domain.dtos.SaldoResponse;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carteira")
@RequiredArgsConstructor
public class CarteiraController {

    private final UserRepository userRepository;

    @GetMapping("/saldo")
    public ResponseEntity<SaldoResponse> obterSaldo() {
        // Recupera o CPF do usuário autenticado no contexto do Spring Security
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String cpfUsuarioLogado = usuarioLogado.getCpf();

        Usuario usuario = userRepository.findByCpf(cpfUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // O usuário consulta seu saldo e visualiza o token numérico de autorização
        SaldoResponse response = new SaldoResponse(
                usuario.getCarteira().getSaldo(),
                usuario.getCarteira().getTokenAutorizacao()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/saldo/reembolso")
    public ResponseEntity<SaldoResponse> obterSaldoReembolso(@RequestParam String cpfReembolso) {
        Usuario usuario = userRepository.findByCpf(cpfReembolso)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // O usuário consulta seu saldo e visualiza o token numérico de autorização
        SaldoResponse response = new SaldoResponse(
                usuario.getCarteira().getSaldo(),
                usuario.getCarteira().getTokenAutorizacao()
        );

        return ResponseEntity.ok(response);
    }
}