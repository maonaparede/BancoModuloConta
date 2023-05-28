
package com.tads.dac.conta.service;

import com.tads.dac.conta.DTOs.RemoveGerenteDTO;
import com.tads.dac.conta.mensageria.ProducerContaSync;
import com.tads.dac.conta.modelCUD.ContaCUD;
import com.tads.dac.conta.modelR.ContaR;
import com.tads.dac.conta.repositoryR.ContaRepositoryR;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SagaServiceR {
    
    @Autowired
    private ContaRepositoryR rep;
    
    public void rollback(RemoveGerenteDTO dto) {
        for(Long id : dto.getContas()){
            Optional<ContaR> con = rep.findById(id);
            con.get().setIdGerente(dto.getGerenteIdOld());
            con.get().setNomeGerente(dto.getGerenteNameOld());
        }
    }
    
}
