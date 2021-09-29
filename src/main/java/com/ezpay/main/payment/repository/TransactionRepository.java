package com.ezpay.main.payment.repository;

import com.ezpay.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "select * from transaction where merchantProjectId = :merchantProjectId and orderInfo = :orderInfo limit 1", nativeQuery = true)
    Optional<Transaction> getTransaction(@Param("merchantProjectId") int mercharProjectId, @Param("orderInfo") String orderInfo);

    @Query(value = "select * from transaction where merchantProjectId = :merchantProjectId and orderInfo = :orderInfo and status > 0 limit 1", nativeQuery = true)
    Optional<Transaction> getTransactionUpdated(@Param("merchantProjectId") int mercharProjectId, @Param("orderInfo") String orderInfo);

    @Query(value = "select * from transaction where txnRef = :txnRef limit 1", nativeQuery = true)
    Optional<Transaction> getByTxnRef(@Param("txnRef") String txnRef);

    @Query(value = "select * from transaction where status = 1 and responseCode is not null", nativeQuery = true)
    List<Transaction> getByStatus();

//    @Query(value = "select * from transaction where status = 0 and expDate between :from and :to and countQuery <= :maxCountQuery", nativeQuery = true)
//    List<Transaction> getTransactionNotUpdate(@Param("from") String form, @Param("to") String to, @Param("maxCountQuery") int maxCountQuery);

    @Query(value = "select * from transaction where status = 0 and countQuery <= :maxCountQuery", nativeQuery = true)
    List<Transaction> getTransactionNotUpdated(@Param("maxCountQuery") int maxCountQuery);

    @Query(value = "select * from transaction where (status = 1 or status = 2) and countUpdates <= :maxCountUpdates", nativeQuery = true)
    List<Transaction> getTransactionNotUpdatedToProject(@Param("maxCountUpdates") int maxCountUpdates);
}
