package com.swp.ihelp.app.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {
    private AccountRepository repository;

    @Autowired
    public AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/accounts")
    public List<AccountEntity> getAll() throws Exception {
        return repository.findAll();
    }

    @GetMapping("/accounts/{email}")
    public AccountResponse findByEmail(@PathVariable String email) throws Exception {
        Optional<AccountEntity> accountEntityOptional = repository.findById(email);
        AccountResponse accountResponse = null;
        if (accountEntityOptional.isPresent()) {
            accountResponse = new AccountResponse(accountEntityOptional.get());
        }
        return accountResponse;
    }
}
