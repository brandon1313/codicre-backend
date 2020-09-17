package com.systemsolution.commons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResult<T> {
	private T resultObject;
	private String message;

}
