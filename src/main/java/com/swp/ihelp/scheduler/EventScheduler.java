package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.event.EventRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventScheduler {
    private EventRepository eventRepository;

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoStartEvent() {
        Session currentSession = entityManager.unwrap(Session.class);

        String query = "Select e.id, e.startDate " +
                "From EventEntity e";
        Map<String, Timestamp> results = new HashMap<>();
        List<Object[]> resultList = currentSession.createQuery(query).getResultList();
        for (Object[] event : resultList) {
            results.put((String) event[0], (Timestamp) event[1]);
        }

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);

        for (Map.Entry<String, Timestamp> entry : results.entrySet()) {
            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTimeInMillis(entry.getValue().getTime());
            startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startDateCalendar.set(Calendar.MINUTE, 0);
            startDateCalendar.set(Calendar.SECOND, 0);
            startDateCalendar.set(Calendar.MILLISECOND, 0);

            if (startDateCalendar.compareTo(currentDate) == 0) {
                EventEntity eventEntity = eventRepository.getOne(entry.getKey());
                if (eventEntity.getStatus().getName().trim().equals("Approved")) {
                    eventRepository.updateStatus(eventEntity.getId(), 4);
                }
            }
        }
    }
}
