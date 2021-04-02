package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.transaction.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<String> save(@RequestBody TransactionRequest request) throws Exception {
        transactionService.save(request);
        return ResponseEntity.ok("Transaction added.");
    }

    @GetMapping("/transactions/{email}")
    public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = transactionService.findByEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
