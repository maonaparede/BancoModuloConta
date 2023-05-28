
package com.tads.dac.conta.mensageria;

import com.tads.dac.conta.DTOs.GerenciadoSaldoDTO;
import java.math.BigDecimal;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerGerenteSaldoSync {
    
    @Autowired
    private AmqpTemplate template;
    
    public void syncClienteSaldo(Long idCliente, BigDecimal saldo, Long idGerente){
        GerenciadoSaldoDTO dto = new GerenciadoSaldoDTO(idCliente, saldo, idGerente);
        template.convertAndSend("gerente", dto);
    }
}
