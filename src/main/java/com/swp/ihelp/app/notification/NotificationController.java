package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.notification.response.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{email}")
    public List<NotificationResponse> findByEmail(@PathVariable String email) throws Exception {
        return notificationService.findByEmail(email);
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
