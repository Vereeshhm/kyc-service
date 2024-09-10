package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Individualpandetails;

@Repository
public interface Individualpandetailsrepository extends JpaRepository<Individualpandetails, String>{

}
