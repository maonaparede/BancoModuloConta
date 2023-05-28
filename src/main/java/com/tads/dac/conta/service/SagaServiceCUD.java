
package com.tads.dac.conta.service;

import com.tads.dac.conta.DTOs.GerenteNewOldDTO;
import com.tads.dac.conta.DTOs.RemoveGerenteDTO;
import com.tads.dac.conta.mensageria.ConsumerContaSync;
import com.tads.dac.conta.mensageria.ProducerContaSync;
import com.tads.dac.conta.modelCUD.ContaCUD;
import com.tads.dac.conta.repositoryCUD.ContaRepositoryCUD;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SagaServiceCUD {
    
    @Autowired
    private ContaRepositoryCUD rep;
    
    @Autowired
    private ProducerContaSync contaSync;
    
    public RemoveGerenteDTO substituteGerenteConta(GerenteNewOldDTO dto) throws Exception{
        try {
           List<ContaCUD> contas = rep.findByIdGerente(dto.getIdOld());
           List<Long> idContas = new ArrayList<>();
           for (ContaCUD conta : contas) {
               idContas.add(conta.getIdConta());
           }
          

           RemoveGerenteDTO ret = new RemoveGerenteDTO();
           ret.setGerenteIdNew(dto.getIdNew());
           ret.setGerenteIdOld(dto.getIdOld());
           
           if(contas != null){
                if(!contas.isEmpty()){
                    ret.setGerenteNameOld(contas.get(0).getNomeGerente());
                }
           }
           
           ret.setContas(idContas);
           
           rep.updateGerenteIdNome(dto.getIdOld(), dto.getNomeNew(), dto.getIdNew());
           
           contaSync.syncUpdateGerente(dto);
           return ret;
        }catch(Exception e){
            throw new Exception("Houve Algum Erro Na Mudança(Exclusão) de Gerentes no Módulo Conta");
        }
    }

    public void rollback(RemoveGerenteDTO dto) {
        for(Long id : dto.getContas()){
            Optional<ContaCUD> con = rep.findById(id);
            con.get().setIdGerente(dto.getGerenteIdOld());
            con.get().setNomeGerente(dto.getGerenteNameOld());
            rep.save(con.get());
        }
        
        contaSync.syncUpdateGerenteRollback(dto);
        
    }
    
}
