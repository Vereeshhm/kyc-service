package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Passportnumberedlog;


@Repository
public interface Passportnumberedlogrepository extends JpaRepository<Passportnumberedlog, String>{

}
