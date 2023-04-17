
package com.tads.dac.conta.repository;

import com.tads.dac.conta.model.Conta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContaRepository extends JpaRepository<Conta, Long> {
    
    public List<Conta> findAllBySituacao(String sit);
    
    public Optional<Conta> findByIdCliente(Long id);
}

