
package com.tads.dac.conta.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GerenteNewOldDTO {
    
    private Long idOld;
    
    private Long idNew;
    private String nomeNew;    
}
