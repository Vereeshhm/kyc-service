package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.voterverificationlog;


@Repository
public interface voterverificationlogrepository extends JpaRepository<voterverificationlog, String> {

}
