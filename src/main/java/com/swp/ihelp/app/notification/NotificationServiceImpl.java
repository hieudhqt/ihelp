package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.notification.response.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;

    private AccountRepository accountRepository;

    private DeviceRepository deviceRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, AccountRepository accountRepository, DeviceRepository deviceRepository) {
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public List<NotificationResponse> findByEmail(String email) throws Exception {
        if (!accountRepository.existsById(email)) {
            throw new RuntimeException("Account: " + email + " not found");
        }
        List<NotificationEntity> notificationEntities = notificationRepository.findByEmail(email);
        return NotificationResponse.convertToListNotification(notificationEntities);
    }

    @Override
    public String insert(NotificationEntity notificationEntity) throws Exception {
        NotificationEntity savedNoti = notificationRepository.save(notificationEntity);
        return savedNoti.getId();
    }

    @Override
    public void insertDeviceToken(String email, String deviceToken) throws Exception {
        existsByEmail(email);
        AccountEntity accountEntity = new AccountEntity().setEmail(email);
        deviceRepository.save(new DeviceEntity().setToken(deviceToken).setAccount(accountEntity));
    }

    @Override
    public void deleteDeviceToken(String email, String deviceToken) throws Exception {
        existsByEmail(email);
        if (!deviceRepository.existsById(deviceToken)) {
            throw new RuntimeException("Device token doesn't exist");
        }
        deviceRepository.deleteDeviceToken(email, deviceToken);
    }

    @Override
    public List<String> findDeviceTokensByEmail(String email) throws Exception {
        existsByEmail(email);
        return deviceRepository.findByEmail(email);
    }

    private void existsByEmail(String email) {
        if (!accountRepository.existsById(email)) {
            throw new RuntimeException("Account: " + email + " not found.");
        }
    }

}
