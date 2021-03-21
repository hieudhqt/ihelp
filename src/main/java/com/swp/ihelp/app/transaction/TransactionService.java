package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.transaction.request.TransactionRequest;

public interface TransactionService {
    void save(TransactionRequest request) throws Exception;
}
