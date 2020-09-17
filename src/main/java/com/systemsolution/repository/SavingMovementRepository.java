package com.systemsolution.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemsolution.model.Saving;
import com.systemsolution.model.SavingMovement;

@Repository
public interface SavingMovementRepository extends JpaRepository<SavingMovement, Long> {
	List<SavingMovement> findByDateBetweenAndSavingAccount(Date dateFrom, Date dateUntil, Saving saving);
}
