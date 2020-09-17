package com.systemsolution.user.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdministratorRequest {

	private String userName;
	private String password;
	private String module;
}
