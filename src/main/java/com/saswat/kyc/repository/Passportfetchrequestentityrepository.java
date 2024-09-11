package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Passportfetchrequestentity;

@Repository
public interface Passportfetchrequestentityrepository extends JpaRepository<Passportfetchrequestentity, String>{

}
