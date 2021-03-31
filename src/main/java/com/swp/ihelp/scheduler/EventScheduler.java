package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.status.StatusEnum;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;

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

    //    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoStartCompleteEvent() {
        Session currentSession = entityManager.unwrap(Session.class);

        String query = "Select NEW EventEntity(e.id, e.startDate, e.endDate) " +
                "From EventEntity e";
//        Map<String, Timestamp> results = new HashMap<>();
        List<EventEntity> resultList = currentSession.createQuery(query, EventEntity.class).getResultList();
//        for (Object[] event : resultList) {
//            results.put((String) event[0], (Timestamp) event[1]);
//        }

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);

        for (EventEntity event : resultList) {
            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTimeInMillis(event.getStartDate().getTime());
            startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startDateCalendar.set(Calendar.MINUTE, 0);
            startDateCalendar.set(Calendar.SECOND, 0);
            startDateCalendar.set(Calendar.MILLISECOND, 0);

            if (startDateCalendar.compareTo(currentDate) == 0) {
                EventEntity eventEntity = eventRepository.getOne(event.getId());
                if (eventEntity.getStatus().getName().trim().equals("Approved")) {
                    eventRepository.updateStatus(eventEntity.getId(), StatusEnum.ONGOING.getId());
                }
            }

            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTimeInMillis(event.getEndDate().getTime());
            endDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            endDateCalendar.set(Calendar.MINUTE, 0);
            endDateCalendar.set(Calendar.SECOND, 0);
            endDateCalendar.set(Calendar.MILLISECOND, 0);

            if (endDateCalendar.compareTo(currentDate) == 0) {
                EventEntity eventEntity = eventRepository.getOne(event.getId());
                if (eventEntity.getStatus().getName().trim().equals("Ongoing")) {
                    eventRepository.updateStatus(eventEntity.getId(), StatusEnum.COMPLETED.getId());
                }
            }
        }
    }
}
