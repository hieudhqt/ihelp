package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.transaction.request.TransactionRequest;

import java.util.Map;

public interface TransactionService {

    void save(TransactionRequest request) throws Exception;

    Map<String, Object> findByEmail(String email, int page) throws Exception;
}
