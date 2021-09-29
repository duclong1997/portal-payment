package com.ezpay.main.payment.repository;

import com.ezpay.core.entity.MerchantGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantGatewayRepository extends JpaRepository<MerchantGateway, Integer> {

    @Query(value = "select * from merchantgateway where merchantProjectId = :merchantProjectId and gatewayCode = :gatewayCode limit 1", nativeQuery = true)
    Optional<MerchantGateway> getByMerchantAndGateway(@Param("merchantProjectId") int merchantProjectId, @Param("gatewayCode") String gatewayCode);
}
