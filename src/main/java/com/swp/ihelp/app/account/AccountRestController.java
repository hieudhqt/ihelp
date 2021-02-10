package com.swp.ihelp.app.account;

import com.swp.ihelp.security.JwtTokenUtil;
import com.swp.ihelp.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AccountRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

//    @PostMapping("/signup")
//    public Boolean register(@RequestBody AccountEntity accountEntity) throws Exception {
//
//    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException ex) {
            throw new Exception("USER_DISABLED", ex);
        } catch (BadCredentialsException ex) {
            throw new Exception("INVALID_CREDENTIALS", ex);
        }
    }

}
