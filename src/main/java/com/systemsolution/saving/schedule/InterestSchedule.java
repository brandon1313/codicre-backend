package com.systemsolution.saving.schedule;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.systemsolution.model.Saving;
import com.systemsolution.model.SavingMovement.MovementType;
import com.systemsolution.saving.resource.SavingMovementResource;
import com.systemsolution.saving.service.SavingService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InterestSchedule {
	
	@Autowired
	private SavingService savingService;
	
	@Value("${codicre.interest.saving}")	
	private String interest;
	
	@Scheduled(cron = "0 1 0 1 * *")
	@Transactional
	public void generateInterest() {
	   log.info("Initializing batch {} - date {}", "generateInterest()", new Date());
	   List<Saving> savings = savingService.getSavings(); 
       savings.forEach(saving -> {
    	  BigDecimal balance = savingService.getBalance(saving);
    	  BigDecimal percentage = BigDecimal.valueOf(Double.valueOf(interest));
    	  log.info("Account balance {}", balance);
    	  BigDecimal amount = BigDecimal.ZERO;
    	  if(balance.compareTo(BigDecimal.ZERO) > 0) {
    		  amount = calculatePercentage(percentage , balance);
    	  }
    	  SavingMovementResource movementResource = buildMovement(amount, saving.getId());
    	  log.info("Register interest to {} - percentage %{}  - amount Q.{}", saving.getClient().getName(), interest, amount);
    	  savingService.registerNewMovement(movementResource);
       });
	}
	
	private SavingMovementResource buildMovement(BigDecimal amount, Long savingId) {
		return SavingMovementResource.builder()
				.date(new Date())
				.description("Intereses mensuales sobre ahorros.")
				.movementType(MovementType.SAVING.toString())
				.saving(amount)
				.savingId(savingId)
				.retirement(BigDecimal.ZERO)
				.build();
	}
	
    private BigDecimal calculatePercentage(BigDecimal obtained, BigDecimal total) {
        return obtained.multiply(total).divide(BigDecimal.valueOf(100));
    }

}
