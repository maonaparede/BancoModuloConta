
package com.tads.dac.conta.mensageria;

import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.GerenteNewOldDTO;
import com.tads.dac.conta.DTOs.MensagemDTO;
import com.tads.dac.conta.DTOs.OperacaoBdDTO;
import com.tads.dac.conta.DTOs.RemoveGerenteDTO;
import com.tads.dac.conta.modelR.ContaR;
import com.tads.dac.conta.modelR.OperacaoR;
import com.tads.dac.conta.repositoryR.ContaRepositoryR;
import com.tads.dac.conta.repositoryR.OperacaoRepositoryR;
import com.tads.dac.conta.service.ContaService;
import com.tads.dac.conta.service.SagaServiceR;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerContaSync {
    
    @Autowired
    private ContaRepositoryR rep;
    
    @Autowired
    private OperacaoRepositoryR repOp;
            
    @Autowired
    private ModelMapper mapper;
    
    @Autowired
    private SagaServiceR serv;
    
    @RabbitListener(queues = "conta")
    public void enviaSyncConta(@Payload ContaDTO dto){
        ContaR conta = mapper.map(dto, ContaR.class);
        rep.save(conta);
    }
    
    @RabbitListener(queues = "operacao")
    public void enviaSyncDeposito(@Payload OperacaoBdDTO dto){
        OperacaoR conta = mapper.map(dto, OperacaoR.class);
        if(conta.getDeUser() != null){
            rep.save(conta.getDeUser());
        }
        if(conta.getParaUser() != null){
            rep.save(conta.getParaUser());
        }
        repOp.save(conta);
    }

    @RabbitListener(queues = "conta-gerente")
    public void enviaSyncContaGerente(@Payload GerenteNewOldDTO dto){
        rep.updateGerenteIdNome(dto.getIdOld(), dto.getNomeNew(), dto.getIdNew());
    }

    @RabbitListener(queues = "conta-gerente-rollback")
    public void rollbackOrdem(@Payload RemoveGerenteDTO dto){
        serv.rollback(dto);
    }    
}
