package com.swp.ihelp.app.point;

import java.util.Map;

public interface PointService {
    Map<String, Object> findByEmail(String email, int page) throws Exception;

    Map<String, Object> getHistoryByEventId(String eventId, int page) throws Exception;

    Integer getTotalEarnedByServiceId(String serviceId) throws Exception;
}
