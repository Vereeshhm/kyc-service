package com.saswat.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saswat.kyc.model.Panfetchv3details;

@Repository
public interface Panfetchv3detailsrepository extends JpaRepository<Panfetchv3details, String>{

}
