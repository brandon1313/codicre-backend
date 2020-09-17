package com.systemsolution.lend.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.systemsolution.lend.resource.LendResource;
import com.systemsolution.model.Lend;

@Service
public class LendConverter implements Converter<Lend, LendResource> {

	@Override
	public LendResource convert(Lend source) {
		return LendResource.builder()
				.approbedAmmount(source.getApprobedAmmount())
				.approbedDate(source.getApprobedDate())
				.clientId(source.getClient().getId())
				.balance(source.getBalance())
				.firstPaidDate(source.getFirstPaidDate())
				.nextPaidDate(source.getNextPaidDate())
				.interest(source.getInterest())
				.periodicity(source.getPeriodicity())
				.timeUnit(source.getTimeUnit())
				.movements(source.getMovements())
				.updatedBy(source.getUpdatedBy())
				.creditNumber(source.getCreditNumber())
				.createdBy(source.getCreatedBy())
				.id(source.getId())
				.lendTerm(source.getLendTerm())
				.mininumSubscription(source.getMininumSubscription())
				.status(source.getStatus())
				.build();
	}

}
