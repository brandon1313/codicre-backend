package com.systemsolution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemsolution.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
      Optional<Client> findByDocumentId(String documentId);
}
