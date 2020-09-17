package com.systemsolution.saving.controller;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.systemsolution.commons.wrapper.ApiResult;
import com.systemsolution.model.SavingMovement;
import com.systemsolution.saving.converter.SavingConverter;
import com.systemsolution.saving.resource.SavingMovementRequest;
import com.systemsolution.saving.resource.SavingMovementResource;
import com.systemsolution.saving.resource.SavingResource;
import com.systemsolution.saving.service.PDFService;
import com.systemsolution.saving.service.SavingService;

@RestController
@RequestMapping("/api/v1/savings-accounts")
@CrossOrigin(origins = "*")
public class SavingController {

	@Autowired
	private SavingService savingService;
	
	@Autowired
	private SavingConverter savingConverter;
	
	@Autowired
	private PDFService pdfService;
	
	@GetMapping
	@PreAuthorize("hasAnyRole('SAVINGS', 'ADMIN')")
	public ResponseEntity<ApiResult<List<SavingResource>>> getAccounts(){
		return ResponseEntity.ok(new ApiResult<List<SavingResource>>(savingService.getSavings()
				.stream()
				.map(savingConverter::convert)
				.collect(Collectors.toList()), "ok"));
	}
	
    @GetMapping("/clientId")
    public ResponseEntity<ApiResult<SavingResource>> getAccount(@PathVariable final Long clientId){
        return ResponseEntity.ok(new ApiResult<SavingResource>(savingService
        		.getSavingAccount(clientId)
        		.map(savingConverter::convert).get(),"Ok"));
    }
    
    
   @GetMapping("/create/{clientId}")
   @PreAuthorize("hasAnyRole('SAVINGS_CREATE', 'ADMIN')")
   public ResponseEntity<ApiResult<SavingResource>> createAccount(@PathVariable final Long clientId){
       return ResponseEntity.ok(new ApiResult<SavingResource>(savingConverter.convert(savingService
       		.createAccount(clientId)),"Ok"));
   }
   
	@PostMapping("/export/pdf")
	@PreAuthorize("hasAnyRole('SAVINGS_PDF', 'ADMIN')")
	public ResponseEntity<InputStreamResource> exportPDF(@RequestBody SavingMovementRequest movementRequest) throws Exception {
		ByteArrayInputStream stream = pdfService.exportPdf(movementRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition",
				"attachment; filename=".concat("Consulta de Movimientos.pdf"));
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));

	}
   
   @PostMapping("/movement")
   @PreAuthorize("hasAnyRole('SAVINGS_MOVEMENTS', 'ADMIN')")
   public ResponseEntity<ApiResult<SavingMovement>> registerMovement(@RequestBody final SavingMovementResource movementResource){
	   return ResponseEntity.ok(new ApiResult<SavingMovement>(savingService.registerNewMovement(movementResource), "Ok"));
   }
	
   @PostMapping("/movement/consult")
   @PreAuthorize("hasAnyRole('SAVINGS_CONSULT', 'ADMIN')")
   public ResponseEntity<ApiResult<List<SavingMovement>>> movementConsult(@RequestBody SavingMovementRequest movementRequest) throws ParseException{
	   return ResponseEntity.ok(new ApiResult<List<SavingMovement>>(savingService
			   .getMovements(movementRequest.getDateFrom(), movementRequest.getDateTo(), movementRequest.getSavingId()), "ok"));
   }
}
