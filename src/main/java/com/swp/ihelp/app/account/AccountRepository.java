package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.response.NotEvaluatedParticipantsMapping;
import com.swp.ihelp.app.account.response.ParticipantsMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "WHERE ea.is_evaluated <> 1 OR ea.is_evaluated IS NULL", nativeQuery = true)
    List<NotEvaluatedParticipantsMapping> findNotEvaluatedAccountsByEventId(String eventId);

    @Query("SELECT a.phone FROM AccountEntity a WHERE a.email=:email")
    String findPhoneByEmail(String email) throws Exception;

    Page<AccountEntity> findAccountEntitiesByFullNameContainsIgnoreCase(String fullName, Pageable pageable) throws Exception;

    @Modifying
    @Query(value = "UPDATE account a SET a.account_status_id=:statusId WHERE a.email=:email", nativeQuery = true)
    void updateStatus(String email, String statusId) throws Exception;

    @Modifying
    @Query(value = "UPDATE AccountEntity a SET a.password=:password WHERE a.email=:email")
    void updatePassword(String email, String password) throws Exception;

    @Modifying
    @Query(value = "UPDATE account a SET a.role_id=:roleId WHERE a.email=:email", nativeQuery = true)
    void updateRole(String email, String roleId) throws Exception;

    @Query("SELECT CASE WHEN COUNT (a) > 0 THEN TRUE ELSE FALSE END FROM AccountEntity a WHERE a.phone=:phone")
    boolean existsByPhone(String phone) throws Exception;

    @Modifying
    @Query(value = "UPDATE AccountEntity a SET a.contributionPoint = a.contributionPoint + :contributionPoint WHERE a.email = :email ")
    void updateContributionPoint(String email, int contributionPoint) throws Exception;

    @Modifying
    @Query(value = "UPDATE AccountEntity a SET a.balancePoint = a.balancePoint + :point WHERE a.email = :email ")
    void updateBalancePoint(String email, int point) throws Exception;

    @Query(value = "SELECT a.* " +
            "FROM ihelp.account a  " +
            "INNER JOIN (SELECT r.account_email, SUM(r.point) as total " +
            "FROM ihelp.reward r  " +
            "WHERE DATE(r.created_date) >= :startDate AND DATE(r.created_date) <= :endDate " +
            "GROUP BY r.account_email) e " +
            "ON e.account_email = a.email  " +
            "WHERE a.role_id = 'user' " +
            "ORDER BY e.total desc " +
            "LIMIT 0, 100 ", nativeQuery = true)
    List<AccountEntity> getTop100AccountsByContributionAndDate(String startDate, String endDate) throws Exception;

    @Query(value = "SELECT a.* " +
            "FROM ihelp.account a  " +
            "INNER JOIN (SELECT r.account_email, SUM(r.point) as total " +
            "FROM ihelp.reward r  " +
            "WHERE DATE(r.created_date) >= :startDate AND DATE(r.created_date) <= :endDate " +
            "GROUP BY r.account_email) e " +
            "ON e.account_email = a.email  " +
            "WHERE a.role_id = 'user' " +
            "ORDER BY e.total desc", nativeQuery = true)
    Page<AccountEntity> getTopAccountsByContributionAndDateWithPaging(String startDate, String endDate, Pageable pageable) throws Exception;

    @Query(value = "SELECT a FROM AccountEntity a WHERE a.role.name = :roleName")
    List<AccountEntity> findByRoleName(String roleName) throws Exception;
}
