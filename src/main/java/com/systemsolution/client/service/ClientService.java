package com.systemsolution.client.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systemsolution.client.resource.ClientResource;
import com.systemsolution.model.Client;
import com.systemsolution.repository.ClientRepository;
import static java.util.Objects.nonNull;

@Service
public class ClientService {
	
	private static final String CLIENT_DOESNT_EXISTS = "Cliente no existe";
	
	@Autowired
	private ClientRepository clientRepository;
	

	public List<Client> getClients(){
		return clientRepository.findAll()
				.stream()
				.filter(Client::isActive)
				.collect(Collectors.toList());
	}
	
	public Optional<Client> getClient(final Long id){
		return clientRepository.findById(id);
	}
	
	public Client saveClient(Client client){
		return clientRepository.save(client);
	}
	
	public Optional<Client> saveClient(final ClientResource clientResource, final Long id){
		
		if(nonNull(id)) {
			Client client = getClient(id).orElseThrow(() -> new EntityNotFoundException(CLIENT_DOESNT_EXISTS));
			client.setAddress(clientResource.getAddress());
			client.setCode(clientResource.getCode());
			client.setDocumentId(clientResource.getDocumentId());
			client.setEmail(clientResource.getEmail());
			client.setPhoneNumber(clientResource.getPhoneNumber());
			client.setName(clientResource.getName());
			client.setActive(true);
			return Optional.of(clientRepository.save(client));
		}
		
		if(clientRepository.findByDocumentId(clientResource.getDocumentId()).isPresent()) {
			throw new RuntimeException("Ya existe un cliente con el DPI ingresado.");
		}
		Client client = Client.builder()
				.address(clientResource.getAddress())
				.code(clientResource.getCode())
				.name(clientResource.getName())
				.documentId(clientResource.getDocumentId())
				.email(clientResource.getEmail())
				.phoneNumber(clientResource.getPhoneNumber())	
				.active(true)
				.build();
		return Optional.of(clientRepository.save(client));
		
	}
	
	public Optional<Client> saveClient(final ClientResource clientResource){
		return saveClient(clientResource, null);
	}
	
	public Optional<Client> inactiveClient(final Long id) {
		Client client = getClient(id).orElseThrow(() -> new EntityNotFoundException(CLIENT_DOESNT_EXISTS));
		client.setActive(false);
		return Optional.of(clientRepository.save(client));
	}
}
