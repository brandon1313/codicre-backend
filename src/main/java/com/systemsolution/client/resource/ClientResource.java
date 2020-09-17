package com.systemsolution.client.resource;


import com.systemsolution.saving.resource.SavingResource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResource {

	private Long id;

	private String name;
	
	private String address;
	
	private String email;
	
	private String documentId;
	
	private String phoneNumber;
	
	private String code;
	
	private String createdBy;
	
	private String updatedBy;
	
	private boolean active;
	
	private SavingResource savingAccount;
}
