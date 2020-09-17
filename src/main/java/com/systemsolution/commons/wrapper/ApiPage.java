package com.systemsolution.commons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ApiPage<T> {
	private List<T> content;
	private Long totalElements;
}
