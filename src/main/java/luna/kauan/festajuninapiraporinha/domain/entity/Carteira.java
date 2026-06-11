package luna.kauan.festajuninapiraporinha.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "carteiras")
@Builder
public class Carteira {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    // Token numérico exigido para rápida identificação na barraca
    @Column(name = "token_autorizacao", length = 6, unique = true)
    private String tokenAutorizacao;

    // Controle de Concorrência Otimista (JPA)
    @Version
    private Long version;

    public Carteira(UUID id, BigDecimal saldo, Usuario usuario, String tokenAutorizacao, Long version) {
        this.id = id;
        this.saldo = saldo;
        this.usuario = usuario;
        this.tokenAutorizacao = tokenAutorizacao;
        this.version = version;
    }

    public Carteira() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTokenAutorizacao() {
        return tokenAutorizacao;
    }

    public void setTokenAutorizacao(String tokenAutorizacao) {
        this.tokenAutorizacao = tokenAutorizacao;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
