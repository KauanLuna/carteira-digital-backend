package luna.kauan.festajuninapiraporinha.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import luna.kauan.festajuninapiraporinha.domain.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Impede instanciação vazia fora de frameworks
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador; // Quem realizou a cobrança (Barraca) ou a recarga (Caixa)

    public UUID getId() {
        return id;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Usuario getOperador() {
        return operador;
    }

    @Builder
    public Transacao(Carteira carteira, BigDecimal valor, TipoTransacao tipo, Usuario operador) {
        this.carteira = carteira;
        this.valor = valor;
        this.tipo = tipo;
        this.operador = operador;
        this.dataHora = LocalDateTime.now();
    }
}
