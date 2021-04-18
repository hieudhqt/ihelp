package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationService;
import com.swp.ihelp.app.transaction.request.TransactionRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    private NotificationService notificationService;

    private PushNotificationService pushNotificationService;

    @Autowired
    public TransactionController(TransactionService transactionService, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.transactionService = transactionService;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<String> save(@RequestBody TransactionRequest request) throws Exception {
        transactionService.save(request);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        String transactionTime = sdf.format(currentTime);

        //Push notification to receiver
        List<String> receiverDeviceTokens = notificationService.findDeviceTokensByEmail(request.getReceiverEmail());
        if (receiverDeviceTokens != null || !receiverDeviceTokens.isEmpty()) {
            PushNotificationRequest receiverNotificationRequest = new PushNotificationRequest()
                    .setTitle("You have received " + request.getPoint() + " point from " + request.getSenderEmail())
                    .setMessage(request.getPoint() + " point was sent from " + request.getSenderEmail() + " at: " + transactionTime)
                    .setRegistrationTokens(receiverDeviceTokens);

            pushNotificationService.sendPushNotificationToMultiDevices(receiverNotificationRequest);

            notificationService.insert(new NotificationEntity()
                    .setTitle("You have received " + request.getPoint() + " point from " + request.getSenderEmail())
                    .setMessage(request.getPoint() + " point was sent from " + request.getSenderEmail() + " at: " + transactionTime)
                    .setDate(new Timestamp(System.currentTimeMillis()))
                    .setAccountEntity(new AccountEntity().setEmail(request.getReceiverEmail())));
        }

        //Push notification to sender
        List<String> senderDeviceTokens = notificationService.findDeviceTokensByEmail(request.getSenderEmail());
        if (senderDeviceTokens != null || !senderDeviceTokens.isEmpty()) {
            PushNotificationRequest senderNotificationRequest = new PushNotificationRequest()
                    .setTitle(request.getPoint() + " point was sent successfully to " + request.getReceiverEmail())
                    .setMessage(request.getPoint() + " point was sent to " + request.getReceiverEmail() + " at: " + transactionTime)
                    .setRegistrationTokens(senderDeviceTokens);

            pushNotificationService.sendPushNotificationToMultiDevices(senderNotificationRequest);

            notificationService.insert(new NotificationEntity()
                    .setTitle(request.getPoint() + " point was sent successfully to " + request.getReceiverEmail())
                    .setMessage(request.getPoint() + " point was sent to " + request.getReceiverEmail() + " at: " + transactionTime)
                    .setDate(new Timestamp(System.currentTimeMillis()))
                    .setAccountEntity(new AccountEntity().setEmail(request.getSenderEmail())));
        }

        return ResponseEntity.ok("Transaction added.");
    }

    @GetMapping("/transactions/{email}")
    public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = transactionService.findByEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
