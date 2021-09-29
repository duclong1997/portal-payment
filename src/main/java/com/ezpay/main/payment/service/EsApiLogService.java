package com.ezpay.main.payment.service;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.service.EsBaseService;
import org.springframework.data.domain.Page;


public interface EsApiLogService extends EsBaseService<EsApiLog, String> {
    Page<EsApiLog> findByType(String type, int page, int size);
}
