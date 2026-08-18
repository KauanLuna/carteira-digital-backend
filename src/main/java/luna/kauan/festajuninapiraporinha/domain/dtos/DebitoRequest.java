package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;

public record DebitoRequest(String tokenAutorizacao, BigDecimal valor) {
}
