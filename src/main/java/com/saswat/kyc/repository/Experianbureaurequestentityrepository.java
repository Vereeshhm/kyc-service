package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Experianbureaurequestentity;

@Repository
public interface Experianbureaurequestentityrepository extends JpaRepository<Experianbureaurequestentity, String> {

}
