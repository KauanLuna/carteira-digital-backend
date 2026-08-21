package luna.kauan.festajuninapiraporinha.controller;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.dtos.ClienteBuscaResponse;
import luna.kauan.festajuninapiraporinha.domain.entity.Carteira;
import luna.kauan.festajuninapiraporinha.repository.CarteiraRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CarteiraRepository carteiraRepository;

    @GetMapping("/buscar/cpf")
    public ResponseEntity<ClienteBuscaResponse> buscarPorCpf(@RequestParam String cpf) {
        Carteira carteira = carteiraRepository.findByUsuario_Cpf(cpf);

        if (carteira == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ClienteBuscaResponse(
                carteira.getUsuario().getNome(),
                "***.***.***-**",
                null
        ));
    }

    @GetMapping("/buscar/token")
    public ResponseEntity<ClienteBuscaResponse> buscarPorToken(@RequestParam String token) {
        Carteira carteira = carteiraRepository.findByTokenAutorizacao(token)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada com esse token"));

        return ResponseEntity.ok(new ClienteBuscaResponse(
                carteira.getUsuario().getNome(),
                "***.***.***-**",
                null
        ));
    }
}