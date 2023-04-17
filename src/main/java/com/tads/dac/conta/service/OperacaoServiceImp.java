
package com.tads.dac.conta.service;

import com.tads.dac.conta.DTOs.OperacaoDTO;
import com.tads.dac.conta.exception.ClienteNotFoundException;
import com.tads.dac.conta.exception.InvalidMovimentacaoException;
import com.tads.dac.conta.exception.InvalidValorException;
import com.tads.dac.conta.exception.OperacaoDoesntExist;
import com.tads.dac.conta.model.Conta;
import com.tads.dac.conta.model.Operacao;
import com.tads.dac.conta.repository.ContaRepository;
import com.tads.dac.conta.repository.OperacaoRepository;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static java.util.Date.parse;
import java.util.List;
import java.util.Optional;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperacaoServiceImp{
    
    @Autowired
    ContaRepository repConta;
    
    @Autowired
    OperacaoRepository repOp;

    
    public List<OperacaoDTO> getExtrato(Long id, Date inicio, Date fim) throws ClienteNotFoundException{
       
        List<Tuple> tuples = repOp.getExtrato(id, inicio, fim);
        
        
        List<OperacaoDTO> dtos = new ArrayList<>();
        //Mappeamento na mão
        for (Tuple tuple : tuples) {
            
            OperacaoDTO dto = new OperacaoDTO();
            dto.setId(Long.valueOf(tuple.get("id").toString()));
            dto.setDataTempo((Date) tuple.get("data_tempo"));
            dto.setValor(new BigDecimal(tuple.get("valor").toString()));
            dto.setOperacao(tuple.get("operacao").toString());
            if(tuple.get("de_user") != null){
                dto.setDeUser(Long.valueOf(tuple.get("de_user").toString()));
            }
            if(tuple.get("para_user") != null){
                dto.setParaUser(Long.valueOf(tuple.get("para_user").toString()));
            }
            
            dtos.add(dto);
            
        }
        return dtos;
    }

    
    public Operacao fazDeposito(Long contaId, BigDecimal valor) throws ClienteNotFoundException {
        Operacao op = new Operacao();
        try {
            Optional<Conta> ct = repConta.findById(contaId);
            if (!ct.isPresent()) throw new ClienteNotFoundException("O Cliente Que Faz a Transferência Não Existe.");

            op.setParaUser(ct.get());
            op.setDeUser(null);
            op.setValor(valor);
            op.setOperacao("D");
            op.setDataTempo(null);

            op = repOp.save(op);

            BigDecimal saldo = ct.get().getSaldo().add(valor); // Adiciona ao Saldo
            updateSaldo(contaId, saldo);

            return op;
            
        } catch (ClienteNotFoundException e) {
            throw new ClienteNotFoundException("O Cliente com essa conta não existe");
        }
    }

    
    public Operacao fazSaque(Long contaId, BigDecimal valor) throws ClienteNotFoundException, InvalidMovimentacaoException, InvalidValorException {
        Operacao op = new Operacao();
        try {
            Optional<Conta> ct = repConta.findById(contaId);
            if (!ct.isPresent()) throw new ClienteNotFoundException("O Cliente Requerendo o Saque Não Existe.");
            if(valor.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidValorException("O valor de Transferencia Tem Que ser Maior Que Zero");
            
            BigDecimal valorPossivel = ct.get().getSaldo().add(ct.get().getLimite()); // O Valor Máximo de Saque é Saldo + Limite

            if (valorPossivel.compareTo(valor) < 0) throw new InvalidMovimentacaoException("Seu Limite e Saldo Não Permitem Essa Operação.");         

            op.setParaUser(null);
            op.setDeUser(ct.get());
            op.setValor(valor);
            op.setOperacao("S");
            op.setDataTempo(null);

            op = repOp.save(op);

            BigDecimal saldo = ct.get().getSaldo().subtract(valor); // Subtrai do Saldo

            updateSaldo(contaId, saldo);

            return op;
        } catch (ClienteNotFoundException e) {
            throw new ClienteNotFoundException("O Cliente com essa conta não existe");
        }
    }

    public Operacao fazTransferencia(Long deId, Long paraId, BigDecimal valor) throws ClienteNotFoundException, InvalidMovimentacaoException, InvalidValorException {
        Operacao op = new Operacao();
        try {
            System.out.println("Long: " + deId + " " + paraId);
            
            Optional<Conta> ctDe = repConta.findById(deId);
            Optional<Conta> ctPara = repConta.findById(paraId);
            
            if(!ctDe.isPresent()) throw new ClienteNotFoundException("O Cliente Que Faz a Transferência Não Existe.");
            if(!ctPara.isPresent()) throw new ClienteNotFoundException("O Cliente Que Recebe a Transferência Não Existe.");
            
            if(valor.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidValorException("O valor de Transferencia Tem Que ser Maior Que Zero");

            BigDecimal valorPossivel = ctDe.get().getSaldo().add(ctDe.get().getLimite());

            if (valorPossivel.compareTo(valor) < 0) throw new InvalidMovimentacaoException("Seu Limite e Saldo Não Permitem Essa Operação.");

            op.setId(null);
            op.setParaUser(ctPara.get());
            op.setDeUser(ctDe.get());
            op.setValor(valor);
            op.setOperacao("T");
            op.setDataTempo(null); 
            
            op = repOp.save(op); // Salva Operação

            BigDecimal saldoDe = ctDe.get().getSaldo().subtract(valor); // Subtrai do remetente
            updateSaldo(deId, saldoDe);
            
            BigDecimal saldoPara = ctPara.get().getSaldo().add(valor); // Adiciona ao destinatario
            updateSaldo(paraId, saldoPara);    
            
            return op;
        } catch (ClienteNotFoundException e) {
            throw new ClienteNotFoundException("O Cliente com essa conta não existe");
        }    
    }
    
    public Conta updateSaldo(Long contaId, BigDecimal saldo) throws ClienteNotFoundException {
        Optional<Conta> conta = repConta.findById(contaId);
        if(conta.isPresent()){
            Conta ct = conta.get();
            ct.setSaldo(saldo);
            return repConta.save(ct);
        }else{
            throw new ClienteNotFoundException("O Cliente com essa conta ("+ contaId+") não existe");
        }
    }

    
}
