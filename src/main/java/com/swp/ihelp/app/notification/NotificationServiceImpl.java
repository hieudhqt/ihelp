package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.notification.response.NotificationResponse;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${paging.page-size}")
    private int pageSize;

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
    public Map<String, Object> findByEmail(String email, int page) throws Exception {
        if (!accountRepository.existsById(email)) {
            throw new RuntimeException("Account: " + email + " not found");
        }
        Pageable paging = PageRequest.of(page, pageSize);
        Page<NotificationEntity> pageNotifications = notificationRepository.findByEmail(email, paging);
        if (pageNotifications.isEmpty()) {
            throw new EntityNotFoundException("Notification not found.");
        }

        List<NotificationEntity> notificationEntities = pageNotifications.getContent();
        List<NotificationResponse> notificationResponses = NotificationResponse.convertToListNotification(notificationEntities);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationResponses);
        response.put("currentPage", pageNotifications.getNumber());
        response.put("totalItems", pageNotifications.getTotalElements());
        response.put("totalPages", pageNotifications.getTotalPages());

        return response;
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

    @Transactional
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
