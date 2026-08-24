package luna.kauan.festajuninapiraporinha.controller;

import luna.kauan.festajuninapiraporinha.domain.dtos.CadastroRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.LoginBarracaRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.LoginRequest;
import luna.kauan.festajuninapiraporinha.domain.dtos.TokenResponse;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import luna.kauan.festajuninapiraporinha.repository.UserRepository;
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
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.cpf(), request.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String cpfAutenticado = auth.getName();

        Usuario usuario = userRepository.findByCpf(cpfAutenticado)
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar usuário autenticado."));

        var token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponse(token, usuario.getNome()));
    }

    @PostMapping("/login/barraca")
    public ResponseEntity<TokenResponse> loginBarraca(@RequestBody LoginBarracaRequest request) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.nome(), request.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // O UserDetailsService retorna um username que pode ser CPF ou nome; usar auth.getName() e buscar por CPF primeiro, depois por nome
        String principalName = auth.getName();

        Usuario usuario = userRepository.findByCpf(principalName).orElse(null);
        if (usuario == null) {
            usuario = userRepository.findByNome(principalName);
        }

        if (usuario == null) {
            throw new RuntimeException("Falha ao recuperar usuário autenticado.");
        }

        var token = tokenService.gerarTokenBarraca(usuario);

        return ResponseEntity.ok(new TokenResponse(token, usuario.getNome()));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody CadastroRequest request) {
        usuarioService.cadastrarCliente(request.nome(), request.cpf(), request.senha());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}