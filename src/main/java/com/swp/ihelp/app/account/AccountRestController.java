package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.LoginRequest;
import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.LoginResponse;
import com.swp.ihelp.security.JwtTokenUtil;
import com.swp.ihelp.security.UserDetailsServiceImpl;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AccountRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private final AccountService accountService;


    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        return new LoginResponse(accessToken);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity refreshToken(HttpServletRequest request) throws Exception {
        DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");

        Map<String, Object> expectedMap = getMapFromJwtToken(claims);
        String token = jwtTokenUtil.regenerateAccessToken(expectedMap, expectedMap.get("sub").toString());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public String register(@RequestBody SignUpRequest signUpRequest) throws Exception {
        accountService.insert(signUpRequest);
        return "Created new account";
    }

    @GetMapping("/accounts/{email}")
    public ResponseEntity findById(@PathVariable String email) throws Exception {
        accountService.findById(email);
        return ResponseEntity.ok("Found");
    }

//    @GetMapping("/accounts")
//    public Page<AccountGeneralResponse>

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException ex) {
            throw new Exception("USER_DISABLED", ex);
        } catch (BadCredentialsException ex) {
            throw new Exception("INVALID_CREDENTIALS", ex);
        }
    }

    private Map<String, Object> getMapFromJwtToken(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}
