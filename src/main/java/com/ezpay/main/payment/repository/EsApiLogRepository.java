package com.ezpay.main.payment.repository;

import com.ezpay.core.entity.EsApiLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EsApiLogRepository extends ElasticsearchCrudRepository<EsApiLog, String> {
    @Query("{\"bool\": {\"must\": [{\"match\": {\"type\": \"?0\"}}]}}")
    Page<EsApiLog> findByType(String type, Pageable pageable);
}
