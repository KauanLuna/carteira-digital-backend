package luna.kauan.festajuninapiraporinha.controller;

import luna.kauan.festajuninapiraporinha.domain.dtos.CadastroRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.LoginRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.TokenResponse;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.service.UsuarioService;
import luna.kauan.festajuninapiraporinha.infrastructure.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.cpf(), request.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody CadastroRequest request) {
        usuarioService.cadastrarCliente(request.nome(), request.cpf(), request.senha());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}