package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.voterdetailedlog;


@Repository
public interface voterdetailedlogrepository extends JpaRepository<voterdetailedlog, Long>{

}
