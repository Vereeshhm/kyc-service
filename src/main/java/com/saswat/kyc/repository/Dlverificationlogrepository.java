package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Dlverificationapilog;


@Repository
public interface Dlverificationlogrepository extends JpaRepository<Dlverificationapilog, String>{

	
}
