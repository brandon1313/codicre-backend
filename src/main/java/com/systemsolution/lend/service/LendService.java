package com.systemsolution.lend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.systemsolution.client.service.ClientService;
import com.systemsolution.lend.resource.LendMovementResource;
import com.systemsolution.lend.resource.LendResource;
import com.systemsolution.lend.resource.PlanPaidResource;
import com.systemsolution.model.Client;
import com.systemsolution.model.Lend;
import com.systemsolution.model.Lend.LendStatus;
import com.systemsolution.model.Lend.PeriodicityUnits;
import com.systemsolution.model.Lend.TimeUnits;
import com.systemsolution.model.LendMovements;
import com.systemsolution.repository.LendMovementRepository;
import com.systemsolution.repository.LendRepository;

import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class LendService {

	private static final String PRESTAMO_NO_ENCONTRADO = "Prestamo no encontrado";

	@Autowired
	private LendRepository lendRepository;
	
	@Autowired
	LendMovementRepository lendMovementRepository;
	
	@Autowired
	private ClientService clientService;
	
	@Value("${codicre.penalty}")
	private int penaltyAmmount;
	
	public List<Lend> getLends(){
		return lendRepository.findAll()
				.stream()
				.filter(x -> nonNull(x.getStatus()))
				.collect(Collectors.toList());
	}
	
	public Optional<Lend> findBy(Long id) {
		return lendRepository.findById(id);
	}
	
	public Lend createLend(LendResource lendResource, Long id) {
		final Client client = clientService.getClient(lendResource.getClientId())
				.orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado."));
		final Integer lendTerm = getLendTerm(lendResource.getPeriodicity(), lendResource.getTimeUnit(), lendResource.getLendTerm());
		
		BigDecimal minimumSubscription = getMininumSubscription(lendResource.getApprobedAmmount(),
				calculatePercentage(lendResource.getInterest(), lendResource.getApprobedAmmount(), 
				lendResource.getPeriodicity()), lendTerm);
		
		if(lendRepository.findByClientAndStatus(client, LendStatus.ACTIVE).isPresent()) {
			throw new RuntimeException("Cliente ya tiene un prestamo activo");
		}
		
		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final String accountFormat = "%s0000%s-%s%s";
		if(isNull(id)) {
			Lend lend = Lend.builder()
					.approbedAmmount(lendResource.getApprobedAmmount())
					.approbedDate(new Date())
					.client(client)
					.interest(lendResource.getInterest())
					.creditNumber(String.format(accountFormat, year, client.getId() , Calendar.getInstance().get(Calendar.MONTH) + 1,Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ))
					.lendTerm(lendResource.getLendTerm())
					.mininumSubscription(minimumSubscription)
					.periodicity(lendResource.getPeriodicity())
					.timeUnit(lendResource.getTimeUnit())
					.status(LendStatus.ACTIVE)
					.firstPaidDate(lendResource.getFirstPaidDate())
					.nextPaidDate(lendResource.getFirstPaidDate())
					.balance(lendResource.getApprobedAmmount())
					.build();
			return lendRepository.save(lend);
		}
		
		return null;
	}
	
	
	public List<LendMovements> getMovements(Long id){
		Lend lend = findBy(id)
				.orElseThrow(() -> new EntityNotFoundException(PRESTAMO_NO_ENCONTRADO));
		return lendMovementRepository.findByLend(lend);
	}
	
	public List<PlanPaidResource> getPlanPaid(Long id){
		Lend lend = findBy(id)
				.orElseThrow(() -> new EntityNotFoundException(PRESTAMO_NO_ENCONTRADO));
		List<PlanPaidResource> planPaidResources = new ArrayList<>();
		BigDecimal interest = calculatePercentage(lend.getInterest(), lend.getApprobedAmmount(), lend.getPeriodicity());
		Integer paids = getLendTerm(lend.getPeriodicity(), lend.getTimeUnit(), lend.getLendTerm());
		
		Date nextPaidDate = lend.getFirstPaidDate();
		BigDecimal capitalBalance = lend.getApprobedAmmount();
		for(int i = 1; i <= paids; i++) {
			capitalBalance = capitalBalance.subtract(lend.getMininumSubscription().subtract(interest));
			PlanPaidResource paid = new PlanPaidResource();
			paid.setAddress(lend.getClient().getAddress());
			paid.setName(lend.getClient().getName());
			paid.setDpi(lend.getClient().getDocumentId());
			paid.setPhoneNumber(lend.getClient().getPhoneNumber());
			paid.setAmmount(lend.getApprobedAmmount().setScale(2, BigDecimal.ROUND_UP));
			paid.setLendTerm(String.format("%s %s", lend.getLendTerm(), getTimeUnit(lend.getTimeUnit())));
			paid.setPeriodicity(getPeridicity(lend.getPeriodicity()));
			paid.setPercentageInterest(lend.getInterest().setScale(1, BigDecimal.ROUND_UP));
			paid.setApprobedDate(lend.getApprobedDate());
			paid.setFirstPaidDate(lend.getFirstPaidDate());
			paid.setCapital(lend.getMininumSubscription().subtract(interest).setScale(2, BigDecimal.ROUND_UP));
			paid.setCapitalBalance(capitalBalance.setScale(2, BigDecimal.ROUND_UP));
			paid.setQuota(lend.getMininumSubscription().setScale(2, BigDecimal.ROUND_UP));
			paid.setCreditNumber(lend.getCreditNumber());
			paid.setPayNumber(i);
			paid.setInterest(interest.setScale(2, BigDecimal.ROUND_UP));
			paid.setPayDate(nextPaidDate);
			nextPaidDate = getNextPaidDate(nextPaidDate, lend.getPeriodicity());
			planPaidResources.add(paid);
			log.info("{}",paid);
		}
		return planPaidResources;
	}
	
	public void cancelLend(Long id) {
		Lend lend = findBy(id)
				.orElseThrow(() -> new EntityNotFoundException(PRESTAMO_NO_ENCONTRADO));
		lend.setStatus(LendStatus.CANCELED);
		lendRepository.save(lend);
	}
	
	public String getTimeUnit(TimeUnits timeUnits) {
		switch (timeUnits) {
		case MONTH:
			return "MESES(ES)";
		
		case YEAR:
			return "AÑO(S)";
			
		default:
			break;
		}
		return null;
	}
	public String getPeridicity(PeriodicityUnits quantity) {
		switch (quantity) {
		case BIWEEKLY:
			return "CATORCENAL";
			
		case DAILY:
			return "DIARIO";
			
		case MONTHLY:
			return "MENSUAL";
			
		case WEEKLY:
			return "SEMANAL";
			
			
		default:
			break;
		}
		return null;
	}
	public LendMovements setLendPaid(LendMovementResource source) {

		Lend lend = findBy(source.getLendId())
				.orElseThrow(() -> new EntityNotFoundException(PRESTAMO_NO_ENCONTRADO));

		BigDecimal ammount = source.getDebit();
		BigDecimal balance = getBalance(lend);
		BigDecimal interest = calculatePercentage(lend.getInterest(), lend.getApprobedAmmount(), lend.getPeriodicity());
		BigDecimal penalty = BigDecimal.ZERO;
		ammount = ammount.subtract(interest);
		
		if (new Date().after(lend.getNextPaidDate())) {
			penalty = BigDecimal.valueOf(penaltyAmmount);
			ammount = ammount.subtract(penalty);
		}
		
		if(ammount.compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("La cantidad ingresada, no es suficiente para cubrir los gastos de mora e intereses");
		}
		
		if(balance.add(interest).subtract(ammount).compareTo(lend.getMininumSubscription()) < 0) {
			lend.setMininumSubscription(balance.subtract(ammount).add(interest));
		}
		
		if(ammount.compareTo(balance.add(interest)) > 0) {
			throw new RuntimeException("El monto a pagar es mayor a la deuda");
		}
		
		Date nextPaidDate = getNextPaidDate(lend.getNextPaidDate(), lend.getPeriodicity());
		lend.setNextPaidDate(nextPaidDate);
		lend.setBalance(balance.subtract(ammount).add(interest));
		if(balance.subtract(ammount).compareTo(BigDecimal.ZERO) <= 0 || balance.subtract(ammount).compareTo(BigDecimal.ONE) <= 0 ) {
			lend.setBalance(BigDecimal.ZERO);
			lend.setStatus(LendStatus.PAID);
		}
		
		lendRepository.save(lend);
		
		
		LendMovements lendMovements = LendMovements
				.builder()
				.debit(ammount)
				.interest(interest)
				.penalty(penalty)
				.description(source.getDescription())
				.lend(lend)
				.date(new Date())
				.balance(balance.subtract(ammount))
				.build();
		
		return lendMovementRepository.save(lendMovements);
	}
		
	public BigDecimal getBalance(final Lend source) {
		BigDecimal payments = new BigDecimal(0);
		BigDecimal ammount = source.getApprobedAmmount();
		
		if (nonNull(source.getMovements())) {
			payments = BigDecimal
					.valueOf(source.getMovements().stream().mapToDouble(x -> x.getDebit().doubleValue()).sum());
		}
			
		return  ammount.subtract(payments);
	}
	public Lend createLend(LendResource lendResource) {
		return createLend(lendResource, null);
	}
	
    private BigDecimal calculatePercentage(BigDecimal percentaje, BigDecimal quantity, PeriodicityUnits periodicityUnits) {
        return percentaje.multiply(quantity)
        		.divide(BigDecimal.valueOf(100))
        		.divide(BigDecimal.valueOf(interestDivisor(periodicityUnits)));
    }
    
	private Date getNextPaidDate(final Date date, final PeriodicityUnits periodicity ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int daysToAdd = 0;
		switch (periodicity) {
		case DAILY:
			daysToAdd = 1;
			calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
			break;
		case BIWEEKLY:
			//setBiweeklyPaid(calendar);
			daysToAdd = 14;
			calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
			break;
		case MONTHLY:
			
			calendar.add(Calendar.MONTH, 1);
			break;
		case WEEKLY:
			daysToAdd = 7;
			calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
			break;
		default:
			break;
		}
		
		return calendar.getTime();
	}

	public void setBiweeklyPaid(Calendar calendar) {
		if (calendar.get(Calendar.DAY_OF_MONTH) <= 15 && calendar.get(Calendar.DAY_OF_MONTH) >= 12) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else if (calendar.get(Calendar.DAY_OF_MONTH) > 15) {
			calendar.set(Calendar.DAY_OF_MONTH, 15);
			calendar.add(Calendar.MONTH, 1);
		} else {
			calendar.set(Calendar.DAY_OF_MONTH, 15);
		}
	}

	/**
	 * 
	 * @param periodicity
	 * @param timeUnit
	 * @param lendTerm
	 * @return total of paids
	 */
	public Integer getLendTerm(final PeriodicityUnits periodicity, final TimeUnits timeUnit, final Integer lendTerm) {
		return BigDecimal.valueOf(lendTerm).multiply(BigDecimal.valueOf(timeUnit.getDays()))
				.divide(BigDecimal.valueOf(periodicity.getDays()), 2 ,RoundingMode.HALF_UP)
				.intValue();
	}
	
	/**
	 * 
	 * @param periodicityUnits
	 * @return quantity of quotas to divide the interest
	 */
	private Integer interestDivisor(PeriodicityUnits periodicityUnits) {
		return (int) (TimeUnits.MONTH.getDays() / periodicityUnits.getDays());
	}
	
	private BigDecimal getMininumSubscription(BigDecimal ammount, BigDecimal interest, int lendTerm) {
		return ammount.divide(BigDecimal.valueOf(lendTerm), 2 ,RoundingMode.HALF_EVEN).add(interest);
	}
}

