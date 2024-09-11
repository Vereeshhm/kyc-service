package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Voterverificationrequestentity;

@Repository
public interface Voterverificationrequestentityrepository extends JpaRepository<Voterverificationrequestentity, String> {

}
