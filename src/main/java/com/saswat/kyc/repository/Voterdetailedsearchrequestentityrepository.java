package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Voterdetailedsearchrequestentity;

@Repository
public interface Voterdetailedsearchrequestentityrepository extends JpaRepository<Voterdetailedsearchrequestentity, String> {

}