package com.swp.ihelp.app.eventjointable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventHasAccountRepository extends JpaRepository<EventHasAccountEntity, EventHasAccountEntityPK> {
    @Query("SELECT e.account.email From EventHasAccountEntity e Where e.event.id = :eventId")
    List<String> findMemberEmailsByEventId(String eventId);

    @Modifying
    @Query("DELETE FROM EventHasAccountEntity e " +
            "where e.event.id = :eventId and e.account.email = :email")
    void quitEvent(String eventId, String email);

    @Query("SELECT e.isEvaluated FROM EventHasAccountEntity e WHERE e.event.id = :eventId AND " +
            "e.account.email = :email ")
    boolean isMemberEvaluated(String eventId, String email);
}
