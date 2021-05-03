package com.swp.ihelp.security;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.account.AccountStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;

        AccountEntity accountEntity = accountRepository.getOne(s);
        String fullName = accountEntity.getFullName();
        String statusId = accountEntity.getStatus().getId();

        if (statusId.equals(AccountStatusEnum.SUSPENDED.getId())) {
            throw new DisabledException("This account has been suspended.");
        }

        if (accountEntity != null) {
            roles = Arrays.asList(new SimpleGrantedAuthority(accountEntity.getRole().getName()));
            return new CustomUser(accountEntity.getEmail(), accountEntity.getPassword(), roles, fullName);
        }
        throw new UsernameNotFoundException("Email or password not found");
    }
}
