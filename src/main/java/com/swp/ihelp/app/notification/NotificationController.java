package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email,
                                                           @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(notificationService.findByEmail(email, page), HttpStatus.OK);
    }

    @PostMapping("/insert")
    public String insert() throws Exception {
        return notificationService.insert(new NotificationEntity()
        .setAccountEntity(new AccountEntity().setEmail("test"))
        .setDate(new Timestamp(System.currentTimeMillis()))
        .setTitle("test")
        .setMessage("test body"));
    }
}
