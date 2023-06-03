
package com.tads.dac.conta.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GerenciadoGerenteDTO {
    private Long idConta;
    private Long gerenteId;
    private String gerenteNome;
}
