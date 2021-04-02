package com.swp.ihelp.app.point;

import java.util.Map;

public interface PointService {
    Map<String, Object> findByEmail(String email, int page) throws Exception;
}
