package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Dlnumberbasedrequestentity;

@Repository
public interface Dlnumberbasedrequestentityrepository extends JpaRepository<Dlnumberbasedrequestentity, String> {

}