package com.digitalbanking.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfig {

	
	@Bean
	public ModelMapper modelMapper() {
		var modelMapper= new ModelMapper();
		// Enabling loose matching helps ModelMapper associate 
        // "senderAccount.accountNumber" with "senderAccountNumber"
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
	}
}
