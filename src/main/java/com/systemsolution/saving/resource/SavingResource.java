package com.systemsolution.saving.resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.systemsolution.model.SavingMovement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingResource {
	private Long id;
	private String accountNumber;
	private Date createdDate;
	private Long clientId;
	private String createdBy;
	private List<SavingMovement> movements;
	private BigDecimal balance;
}
