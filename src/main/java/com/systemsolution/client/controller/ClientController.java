package com.systemsolution.client.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systemsolution.client.converter.ClientConverter;
import com.systemsolution.client.resource.ClientResource;
import com.systemsolution.client.service.ClientService;
import com.systemsolution.commons.wrapper.ApiResult;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/clients")
public class ClientController {

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientConverter clientConverter;
	
	@GetMapping
	@PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
	public ResponseEntity<ApiResult<List<ClientResource>>> getClients(){
		return ResponseEntity.ok(new ApiResult<List<ClientResource>>(clientService
				.getClients()
				.stream()
				.map(clientConverter::convert)
				.collect(Collectors.toList()), "Ok"));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResult<ClientResource>> getClient(@PathVariable Long id){
		return ResponseEntity.ok(new ApiResult<ClientResource>(clientService
				.getClient(id)
				.map(clientConverter::convert)
				.get()
				, "Ok"));
	}
	
	@PostMapping("/create")
	@PreAuthorize("hasAnyRole('CLIENT_CREATE', 'ADMIN')")
	public ResponseEntity<ApiResult<ClientResource>> createClient(@RequestBody final ClientResource clientResource) {
		return ResponseEntity.ok(new ApiResult<ClientResource>(clientConverter.convert(clientService.saveClient(clientResource).get()), "ok"));
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasAnyRole('CLIENT_UPDATE', 'ADMIN')")
	public ResponseEntity<ApiResult<ClientResource>> updateClient(@RequestBody final ClientResource clientResource, @PathVariable("id") final Long id) {
		return ResponseEntity.ok(new ApiResult<ClientResource>(clientConverter.convert(clientService.saveClient(clientResource, id).get()), "ok"));
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyRole('CLIENT_DELETE', 'ADMIN')")
	public ResponseEntity<ApiResult<ClientResource>> deleteClient(  @PathVariable("id") final Long id) {
		return ResponseEntity.ok(new ApiResult<ClientResource>(clientConverter.convert(clientService.inactiveClient(id).get()), "ok"));
	}


}
