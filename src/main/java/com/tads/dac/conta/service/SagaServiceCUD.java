
package com.tads.dac.conta.service;

import com.tads.dac.conta.exception.changeGerenteException;
import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.GerenciadoGerenteSagaInsertDTO;
import com.tads.dac.conta.DTOs.GerenteNewOldDTO;
import com.tads.dac.conta.DTOs.RemoveGerenteDTO;
import com.tads.dac.conta.exception.ContaConstraintViolation;
import com.tads.dac.conta.mensageria.ConsumerContaSync;
import com.tads.dac.conta.mensageria.ProducerContaSync;
import com.tads.dac.conta.modelCUD.ContaCUD;
import com.tads.dac.conta.repositoryCUD.ContaRepositoryCUD;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SagaServiceCUD {
    
    @Autowired
    private ContaRepositoryCUD rep;
    
    @Autowired
    private ProducerContaSync contaSync;
    
    @Autowired
    private ModelMapper mapper;
    
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

    public void rollbackUpdate(RemoveGerenteDTO dto) {
        for(Long id : dto.getContas()){
            Optional<ContaCUD> con = rep.findById(id);
            con.get().setIdGerente(dto.getGerenteIdOld());
            con.get().setNomeGerente(dto.getGerenteNameOld());
            rep.save(con.get());
        }
        
        contaSync.syncUpdateGerenteRollback(dto);
        
    }
    
    
    public ContaDTO saveAutocadastro(Long idCliente) throws ContaConstraintViolation{
        try{
            ContaCUD savConta = new ContaCUD();
            savConta.setSaldo(BigDecimal.ZERO);
            savConta.setSituacao("E");
            savConta.setLimite(BigDecimal.ZERO);
            savConta.setIdCliente(idCliente);
            savConta.setIdConta(null);
            savConta.setDataAproRep(null);
            savConta.setDataCriacao(null);
            
            savConta = rep.save(savConta);
            
            ContaDTO dto = mapper.map(savConta, ContaDTO.class);
            contaSync.syncConta(dto);
            
            return dto;
        }catch(DataIntegrityViolationException e){
            SQLException ex = ((ConstraintViolationException) e.getCause()).getSQLException();
            String campo = ex.getMessage();
            campo = campo.substring(campo.indexOf("(") + 1, campo.indexOf(")"));
            throw new ContaConstraintViolation("Esse " + campo + " já existe!");
        }
    }
    
    
    public void rollbackAutocadastro(Long id){
        rep.deleteById(id);
        contaSync.rollbackAutocadastro(id);
    }

    public GerenciadoGerenteSagaInsertDTO changeGerente(GerenciadoGerenteSagaInsertDTO dto) throws changeGerenteException {
        try{
            Optional<ContaCUD> contaOp = rep.findById(dto.getIdConta());
            if(contaOp.isPresent()){
                ContaCUD conta = contaOp.get();

                dto.setGerenteIdOld(conta.getIdGerente());
                dto.setGerenteNomeOld(conta.getNomeGerente());

                conta.setIdGerente(dto.getGerenteIdNew());
                conta.setNomeGerente(dto.getGerenteNomeNew());

                rep.save(conta);
            }
            return dto;
        }catch(Exception e){
            System.out.println("Erro na mudança do Gerente:" + e.getMessage());
            throw new changeGerenteException("Não Foi Possível Realizar a Operação");
        }
    }
}
