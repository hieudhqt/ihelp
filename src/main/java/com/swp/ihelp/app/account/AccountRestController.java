package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.*;
import com.swp.ihelp.app.account.response.LoginResponse;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.event.EventService;
import com.swp.ihelp.app.image.ImageService;
import com.swp.ihelp.app.notification.NotificationService;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import com.swp.ihelp.security.CustomUser;
import com.swp.ihelp.security.JwtTokenUtil;
import com.swp.ihelp.security.UserDetailsServiceImpl;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.ApiImplicitParam;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class AccountRestController {

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private UserDetailsServiceImpl userDetailsService;

    private AccountService accountService;

    private ImageService imageService;

    private EventService eventService;

    private PushNotificationService pushNotificationService;

    private NotificationService notificationService;

    @Autowired
    public AccountRestController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService, AccountService accountService, ImageService imageService, EventService eventService, PushNotificationService pushNotificationService, NotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.accountService = accountService;
        this.imageService = imageService;
        this.eventService = eventService;
        this.pushNotificationService = pushNotificationService;
        this.notificationService = notificationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String email = loginRequest.getEmail();
        final CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(email);
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String imageUrl = imageService.findAvatarByEmail(email);
        String fullName = userDetails.getFullName();

        List<String> evaluateRequiredEvents = eventService.findEvaluateRequiredByAuthorEmail(email);

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
//        pushNotificationService.subscribe("eBkp0QvHSymYb8RBxluv6y:APA91bEJXAcadb8pcGVHgYBI56a7esiApph6873RheqrtSbiumCyot6yYAKLNlIAkdhaJX2NJlDFj9OYSzwaHQVlSzwT8YUSfCiClQ4GE2lhLshpr6FvAHkaUApEXQZwYYdbvrtV0rWe", "EV_00001");
//        pushNotificationService.subscribe("dUjYtuPRk9vPKyThCy8n31:APA91bF6t4n0RKZpQ7iPyecXlEanBO4wdC4B5sXlr40ih9Y2sBES_OE-_x-h9u5-4vtICU5xgkNEuLxxmfMP6n-MIdlnJm1TGmJPp6mIJ304ZkEim2jovlqsAZPhsNc1yA1jRCW5IpkH", "EV_00001");
//        pushNotificationService.sendPushNotificationToToken(new PushNotificationRequest()
//        .setTitle("Test topic")
//        .setMessage("Test noti topic")
//                .setToken("dQ3dZpPsZ3XyQBTfkcLV-O:APA91bEQWwdXYnOWLGPd_Iu2N4f8LooeYXShtau8WHjZ5UcGMJIRSOYLORtXodLm0BTpIELSC5wQ78alTfEkhaOvu7BeHyg048VVmPcBc4KKghmKka_n1QZzFsTvD7p9mk640gE2XhUG"));
        return new LoginResponse(accessToken, email, fullName, imageUrl, role, evaluateRequiredEvents);
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

    @PostMapping("/accounts/{email}/avatar")
    public ResponseEntity insertAvatar(@PathVariable String email, @RequestBody String avatarUrl) throws Exception {
        accountService.insertAvatar(email, avatarUrl);
        return ResponseEntity.ok("Avatar added");
    }

    @PostMapping("/accounts/device_token")
    public ResponseEntity insertDeviceToken(@RequestBody DeviceTokenRequest request) throws Exception {
        notificationService.insertDeviceToken(request.getEmail(), request.getDeviceToken());
        return ResponseEntity.ok("Device token added");
    }

    @GetMapping("/accounts/{email}")
    public ResponseEntity<ProfileResponse> findById(@PathVariable String email) throws Exception {
        ProfileResponse response = accountService.findById(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(accountService.findAll(page), HttpStatus.OK);
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

    @GetMapping("/accounts/event/{eventId}/evaluation")
    public List<Map<String, Object>> findNotEvaluatedAccountsByEventId(@PathVariable String eventId) throws Exception {
        return accountService.findNotEvaluatedAccountsByEventId(eventId);
    }

    @GetMapping("/accounts/service/{serviceId}")
    public List<Map<String, Object>> findByServiceId(@PathVariable String serviceId) throws Exception {
        return accountService.findByServiceId(serviceId);
    }

    @GetMapping("/accounts/contributor")
    public ResponseEntity<Map<String, Object>> findTopContributors(@RequestParam(value = "page") int page,
                                                                   @RequestParam(value = "pageSize") int pageSize) throws Exception {
        return new ResponseEntity<>(accountService.getTopContributor(page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/accounts/exist")
    public Map<String, Object> existsByEmailAndPhone(@RequestParam String email, @RequestParam String phone) throws Exception {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required for verifying");
        }
        if (phone == null || phone.isEmpty()) {
            throw new RuntimeException("Phone is required for verifying");
        }
        return accountService.existsByEmailAndPhone(email, phone);
    }

    @PutMapping("/accounts/{email}/status/{statusId}")
    public void updateStatus(@PathVariable String email, @PathVariable String statusId) throws Exception {
        accountService.updateStatus(email, statusId);
    }

    @PutMapping("/accounts/reset_password")
    public void updatePassword(@RequestBody ResetPasswordRequest request) throws Exception {
        authenticate(request.getEmail(), request.getOldPassword());
        accountService.updatePassword(request.getEmail(), request.getNewPassword());
    }

    @PutMapping("/accounts/{email}/role/{roleId}")
    public void updateRole(@PathVariable String email, @PathVariable String roleId) throws Exception {
        accountService.updateRole(email, roleId);
    }

    @PutMapping("/accounts")
    public ProfileResponse update(@RequestBody ProfileUpdateRequest request) throws Exception {
        return accountService.update(request);
    }

    @PutMapping("/accounts/{email}/avatar")
    public ResponseEntity updateAvatar(@PathVariable String email, @RequestBody String avatarUrl) throws Exception {
        accountService.updateAvatar(email, avatarUrl);
        return ResponseEntity.ok("Avatar updated");
    }

    @DeleteMapping("/signout")
    public ResponseEntity logout(@RequestBody DeviceTokenRequest request) throws Exception {
        notificationService.deleteDeviceToken(request.getEmail(), request.getDeviceToken());
        return ResponseEntity.ok("Device token is deleted");
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
