
package com.tads.dac.conta.service;

import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.ContaSituacaoDTO;
import com.tads.dac.conta.exception.ClienteNotFoundException;
import com.tads.dac.conta.exception.ContaConstraintViolation;
import com.tads.dac.conta.exception.NegativeSalarioException;
import com.tads.dac.conta.exception.SituacaoInvalidaException;
import com.tads.dac.conta.model.Conta;
import com.tads.dac.conta.repository.ContaRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ContaServiceImp{

    @Autowired
    private ContaRepository rep;
    
    public Conta save(Long idCliente) throws ContaConstraintViolation{
        try{
            Conta savConta = new Conta();
            savConta.setSaldo(BigDecimal.ZERO);
            savConta.setSituacao("E");
            savConta.setLimite(BigDecimal.ZERO);
            savConta.setIdCliente(idCliente);
            savConta.setIdConta(null);
            savConta.setDataAproRep(null);
            savConta.setDataCriacao(null);
            return rep.save(savConta);
        }catch(DataIntegrityViolationException e){
            SQLException ex = ((ConstraintViolationException) e.getCause()).getSQLException();
            String campo = ex.getMessage();
            campo = campo.substring(campo.indexOf("(") + 1, campo.indexOf(")"));
            throw new ContaConstraintViolation("Esse " + campo + " já existe!");
        }
    }
    
    
    public Conta updateSituacao(ContaSituacaoDTO dto) throws ClienteNotFoundException, SituacaoInvalidaException{
        Optional<Conta> conta = rep.findById(dto.getContaId());
        if(conta.isPresent()){
            Conta ct = conta.get();
            Date dt = Date.from(Instant.now());
            if("A".equals(dto.getSituacao())){
                ct.setSituacao("A");
                ct.setLimite(dto.getSalario().divide(BigDecimal.valueOf(2)));
                ct.setDataAproRep(dt);
                ct = rep.save(ct);
                return ct;
            }else if("R".equals(dto.getSituacao())){
                ct.setSituacao("R");
                ct.setDataAproRep(dt);
                ct = rep.save(ct);
                return ct;
            }
            throw new SituacaoInvalidaException("Esse Estado de Situação Não Existe");
        }else{
            throw new ClienteNotFoundException("O Cliente Com Essa Conta Não Existe");
        }
    }

    
    public Conta updateLimite(Long contaId, BigDecimal salario) throws ClienteNotFoundException, NegativeSalarioException{
        if(salario.compareTo(BigDecimal.ONE) < 1){
            throw new NegativeSalarioException("O Salário do Cliente deve ser Maior que R$1");
        }
        Optional<Conta> conta = rep.findById(contaId);
        if(conta.isPresent()){
            Conta ct = conta.get();
            BigDecimal saldo = ct.getSaldo();
            BigDecimal limite = salario.divide(BigDecimal.valueOf(2)); //Calcula limite
            
            if(saldo.compareTo(BigDecimal.ZERO) < 0){ // Se o saldo for negativo, ou seja menor q 0
                //multiplica por -1, pra ficar positivo, pra poder comparar com o limite
                saldo = saldo.multiply(new BigDecimal("-1")); 
                if(saldo.compareTo(limite) > 0){ // Verifica se o saldo (negativo) é maior que o limite
                    limite = saldo; // Coloca o saldo (negativo) como novo limite
                }
            }
            ct.setLimite(limite);
            ct = rep.save(ct);
            return ct;
        }else{
            throw new ClienteNotFoundException("O Cliente com essa conta não existe");
        }
    }
    
    
    public Conta getById(Long id) throws ClienteNotFoundException {
        Optional<Conta> conta = rep.findById(id);
        if(conta.isPresent()){
            return conta.get();
        }
        
        throw new ClienteNotFoundException("O Cliente com essa conta não existe");
    }
    
    public List<Conta> getAllSituacaoEsperando(){
        List<Conta> contas = rep.findAllBySituacao("E");
        return contas;
    }
    
}
