package com.swp.ihelp.app.point;

import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.point.response.EventPointHistory;
import com.swp.ihelp.app.point.response.PointResponse;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PointServiceImpl implements PointService {
    private final static Pattern numberPattern = Pattern.compile("\\d+");

    private PointRepository pointRepository;
    private ImageRepository imageRepository;
    private RewardRepository rewardRepository;
    private EventRepository eventRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Autowired
    public PointServiceImpl(PointRepository pointRepository, ImageRepository imageRepository, RewardRepository rewardRepository, EventRepository eventRepository) {
        this.pointRepository = pointRepository;
        this.imageRepository = imageRepository;
        this.rewardRepository = rewardRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Map<String, Object> findByEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<PointEntity> pagePoints = pointRepository.findAllByEmail(email, paging);
        if (pagePoints.isEmpty()) {
            throw new EntityNotFoundException("Point not found.");
        }

        List<PointEntity> listPoints = pagePoints.getContent();
        List<PointResponse> pointResponses
                = PointResponse.convertToResponseList(listPoints);

        Map<String, Object> response = new HashMap<>();
        response.put("points", pointResponses);
        response.put("currentPage", pagePoints.getNumber());
        response.put("totalItems", pagePoints.getTotalElements());
        response.put("totalPages", pagePoints.getTotalPages());
        return response;
    }

    private static String getFirstNumber(String s) {
        Matcher m = numberPattern.matcher(s);
        return m.find() ? m.group() : null;
    }

    @Override
    public Map<String, Object> getHistoryByEventId(String eventId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        EventEntity event = eventRepository.getOne(eventId);
        if (event == null) {
            throw new EntityNotFoundException("Event not found.");
        }
        String authorEmail = event.getAuthorAccount().getEmail();
        Page<PointEntity> pagePoints = pointRepository.findPointReceivedByEventId(eventId, authorEmail, paging);
        if (pagePoints.isEmpty()) {
            throw new EntityNotFoundException("Point not found.");
        }

        List<PointEntity> listPoints = pagePoints.getContent();
        List<EventPointHistory> pointResponses = new ArrayList<>();
        for (PointEntity point : listPoints) {
            EventPointHistory eventPointHistory = new EventPointHistory();
            eventPointHistory.setEmail(point.getAccount().getEmail());
            eventPointHistory.setName(point.getAccount().getFullName());
            eventPointHistory.setImageUrl(imageRepository.findAvatarByEmail(point.getAccount().getEmail()));
            eventPointHistory.setPoint(point.getAmount());
            eventPointHistory.setRating(Integer.parseInt(getFirstNumber(point.getDescription())));

            RewardEntity rewardEntity = rewardRepository
                    .findRewardByEventIdAndEmail(eventId, point.getAccount().getEmail());
            if (rewardEntity != null) {
                int bonus = rewardEntity.getPoint();
                eventPointHistory.setBonus(bonus);
            } else {
                eventPointHistory.setBonus(0);
            }
            pointResponses.add(eventPointHistory);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("points", pointResponses);
        response.put("currentPage", pagePoints.getNumber());
        response.put("totalItems", pagePoints.getTotalElements());
        response.put("totalPages", pagePoints.getTotalPages());
        return response;
    }

    @Override
    public Integer getTotalEarnedByServiceId(String serviceId) throws Exception {
        return pointRepository.getTotalEarnedByServiceId(serviceId);
    }

}
