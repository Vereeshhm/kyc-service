package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Passportverificationrequestentity;

@Repository
public interface Passportverificationrequestentityrepository extends JpaRepository<Passportverificationrequestentity, String> {

}
