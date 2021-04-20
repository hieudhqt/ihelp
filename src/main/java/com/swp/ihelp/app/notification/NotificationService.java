package com.swp.ihelp.app.notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    Map<String, Object> findByEmail(String email, int page) throws Exception;

    String insert(NotificationEntity notificationEntity) throws Exception;

    void insertDeviceToken(String email, String deviceToken) throws Exception;

    void deleteDeviceToken(String email, String deviceToken) throws Exception;

    List<String> findDeviceTokensByEmail(String email) throws Exception;

}
