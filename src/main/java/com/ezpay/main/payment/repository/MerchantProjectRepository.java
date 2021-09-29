package com.ezpay.main.payment.repository;

import com.ezpay.core.entity.MerchantProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantProjectRepository extends JpaRepository<MerchantProject, Integer> {
    @Query(value = "select * from merchant_project where connectId = :connectId limit 1",nativeQuery = true)
    Optional<MerchantProject> getByConnectId(String connectId);
}
