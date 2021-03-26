package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.LoginRequest;
import com.swp.ihelp.app.account.request.ProfileUpdateRequest;
import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.account.response.LoginResponse;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.image.ImageService;
import com.swp.ihelp.security.JwtTokenUtil;
import com.swp.ihelp.security.UserDetailsServiceImpl;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private ImageService imageService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String email = loginRequest.getEmail();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String imageUrl = imageService.findAvatarByEmail(email);
        String role = "";

        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            role = "ADMIN";
        }
        if (roles.contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            role = "MANAGER";
        }
        if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            role = "USER";
        }
        return new LoginResponse(accessToken, email, imageUrl, role);
    }

    @ApiImplicitParam(name = "isRefreshToken", value = "Set refresh token header", required = true, dataTypeClass = String.class, paramType = "header", defaultValue = "true")
    @PostMapping("/refreshtoken")
    public Map<String, Object> refreshToken(HttpServletRequest request) throws Exception {
        DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");

        Map<String, Object> expectedMap = getMapFromJwtToken(claims);
        String token = jwtTokenUtil.regenerateAccessToken(expectedMap, expectedMap.get("sub").toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("refreshToken", token);
        Map<String, Object> map = jsonObject.toMap();

        return map;
    }

    @PostMapping("/signup")
    public String register(@RequestBody SignUpRequest signUpRequest) throws Exception {
        accountService.insert(signUpRequest);
        return "Created new account";
    }

    @GetMapping("/accounts/{email}")
    public ResponseEntity<ProfileResponse> findById(@PathVariable String email) throws Exception {
        ProfileResponse response = accountService.findById(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountGeneralResponse>> findAll() throws Exception {
        return new ResponseEntity<>(accountService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/accounts/{email}/role")
    public ResponseEntity<Map<String, Object>> findRoleById(@PathVariable String email) throws Exception {
        String role = accountService.findRoleById(email);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", role);
        Map<String, Object> map = jsonObject.toMap();
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/accounts/event/{eventId}")
    public List<Map<String, Object>> findByEventId(@PathVariable String eventId) throws Exception {
        return accountService.findByEventId(eventId);
    }

    @GetMapping("/accounts/service/{serviceId}")
    public List<Map<String, Object>> findByServiceId(@PathVariable String serviceId) throws Exception {
        return accountService.findByServiceId(serviceId);
    }

    @PutMapping("/accounts/{email}/status")
    public void updateStatus(@PathVariable String email, @RequestBody String statusId) throws Exception {
        accountService.updateStatus(email, statusId);
    }

    @PutMapping("/accounts/{email}/reset_password")
    public void updatePassword(@PathVariable String email, @RequestBody String password) throws Exception {
        accountService.updatePassword(email, password);
    }

    @PutMapping("/accounts")
    public ProfileResponse update(@RequestBody ProfileUpdateRequest request) throws Exception {
        return accountService.update(request);
    }

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
