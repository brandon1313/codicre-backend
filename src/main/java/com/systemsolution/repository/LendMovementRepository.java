package com.systemsolution.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemsolution.model.Lend;
import com.systemsolution.model.LendMovements;

@Repository
public interface LendMovementRepository extends JpaRepository<LendMovements, Long> {
           List<LendMovements> findByLend(Lend lend);
}
