package com.systemsolution.lend.resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.systemsolution.model.LendMovements;
import com.systemsolution.model.Lend.LendStatus;
import com.systemsolution.model.Lend.PeriodicityUnits;
import com.systemsolution.model.Lend.TimeUnits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendResource {

	private Long id;
	private Long clientId;
	
	private String creditNumber;
	
	private BigDecimal approbedAmmount;
	
	private BigDecimal balance;

	private Integer lendTerm;

	private BigDecimal interest;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-6")
	private Date nextPaidDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-6")
	private Date firstPaidDate;
	
	private TimeUnits timeUnit;
	
	private PeriodicityUnits periodicity;
	
	private BigDecimal mininumSubscription;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-6")
	private Date approbedDate;
	
	private String createdBy;
	
	private String updatedBy;
	
	private List<LendMovements> movements;
	
	private LendStatus status;
}
