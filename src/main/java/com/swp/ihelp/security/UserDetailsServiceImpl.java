package com.swp.ihelp.security;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;

        AccountEntity accountEntity = accountRepository.getOne(s);
        if (accountEntity != null) {
            roles = Arrays.asList(new SimpleGrantedAuthority(accountEntity.getRole().getName()));
            return new User(accountEntity.getEmail(), accountEntity.getPassword(), roles);
        }
        throw new UsernameNotFoundException("Email or password not found");
    }
}
