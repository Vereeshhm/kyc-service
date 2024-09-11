package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Aadharrequestenity;

@Repository
public interface Aadharrequestenityrepository extends JpaRepository<Aadharrequestenity, String>{

}
