
package com.tads.dac.conta.controller;

import com.tads.dac.conta.DTOs.ContaDTO;
import com.tads.dac.conta.DTOs.ContaSaveDTO;
import com.tads.dac.conta.DTOs.ContaSituacaoDTO;
import com.tads.dac.conta.exception.ClienteNotFoundException;
import com.tads.dac.conta.exception.ContaConstraintViolation;
import com.tads.dac.conta.exception.NegativeSalarioException;
import com.tads.dac.conta.exception.SituacaoInvalidaException;
import com.tads.dac.conta.model.Conta;
import com.tads.dac.conta.service.ContaServiceImp;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class SistemaController {
    
    @Autowired
    ContaServiceImp contaService;
    
    @Autowired
    ModelMapper mapper;
    
    @PostMapping("/sys") 
    public ResponseEntity<?> save(@RequestBody ContaSaveDTO dtoR){
       
        try {
            Conta ct = contaService.save(dtoR.getIdCliente());
            ContaDTO dto = mapper.map(ct, ContaDTO.class);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }catch(ContaConstraintViolation e){
            String msg = e.getMessage();
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/sys")
    public ResponseEntity<?> updateSituacao(@RequestBody ContaSituacaoDTO dtoR){
       
        try {
            Conta ct = contaService.updateSituacao(dtoR);
            ContaDTO dto = mapper.map(ct, ContaDTO.class);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch(SituacaoInvalidaException e){
            String msg = e.getMessage();
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        } catch (ClienteNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/sys/{id}/{salario}")
    public ResponseEntity<?> updateLimite(@PathVariable(value = "id") Long id, @PathVariable(value = "salario") BigDecimal salario){
       
        try {
            Conta ct = contaService.updateLimite(id, salario);
            ContaDTO dto = mapper.map(ct, ContaDTO.class);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ClienteNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NegativeSalarioException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sys/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Long id){       
        try{
            Conta ct = contaService.getById(id);
            ContaDTO dto = mapper.map(ct, ContaDTO.class);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch(ClienteNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);   
        }
    }

    @GetMapping("/sys")
    public ResponseEntity<?> getAllEsperando(){       
        List<Conta> list = contaService.getAllSituacaoEsperando();
        
        List<ContaDTO> contaList = list.stream()
                .map(item -> mapper.map(item, ContaDTO.class))
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(contaList, HttpStatus.OK);
    }
    
}
