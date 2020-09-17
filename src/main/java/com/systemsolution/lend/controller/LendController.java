package com.systemsolution.lend.controller;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systemsolution.lend.resource.LendMovementResource;
import com.systemsolution.lend.resource.LendResource;
import com.systemsolution.lend.resource.PlanPaidResource;
import com.systemsolution.commons.wrapper.ApiResult;
import com.systemsolution.lend.converter.LendConverter;
import com.systemsolution.lend.service.LendService;
import com.systemsolution.model.LendMovements;

import com.systemsolution.saving.service.PDFService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/lends")
public class LendController {

	@Autowired
	LendService lendService;
	
	@Autowired
	LendConverter lendConverter;
	
	@Autowired
	PDFService pdfService;
	
	@GetMapping
	@PreAuthorize("hasAnyRole('LEND', 'ADMIN')")
	public ResponseEntity<ApiResult<List<LendResource>>> getLends(){
		return ResponseEntity.ok(new ApiResult<List<LendResource>>(lendService
				.getLends()
				.stream()
				.map(lendConverter::convert)
				.collect(Collectors.toList()), "Ok"));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResult<LendResource>> getLend(@PathVariable Long id){
		return ResponseEntity.ok(new ApiResult<LendResource>(lendService
				.findBy(id)
				.map(lendConverter::convert)
				.get()
				, "Ok"));
	}
	
	@PostMapping
	@PreAuthorize("hasAnyRole('LEND_CREATE','ADMIN')")
	public ResponseEntity<ApiResult<LendResource>> createLend(@RequestBody LendResource lendResource){
		return  ResponseEntity.ok(new ApiResult<LendResource>(lendConverter
				.convert(lendService.createLend(lendResource)), "Ok."));
	}
	
	
	@PostMapping("/payments")
	@PreAuthorize("hasAnyRole('LEND_MOVEMENT','ADMIN')")
	public ResponseEntity<ApiResult<LendMovements>> createLend(@RequestBody LendMovementResource paymentResource){
		return  ResponseEntity.ok(new ApiResult<LendMovements>(lendService.setLendPaid(paymentResource), "Ok."));
	}
	
	@GetMapping("/movements/{id}")
	public ResponseEntity<ApiResult<List<LendMovements>>> getMovements(@PathVariable Long id){
		return  ResponseEntity.ok(new ApiResult<List<LendMovements>>(lendService.getMovements(id), "Ok."));
	}
	
	@GetMapping("/plan/{id}")
	public ResponseEntity<ApiResult<List<PlanPaidResource>>> getPlanPaid(@PathVariable Long id){
		return ResponseEntity.ok(new ApiResult<List<PlanPaidResource>>(lendService
				.getPlanPaid(id)
				, "Ok"));
	}
	
	@PostMapping("/export/pdf/{id}")
	public ResponseEntity<InputStreamResource> exportPDF(@PathVariable Long id) throws Exception {
		ByteArrayInputStream stream = pdfService.exportPdfPlan(id);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition",
				"attachment; filename=".concat("Plan de Pagos.pdf"));
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));

	}
	
	@DeleteMapping("/cancel/{id}")
	@PreAuthorize("hasAnyRole('LEND_CANCEL','ADMIN')")
	public ResponseEntity<ApiResult<String>> cancelLend(@PathVariable Long id){
		lendService.cancelLend(id);
		return ResponseEntity.ok(new ApiResult<String>("Canceled", "ok"));
	}
	
}
