package com.swp.ihelp.app.point;

import com.swp.ihelp.app.point.response.PointResponse;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PointServiceImpl implements PointService {
    private PointRepository pointRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Autowired
    public PointServiceImpl(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
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
}
