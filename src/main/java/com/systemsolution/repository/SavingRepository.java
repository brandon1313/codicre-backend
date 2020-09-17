package com.systemsolution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemsolution.model.Client;
import com.systemsolution.model.Saving;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Long> {

	Optional<Saving> findByClient(Client client);
}
