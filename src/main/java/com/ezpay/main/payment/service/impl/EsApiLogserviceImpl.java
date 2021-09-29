package com.ezpay.main.payment.service.impl;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.service.impl.EsBaseServiceImpl;
import com.ezpay.main.payment.repository.EsApiLogRepository;
import com.ezpay.main.payment.service.EsApiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EsApiLogserviceImpl extends EsBaseServiceImpl<EsApiLog, String> implements EsApiLogService {
    private EsApiLogRepository repository;

    @Autowired
    public EsApiLogserviceImpl(EsApiLogRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Page<EsApiLog> findByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByType(type, pageable);
    }
}
