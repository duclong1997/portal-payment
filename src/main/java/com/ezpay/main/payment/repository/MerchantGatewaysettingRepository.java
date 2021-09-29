package com.ezpay.main.payment.repository;

import com.ezpay.core.entity.MerchantGatewaysetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantGatewaysettingRepository extends JpaRepository<MerchantGatewaysetting, Integer> {

    @Query(value = "select * from merchantgatewaysetting where merchantGatewayId = :merchantGatewayId and type = " + MerchantGatewaysetting.KEY_PARAM + " limit 1", nativeQuery = true)
    Optional<MerchantGatewaysetting> getKeyByMerchantGateway(@Param("merchantGatewayId") int merchantGatewayId);
}
