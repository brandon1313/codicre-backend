package com.systemsolution.lend.resource;

import java.math.BigDecimal;
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
public class PlanPaidResource {
	private int payNumber;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-6")
	private Date payDate;
	private BigDecimal capital;
	private BigDecimal interest;
	private BigDecimal quota;
	private BigDecimal capitalBalance;
	private String name;
	private String dpi;
	private String phoneNumber;
	private String address;
	private BigDecimal ammount;
	private String lendTerm;
	private String periodicity;
	private BigDecimal percentageInterest;
	private Date approbedDate;
	private Date firstPaidDate;
	private String creditNumber;
	
	
	
	
	@Override
	public String toString() {
		return "PlanPaidResource [payNumber=" + payNumber + ", payDate=" + payDate + ", capital=" + capital
				+ ", interest=" + interest + ", quota=" + quota + ", capitalBalance=" + capitalBalance + "]";
	}
}
