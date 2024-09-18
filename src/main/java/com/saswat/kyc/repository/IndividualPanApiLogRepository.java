package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.IndividualPanApiLog;


@Repository
public interface IndividualPanApiLogRepository extends JpaRepository<IndividualPanApiLog, String> {

}
