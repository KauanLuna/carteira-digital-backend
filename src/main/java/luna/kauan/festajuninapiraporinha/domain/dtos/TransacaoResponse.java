package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;

public record TransacaoResponse(String idTransacao, BigDecimal valor, String tipo, String dataHora) {
}
