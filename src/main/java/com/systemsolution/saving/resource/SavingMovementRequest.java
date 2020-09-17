package com.systemsolution.saving.resource;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingMovementRequest {
	private Long savingId;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-6")
	private Date dateFrom;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-6")
	private Date dateTo;
}
