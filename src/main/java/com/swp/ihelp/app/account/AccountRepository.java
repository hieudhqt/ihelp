package com.swp.ihelp.app.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Query("SELECT a.role.name FROM AccountEntity a WHERE a.email=:email")
    String findRoleByEmail(String email);

    @Query("SELECT e.account FROM EventHasAccountEntity e WHERE e.event.id=:eventId")
    List<AccountEntity> findByEventId(String eventId);

    @Query("SELECT s.account FROM ServiceHasAccountEntity s WHERE s.service.id=:serviceId")
    List<AccountEntity> findByServiceId(String serviceId);

    @Modifying
    @Query(value = "UPDATE account a SET a.account_status_id=:statusId WHERE a.email=:email", nativeQuery = true)
    void updateStatus(String email, String statusId) throws Exception;

    @Modifying
    @Query(value = "UPDATE account a SET a.password=:password WHERE a.email=:email", nativeQuery = true)
    void updatePassword(String email, String password) throws Exception;

}
