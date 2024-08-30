package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saswat.kyc.model.voterverificationlog;

public interface voterverificationlogrepository extends JpaRepository<voterverificationlog, String> {

}
