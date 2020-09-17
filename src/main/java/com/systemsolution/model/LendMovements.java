package com.systemsolution.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.systemsolution.commons.serializers.MoneySerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "lend_movements")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LendMovements {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "lend_id")
	@JsonIgnoreProperties({ "client", "movements" })
	private Lend lend;

	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal debit;

	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal interest;
	
	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal penalty;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-6")
	private Date date;
	
	private String description;
	
	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal balance;
	
	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;

}
