
package com.tads.dac.conta.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "tb_operacao")
public class Operacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @CreationTimestamp
    private Date dataTempo;
    
    private BigDecimal valor;
    @Column(columnDefinition="CHAR(1)")
    private String operacao;
    
    @ManyToOne
    @JoinColumn(name = "de_user")
    private Conta deUser;
    
    @ManyToOne
    @JoinColumn(name = "para_user")
    private Conta paraUser;
  
    
}
