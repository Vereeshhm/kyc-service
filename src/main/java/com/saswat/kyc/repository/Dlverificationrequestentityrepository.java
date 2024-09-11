package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Dlverificationrequestentity;

@Repository
public interface Dlverificationrequestentityrepository extends JpaRepository<Dlverificationrequestentity, String>{

}
