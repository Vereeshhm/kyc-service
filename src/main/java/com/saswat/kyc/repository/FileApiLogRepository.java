package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.FileApiLog;


@Repository
public interface FileApiLogRepository  extends JpaRepository<FileApiLog, String>{

}
