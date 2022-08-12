package com.rstn.iws.webservice.repository;

import com.rstn.iws.webservice.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Claimrepository extends JpaRepository<ClaimEntity,Long> {
    List<ClaimEntity> findByApplicationNumber(String applicationNumber);
}
