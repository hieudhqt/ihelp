package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.event.EventEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
public class EventTaskDispatcher implements Runnable {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isInterrupted = false;

    private EntityManager entityManager;

    public EventTaskDispatcher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void run() {
        while (!isInterrupted) {
            Date now = new Date();
            System.out.println("Reading database for next date");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateString = dateFormat.format(now);

            String nearestEventQuery = "SELECT NEW EventEntity(e.id, e.startDate, e.endDate) FROM EventEntity e WHERE e.startDate >= ?1 " +
                    "ORDER BY e.startDate ASC";

            List<EventEntity> nearestEventToStart = entityManager.createQuery(nearestEventQuery).setParameter(1, now)
                    .setMaxResults(1).getResultList();

            String eventToStartId = (String) nearestEventToStart.get(0).getId();
            Date nearestStartDate = (Date) nearestEventToStart.get(0).getStartDate();
//
//            Object[] nearestEventToEnd = eventRepository.getNearestEventEndDate(currentDateString);
//            String eventToEndId = (String) nearestEventToEnd[0];
//            Date nearestEndDate = (Date) nearestEventToEnd[1];

            long delayStartDate = nearestStartDate.getTime() - now.getTime();
//            long delayEndDate = nearestEndDate.getTime() - now.getTime();

            System.out.println("delay:" + delayStartDate);

            CountDownLatch latch = new CountDownLatch(1);
            ScheduledFuture<?> countdownStartEvent = scheduler.schedule(
                    new EventTask(latch, eventToStartId, true), delayStartDate, TimeUnit.MILLISECONDS);
//            ScheduledFuture<?> countdownEndEvent = scheduler.schedule(
//                    new EventTask(latch, eventToEndId, false), delayEndDate, TimeUnit.MILLISECONDS);

            try {
                System.out.println("Blocking until the current job has completed");
                latch.await();
            } catch (InterruptedException e) {
                System.out.println("Thread has been requested to stop");
                isInterrupted = true;
            }
            if (!isInterrupted) {
                System.out.println("Job has completed normally");
            }
        }
        scheduler.shutdown();
    }
}
