package com.systemsolution.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "clients")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String address;

	private String email;

	@NotBlank(message = "document id cannot be null")
	private String documentId;

	@NotBlank(message = "phoneNumber cannot be null")
	private String phoneNumber;

	private String code;

	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;
	

	@OneToOne
	@JoinColumn(name = "saving_id")
	@JsonIgnoreProperties("client")
	private Saving savingAccount;
	
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
    
    private boolean active;
}
