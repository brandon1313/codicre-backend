package com.systemsolution.saving.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

import java.math.BigDecimal;

import com.systemsolution.model.Saving;
import com.systemsolution.saving.resource.SavingResource;

@Service
public class SavingConverter implements Converter<Saving, SavingResource> {

	@Override
	public SavingResource convert(Saving source) {
	
		BigDecimal retirements = new BigDecimal(0);
		BigDecimal savings = new BigDecimal(0);
		
		if (nonNull(source.getMovements())) {
			retirements = BigDecimal
					.valueOf(source.getMovements().stream().mapToDouble(x -> x.getRetirement().doubleValue()).sum());
			savings = BigDecimal
					.valueOf(source.getMovements().stream().mapToDouble(x -> x.getSaving().doubleValue()).sum());
		}
		 
		BigDecimal balance = savings.subtract(retirements);
		
		return SavingResource.builder()
				.accountNumber(source.getAccountNumber())
				.clientId(source.getClient().getId())
				.createdBy(source.getCreatedBy())
				.id(source.getId())
				.movements(source.getMovements())
				.balance(balance)
				.build();
	}

}
