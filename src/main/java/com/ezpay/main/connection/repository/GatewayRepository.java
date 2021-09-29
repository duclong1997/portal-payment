package com.ezpay.main.connection.repository;

import com.ezpay.core.entity.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, String> {
}
