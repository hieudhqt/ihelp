package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    void insert(SignUpRequest signUpRequest) throws Exception;

    AccountEntity findById(String email) throws Exception;

//    Page<AccountGeneralResponse> findAll(Pageable pageable) throws Exception;


}
