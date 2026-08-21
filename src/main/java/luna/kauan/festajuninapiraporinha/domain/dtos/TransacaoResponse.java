package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(String idTransacao, BigDecimal valor, String tipo, LocalDateTime dataHora, String nomeCliente, String nomeOperador) {
}
