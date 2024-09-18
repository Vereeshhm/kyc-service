package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.PANDataApiLog;


@Repository
public interface PANDataApiLogRepository extends JpaRepository<PANDataApiLog, Long> {

}