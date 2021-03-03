package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void insert(SignUpRequest signUpRequest) throws Exception {
        if (!accountRepository.existsById(signUpRequest.getEmail())) {
            AccountEntity accountEntity = SignUpRequest.convertToEntity(signUpRequest);
            accountRepository.save(accountEntity);
        }
    }

    @Override
    public AccountEntity findById(String email) throws Exception {
        Optional<AccountEntity> result = accountRepository.findById(email);
        AccountEntity accountEntity = null;
        if (result.isPresent()) {
            accountEntity = result.get();
        } else {
            throw new RuntimeException("Do not find account!");
        }
        return accountEntity;
    }

//    @Override
//    public Page<AccountGeneralResponse> findAll(Pageable pageable ) throws Exception {
//        Page<AccountEntity> accountEntityPage = accountRepository.findAll(pageable);
//        Page<AccountGeneralResponse> accountGeneralResponsePage = accountEntityPage.map(::)
//    }

}
