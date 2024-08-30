package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.kyc.model.AadharEntity;

public interface AadharRepository extends JpaRepository<AadharEntity, String> {

}