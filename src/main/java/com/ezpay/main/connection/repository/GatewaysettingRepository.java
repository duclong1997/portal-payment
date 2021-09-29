package com.ezpay.main.connection.repository;

import com.ezpay.core.entity.Gatewaysetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewaysettingRepository extends JpaRepository<Gatewaysetting, Integer> {
}
