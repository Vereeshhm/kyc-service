package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.PanExtractionApiLog;


@Repository
public interface PanExtractionApiLogRepository extends JpaRepository<PanExtractionApiLog, String>{

}
