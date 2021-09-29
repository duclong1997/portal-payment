package com.ezpay.main.connection.repository;

import com.ezpay.core.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    @Query(value = "select * from merchant where taxCode =:taxCode limit 1", nativeQuery = true)
    Optional<Merchant> getByTaxCode(@Param("taxCode") String taxCode);
}
