package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.kyc.model.FileApiLog;

public interface FileApiLogRepository  extends JpaRepository<FileApiLog, String>{

}
