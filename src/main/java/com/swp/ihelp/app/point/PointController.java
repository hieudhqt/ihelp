package com.swp.ihelp.app.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PointController {
    private PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/points/{email}")
    public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = pointService.findByEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/points/event/{eventId}")
    public ResponseEntity<Map<String, Object>> findByEventId(@PathVariable String eventId,
                                                             @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = pointService.getHistoryByEventId(eventId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/points/service/total/{serviceId}")
    public ResponseEntity<Integer> getTotalEarnedByServiceId(@PathVariable String serviceId) throws Exception {
        Integer total = pointService.getTotalEarnedByServiceId(serviceId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}
