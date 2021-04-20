package com.swp.ihelp.app.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

    @Query("SELECT n FROM NotificationEntity n WHERE n.accountEntity.email=:email")
    Page<NotificationEntity> findByEmail(String email, Pageable pageable) throws Exception;

}
