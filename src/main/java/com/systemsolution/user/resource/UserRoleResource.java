package com.systemsolution.user.resource;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UserRoleResource {
	private String name;
	private Long userId;

	public UserRoleResource(String name, Long userId) {
		super();
		this.name = name;
		this.userId = userId;
	}

}
