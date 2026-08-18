package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;

public record DepositoRequest(String cpfCliente, BigDecimal valor) {
}
