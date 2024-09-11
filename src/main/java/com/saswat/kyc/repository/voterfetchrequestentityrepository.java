package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.voterfetchrequestentity;
@Repository
public interface voterfetchrequestentityrepository extends JpaRepository<voterfetchrequestentity, String> {

}
