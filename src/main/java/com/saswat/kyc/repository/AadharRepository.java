package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.AadharEntity;


@Repository
public interface AadharRepository extends JpaRepository<AadharEntity, String> {

}