
package com.tads.dac.conta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_conta")
public class Conta implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idConta;
    
    @Column(nullable = false)
    private BigDecimal saldo;
    
    @Column(nullable = false)
    private BigDecimal limite;
    
    @Column(columnDefinition="CHAR(1)", nullable = false)
    private String situacao;
       
    @Column(name = "id_cliente", unique = true, nullable = false)
    private Long idCliente;
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date dataCriacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAproRep;   

    @Column(columnDefinition = "VARCHAR(36)")
    private UUID saga;
}
