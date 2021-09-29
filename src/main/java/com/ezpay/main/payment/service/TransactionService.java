package com.ezpay.main.payment.service;

import com.ezpay.core.entity.Transaction;
import com.ezpay.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface TransactionService extends BaseService<Transaction, Integer> {
    Optional<Transaction> getTransaction(int mercharProjectId, String orderInfo);

    Optional<Transaction> getTransactionUpdated(int mercharProjectId, String orderInfo);

    Optional<Transaction> getByTxnRef(String txnRef);

    List<Transaction> getByStatus();

    List<Transaction> getTransactionNotUpdated(int maxCountQuery);

    List<Transaction> getTransactionNotUpdatedToProject(int maxCountUpdates);
}
