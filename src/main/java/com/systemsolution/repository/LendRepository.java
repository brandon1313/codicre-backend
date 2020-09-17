package com.systemsolution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemsolution.model.Lend;
import com.systemsolution.model.Lend.LendStatus;
import com.systemsolution.model.Client;

@Repository
public interface LendRepository extends JpaRepository<Lend, Long>{
         Optional<Lend> findByClientAndStatus(Client client, LendStatus lendStatus );
}
