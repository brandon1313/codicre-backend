package com.systemsolution.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "savings")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Saving {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String accountNumber;

	@OneToOne
	@JoinColumn(name = "client_id")
	@JsonIgnoreProperties("savingAccount")
	private Client client;
	
	private Date createdDate;
	
	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;
	
		
	@OneToMany(mappedBy = "savingAccount", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = "savingAccount", allowSetters = true)
	private List<SavingMovement> movements;
}
