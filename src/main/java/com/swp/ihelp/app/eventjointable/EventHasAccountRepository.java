package com.swp.ihelp.app.eventjointable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventHasAccountRepository extends JpaRepository<EventHasAccountEntity, EventHasAccountEntityPK> {
    @Query("SELECT e.account.email From EventHasAccountEntity e Where e.event.id = :eventId")
    List<String> findMemberEmailsByEventId(String eventId);
}
