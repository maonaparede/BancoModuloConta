
package com.tads.dac.conta.mensageria;

import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.GerenteNewOldDTO;
import com.tads.dac.conta.DTOs.OperacaoBdDTO;
import com.tads.dac.conta.DTOs.RemoveGerenteDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerContaSync {
    
    @Autowired
    private AmqpTemplate template;
    
    public void syncConta(ContaDTO dto){
        template.convertAndSend("conta", dto);
    }
    
    public void syncOperacao(OperacaoBdDTO dto){
        template.convertAndSend("operacao", dto);
    }
    
    public void syncUpdateGerente(GerenteNewOldDTO dto){
        template.convertAndSend("conta-gerente", dto);
    }
    
    public void syncUpdateGerenteRollback(RemoveGerenteDTO dto){
        template.convertAndSend("conta-gerente-rollback", dto);
    }
}
