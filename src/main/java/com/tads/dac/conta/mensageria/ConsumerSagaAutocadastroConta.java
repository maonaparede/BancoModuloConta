
package com.tads.dac.conta.mensageria;

import com.tads.dac.conta.DTOs.ClienteEndDTO;
import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.MensagemDTO;
import com.tads.dac.conta.exception.ContaConstraintViolation;
import com.tads.dac.conta.service.SagaServiceCUD;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerSagaAutocadastroConta {
    
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private AmqpTemplate template; 
    
    @Autowired
    private SagaServiceCUD serv;
    

    @RabbitListener(queues = "auto-conta-saga")
    public void commitOrdem(@Payload MensagemDTO msg) {
        ClienteEndDTO dto = mapper.map(msg.getSendObj(), ClienteEndDTO.class);
        
        try {
            ContaDTO contaDto = serv.saveAutocadastro(dto.getId());
            msg.setSendObj(contaDto);
        } catch (ContaConstraintViolation ex) {
            msg.setMensagem(ex.getMessage());
        }
        
        template.convertAndSend("auto-conta-saga-receive", msg);
    }

    @RabbitListener(queues = "auto-conta-saga-rollback")
    public void rollbackOrdem(@Payload MensagemDTO msg) {
        ContaDTO dto = mapper.map(msg.getSendObj(), ContaDTO.class);
        serv.rollbackAutocadastro(dto.getIdCliente());
    }    
}
