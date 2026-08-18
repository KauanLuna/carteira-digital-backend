package luna.kauan.festajuninapiraporinha.domain.dtos;

import java.math.BigDecimal;

public record ClienteBuscaResponse(String nome, String cpf, BigDecimal saldoOculto) {
}
