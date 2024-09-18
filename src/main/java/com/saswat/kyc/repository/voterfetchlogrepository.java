package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Voterfetchlog;


@Repository
public interface voterfetchlogrepository extends JpaRepository<Voterfetchlog, Long>{

}
