package com.swp.ihelp.app.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    @Query("SELECT t from TransactionEntity t " +
            "WHERE t.senderAccount.email = :email OR t.receiverAccount.email = :email " +
            "ORDER BY t.date desc")
    Page<TransactionEntity> findAllByEmail(String email, Pageable pageable);
}
