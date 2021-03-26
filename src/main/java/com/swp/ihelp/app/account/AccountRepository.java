package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.response.ParticipantsMapping;
import com.swp.ihelp.app.account.response.ProfileResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Query("SELECT a.role.name FROM AccountEntity a WHERE a.email=:email")
    String findRoleByEmail(String email);

    @Query(value = "SELECT a.email AS email, a.full_name AS fullname, a.phone as phone,i.image_url AS imageUrl " +
            "FROM account a " +
            "INNER JOIN event_has_account ea on ea.account_email = a.email AND ea.event_id=:eventId " +
            "LEFT JOIN image i on i.account_email = a.email AND i.type = 'avatar'", nativeQuery = true)
    List<ParticipantsMapping> findByEventId(String eventId);

    @Query(value = "SELECT a.email AS email, a.full_name AS fullname, a.phone as phone, i.image_url AS imageUrl " +
            "FROM account a " +
            "INNER JOIN service_has_account sa on sa.account_email = a.email AND sa.service_id=:serviceId " +
            "LEFT JOIN image i on i.account_email = a.email AND i.type = 'avatar'", nativeQuery = true)
    List<ParticipantsMapping> findByServiceId(String serviceId);

    @Modifying
    @Query(value = "UPDATE account a SET a.account_status_id=:statusId WHERE a.email=:email", nativeQuery = true)
    void updateStatus(String email, String statusId) throws Exception;

    @Modifying
    @Query(value = "UPDATE account a SET a.password=:password WHERE a.email=:email", nativeQuery = true)
    void updatePassword(String email, String password) throws Exception;

}
