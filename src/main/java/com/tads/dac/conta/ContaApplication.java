package com.tads.dac.conta;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContaApplication {
    
        @Bean
        public static ModelMapper modelMapper(){
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return mapper;
        }

	public static void main(String[] args) {
		SpringApplication.run(ContaApplication.class, args);
	}

}
