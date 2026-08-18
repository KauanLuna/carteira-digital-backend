package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;

public record SaldoResponse(BigDecimal saldo, String tokenAutorizacao) {
}
