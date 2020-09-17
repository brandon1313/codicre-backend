package com.systemsolution.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.systemsolution.commons.serializers.MoneySerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "lends")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Lend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String creditNumber;

	@OneToOne
	@JoinColumn(name = "client_id")
	@JsonIgnoreProperties("lend")
	private Client client;

	private BigDecimal approbedAmmount;

	private Integer lendTerm;

	private BigDecimal interest;

	@Enumerated(EnumType.STRING)
	@Column(name = "time_unit_measure")
	private TimeUnits timeUnit;

	@Enumerated(EnumType.STRING)
	private PeriodicityUnits periodicity;

	private BigDecimal mininumSubscription;

	private Date approbedDate;

	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;
	
	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal balance;

	@LastModifiedBy
	@Column(name = "updated_by")
	private String updatedBy;

	@OneToMany(mappedBy = "lend", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "lend", allowSetters = true)
	private List<LendMovements> movements;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-6")
	private Date nextPaidDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-6")
	private Date firstPaidDate;
	
	@Enumerated(EnumType.STRING)
	private LendStatus status;

	public enum PeriodicityUnits {
		MONTHLY(30.4167), WEEKLY(7), DAILY(1), BIWEEKLY(15.2083);

		private double days;

		private PeriodicityUnits(double days) {
			this.days = days;
		}

		public double getDays() {
			return days;
		}
	}

	public enum TimeUnits {
		MONTH(30.4167), YEAR(365);

		private double days;

		private TimeUnits(double days) {
			this.days = days;
		}

		public double getDays() {
			return days;
		}
	}

	public enum LendStatus{
		ACTIVE,PAID,CANCELED
	}
}
