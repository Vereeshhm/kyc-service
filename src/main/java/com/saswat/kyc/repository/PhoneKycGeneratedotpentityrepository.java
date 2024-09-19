package com.saswat.kyc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saswat.kyc.model.PhoneKycGeneratedotpentity;

public interface PhoneKycGeneratedotpentityrepository extends JpaRepository<PhoneKycGeneratedotpentity, String> {

	@Query("SELECT p FROM PhoneKycGeneratedotpentity p WHERE p.mobileNumber = :mobileNumber ORDER BY p.id DESC")
    List<PhoneKycGeneratedotpentity> findByMobileNumberOrderByIdDesc(@Param("mobileNumber") String mobileNumber);
}
