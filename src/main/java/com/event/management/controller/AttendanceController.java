package com.event.management.controller;

import com.event.management.dto.AttendanceRequestDto;
import com.event.management.model.Attendance;
import com.event.management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // POST /api/events/{id}/attendance
    @PostMapping("/{id}/attendance")
    public ResponseEntity<String> markAttendance(
            @PathVariable UUID id,
            @RequestBody AttendanceRequestDto dto) {
        attendanceService.markAttendance(id, dto);
        return ResponseEntity.ok("Attendance recorded successfully");
    }

    // GET /api/events/{id}/attendees
    @GetMapping("/{id}/attendees")
    public ResponseEntity<List<Attendance>> getAttendees(@PathVariable UUID id) {
        return ResponseEntity.ok(attendanceService.getAttendees(id));
    }
}
