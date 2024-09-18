package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Dlnumbrfetchapilog;


@Repository
public interface Dlnumberfetchlogrepository extends JpaRepository<Dlnumbrfetchapilog, String>{

}
