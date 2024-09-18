package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.ExperianbureauLog;


@Repository
public interface Experianbureaulogrepository extends JpaRepository<ExperianbureauLog, Long> {

}
