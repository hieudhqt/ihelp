package com.swp.ihelp.app.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

    @Query("SELECT n FROM NotificationEntity n WHERE n.accountEntity.email=:email")
    List<NotificationEntity> findByEmail(String email) throws Exception;

}
