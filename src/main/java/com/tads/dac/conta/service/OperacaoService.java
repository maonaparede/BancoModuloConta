
package com.tads.dac.conta.service;

import com.tads.dac.conta.exception.ClienteNotFoundException;
import com.tads.dac.conta.exception.InvalidMovimentacaoException;
import com.tads.dac.conta.exception.InvalidValorException;
import com.tads.dac.conta.exception.OperacaoDoesntExist;
import com.tads.dac.conta.model.Conta;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public interface OperacaoService {
    /*
    Operacao fazDeposito(Long contaId, BigDecimal valor) throws ClienteNotFoundException, InvalidMovimentacaoException, InvalidValorException;
    
    Operacao fazSaque(Long contaId, BigDecimal valor) throws ClienteNotFoundException, InvalidMovimentacaoException, InvalidValorException;
    
    Operacao fazTransferencia(Long deId, Long paraId, BigDecimal valor) throws ClienteNotFoundException, InvalidMovimentacaoException, InvalidValorException;
    
    List<Operacao> getExtrato(Long id, Date inicio, Date fim) throws ClienteNotFoundException;
    
    Conta updateSaldo(Long contaId, BigDecimal valor) throws ClienteNotFoundException;
*/
}
