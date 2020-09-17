package com.systemsolution.saving.service;

import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systemsolution.client.service.ClientService;
import com.systemsolution.model.Client;
import com.systemsolution.model.Saving;
import com.systemsolution.model.SavingMovement;
import com.systemsolution.model.SavingMovement.MovementType;
import com.systemsolution.repository.SavingMovementRepository;
import com.systemsolution.repository.SavingRepository;
import com.systemsolution.saving.resource.SavingMovementResource;

@Service
public class SavingService {

	
	private static final String CLIENT_DOESNT_HAVE_ACCOUNT_ASOCIATED = "El cliente no tiene una cuenta asociada";
	private static final String CLIENT_NOT_FOUND = "Cliente no encontrado";
	private static final String YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";

	@Autowired
	private SavingRepository savingRepository;
	
	@Autowired 
	private SavingMovementRepository savingMovementRepository;
	
	@Autowired
	private ClientService clientService; 
	
	public List<Saving> getSavings(){
		return savingRepository.findAll();
	}
	
	public Optional<Saving> getSavingAccount(final Long clientId){
        Client client = clientService.getClient(clientId)
        		.orElseThrow(() -> new EntityNotFoundException(CLIENT_NOT_FOUND));
		return savingRepository.findByClient(client);
	}
	
	public Saving createAccount(final Long clientId){
		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final String accountFormat = "%s0000%s";
		
        Client client = clientService.getClient(clientId)
        		.orElseThrow(() -> new EntityNotFoundException(CLIENT_NOT_FOUND));
        Saving saving = Saving.builder()
        		.client(client)
        		.accountNumber(String.format(accountFormat, year, client.getId()))
        		.createdDate(new Date())
        		.build();
        client.setSavingAccount(saving);
        savingRepository.saveAndFlush(saving);
        clientService.saveClient(client);
        return saving;
	}
	
	public SavingMovement registerNewMovement(final SavingMovementResource movementResource) {
		MovementType movementType = MovementType.valueOf(movementResource.getMovementType());
		Saving saving = savingRepository.findById(movementResource.getSavingId())
				.orElseThrow(() -> new RuntimeException(CLIENT_DOESNT_HAVE_ACCOUNT_ASOCIATED));
		
		BigDecimal retirements = new BigDecimal(0);
		BigDecimal savings = new BigDecimal(0);
		if(movementType.equals(MovementType.RETIREMENT)) {
			retirements = movementResource.getRetirement();
		}else {
			savings = movementResource.getSaving();
		}
		
		BigDecimal balance = getBalance(saving).subtract(retirements).add(savings);
		
		SavingMovement movement = SavingMovement.builder()
				.date(new Date())
				.movementType(movementType)
				.savingAccount(saving)
				.saving(savings)
				.retirement(retirements)
				.description(movementResource.getDescription())
				.balance(balance)
				.build();
		
		return savingMovementRepository.save(movement);
	}
	
	
	
	public BigDecimal getBalance(final Saving source) {
		BigDecimal retirements = new BigDecimal(0);
		BigDecimal savings = new BigDecimal(0);
		
		if (nonNull(source.getMovements())) {
			retirements = BigDecimal
					.valueOf(source.getMovements().stream().mapToDouble(x -> x.getRetirement().doubleValue()).sum());
			savings = BigDecimal
					.valueOf(source.getMovements().stream().mapToDouble(x -> x.getSaving().doubleValue()).sum());
		}
			
		return  savings.subtract(retirements);
	}
	public List<SavingMovement> getMovements(Date dateFrom, Date dateUntil, Long savingId) throws ParseException{
		if(dateFrom.after(dateUntil)) {
			throw new RuntimeException("La fecha desde no puede ser mayor a la fecha hasta");
		}
		
		Saving saving = savingRepository.findById(savingId)
				.orElseThrow(() -> new RuntimeException(CLIENT_DOESNT_HAVE_ACCOUNT_ASOCIATED));
		SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD);
		String dateFromString = format.format(dateFrom);
		String dateUntilString = format.format(dateUntil).replace("00:00:00","23:59:59");
				return savingMovementRepository.findByDateBetweenAndSavingAccount(format.parse(dateFromString), format.parse(dateUntilString), saving);
	}
}
