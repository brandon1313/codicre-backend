package com.systemsolution.saving.resource;

import java.math.BigDecimal;
import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingMovementResource {

	private Long id;
	
	private Long savingId;
	
	private BigDecimal retirement;
	
	private BigDecimal saving;
	
	private Date date;
	
	private String createdBy;
	
	private String movementType;
	
	private String description;
}
