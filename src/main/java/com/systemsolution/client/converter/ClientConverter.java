package com.systemsolution.client.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.systemsolution.client.resource.ClientResource;
import com.systemsolution.model.Client;
import com.systemsolution.saving.converter.SavingConverter;
import static java.util.Objects.isNull;

@Service
public class ClientConverter implements Converter<Client, ClientResource> {

	@Autowired
	private SavingConverter savingConverter;
	
	@Override
	public ClientResource convert(Client source) {
		return ClientResource.builder()
				.address(source.getAddress())
				.active(source.isActive())
				.code(source.getCode())
				.phoneNumber(source.getPhoneNumber())
				.createdBy(source.getCreatedBy())
				.email(source.getEmail())
				.id(source.getId())
				.name(source.getName())
				.updatedBy(source.getUpdatedBy())
				.documentId(source.getDocumentId())
				.savingAccount(isNull(source.getSavingAccount())? null : savingConverter.convert(source.getSavingAccount()))
				.build();
	}

}
