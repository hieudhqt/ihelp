package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.response.NotEvaluatedParticipantsMapping;
import com.swp.ihelp.app.account.response.ParticipantsMapping;
import com.swp.ihelp.app.account.response.ProfileResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Query("SELECT a.role.name FROM AccountEntity a WHERE a.email=:email")
    String findRoleByEmail(String email);

    @Query(value = "SELECT a.email AS email, a.full_name AS fullName, a.gender AS gender,a.phone AS phone, a.balance_point AS balancePoint, ea.join_date AS joinDate, i.image_url AS imageUrl " +
            "FROM account a " +
            "INNER JOIN event_has_account ea ON ea.account_email = a.email AND ea.event_id=:eventId " +
            "LEFT JOIN image i ON i.account_email = a.email AND i.type = 'avatar'", nativeQuery = true)
    List<ParticipantsMapping> findByEventId(String eventId);

    @Query(value = "SELECT a.email AS email, a.full_name AS fullname, a.gender AS gender,a.phone AS phone, a.balance_point AS balancePoint, sa.use_date AS joinDate, i.image_url AS imageUrl " +
            "FROM account a " +
            "INNER JOIN service_has_account sa ON sa.account_email = a.email AND sa.service_id=:serviceId " +
            "LEFT JOIN image i ON i.account_email = a.email AND i.type = 'avatar'", nativeQuery = true)
    List<ParticipantsMapping> findByServiceId(String serviceId);

    @Query(value = "SELECT a.email AS email, a.full_name AS fullName, a.phone AS phone, i.image_url AS imageUrl " +
            "FROM account a " +
            "INNER JOIN event_has_account ea ON ea.account_email = a.email AND ea.event_id=:eventId " +
            "LEFT JOIN image i ON i.account_email = a.email AND i.type = 'avatar' " +
            "WHERE ea.is_evaluated <> 1", nativeQuery = true)
    List<NotEvaluatedParticipantsMapping> findNotEvaluatedAccountsByEventId(String eventId);

    @Modifying
    @Query(value = "UPDATE account a SET a.account_status_id=:statusId WHERE a.email=:email", nativeQuery = true)
    void updateStatus(String email, String statusId) throws Exception;

    @Modifying
    @Query(value = "UPDATE AccountEntity a SET a.password=:password WHERE a.email=:email")
    void updatePassword(String email, String password) throws Exception;

    @Modifying
    @Query(value = "UPDATE account a SET a.role_id=:roleId WHERE a.email=:email", nativeQuery = true)
    void updateRole(String email, String roleId) throws Exception;

    @Modifying
    @Query("UPDATE AccountEntity a SET a.deviceToken=:deviceToken WHERE a.email=:email")
    void updateDeviceToken(String email, String deviceToken) throws Exception;

    @Query("SELECT CASE WHEN COUNT (a) > 0 THEN TRUE ELSE FALSE END FROM AccountEntity a WHERE a.phone=:phone")
    boolean existsByPhone(String phone) throws Exception;

}
