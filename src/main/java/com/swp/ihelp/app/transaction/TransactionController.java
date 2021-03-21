package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.transaction.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<String> save(@RequestBody TransactionRequest request) throws Exception {
        transactionService.save(request);
        return ResponseEntity.ok("Transaction added.");
    }
}
