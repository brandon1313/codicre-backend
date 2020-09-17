package com.systemsolution.lend.resource;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendMovementResource {
	
	private Long lendId;
	
	private BigDecimal debit;
	
	private String description;

}
