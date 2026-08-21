package luna.kauan.festajuninapiraporinha.controller;

import lombok.RequiredArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.dtos.CreateCashierRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.CreateStallRequest;
import luna.kauan.festajuninapiraporinha.domain.enums.Role;
import luna.kauan.festajuninapiraporinha.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;

    @PostMapping("/barracas")
    public ResponseEntity<Void> createStall(@RequestBody CreateStallRequest request) {
        usuarioService.cadastrarOperador(request.stallName(), request.responsibleCpf(), request.password(), Role.ROLE_BARRACA);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/caixas")
    public ResponseEntity<Void> createCashier(@RequestBody CreateCashierRequest request) {
        usuarioService.cadastrarOperador(request.cashierId(), request.operatorCpf(), request.password(), Role.ROLE_CAIXA);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
