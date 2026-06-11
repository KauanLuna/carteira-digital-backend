package luna.kauan.festajuninapiraporinha.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import luna.kauan.festajuninapiraporinha.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // A chave secreta deve ser injetada via variável de ambiente no deploy (Railway/Render)
    @Value("${api.security.token.secret:festa_julhina_secret_dev_only}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("carteira-julhina-api")
                    .withSubject(usuario.getCpf()) // Usamos o CPF como chave de identificação
                    .withClaim("role", usuario.getRole().name()) // Embutimos a Role no token para facilitar o frontend
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("carteira-julhina-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return ""; // Se o token for inválido, expirado ou adulterado, retorna vazio
        }
    }

    // Tempo de expiração de 12 horas (suficiente para cobrir a duração de um dia de festa)
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("-03:00"));
    }
}
