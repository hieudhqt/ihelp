package com.swp.ihelp.app.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {

    @Modifying
    @Query("DELETE FROM DeviceEntity d WHERE d.token=:deviceToken AND d.account.email=:email")
    void deleteDeviceToken(String email, String deviceToken) throws Exception;

    @Query("SELECT d.token FROM DeviceEntity d WHERE d.account.email=:email")
    List<String> findByEmail(String email) throws Exception;

}
