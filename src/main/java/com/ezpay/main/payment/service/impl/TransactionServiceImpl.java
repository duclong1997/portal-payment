package com.ezpay.main.payment.service.impl;

import com.ezpay.core.entity.Transaction;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.payment.repository.TransactionRepository;
import com.ezpay.main.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Integer> implements TransactionService {
    private TransactionRepository repository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Transaction> getTransaction(int mercharProjectId, String orderInfo) {
        return repository.getTransaction(mercharProjectId, orderInfo);
    }

    @Override
    public Optional<Transaction> getTransactionUpdated(int mercharProjectId, String orderInfo) {
        return repository.getTransactionUpdated(mercharProjectId, orderInfo);
    }

    @Override
    public Optional<Transaction> getByTxnRef(String txnRef) {
        return repository.getByTxnRef(txnRef);
    }

    @Override
    public List<Transaction> getByStatus() {
        return repository.getByStatus();
    }

    @Override
    public List<Transaction> getTransactionNotUpdated(int maxCountQuery) {
        return repository.getTransactionNotUpdated(maxCountQuery);
    }

    @Override
    public List<Transaction> getTransactionNotUpdatedToProject(int maxCountUpdates) {
        return repository.getTransactionNotUpdatedToProject(maxCountUpdates);
    }


}
