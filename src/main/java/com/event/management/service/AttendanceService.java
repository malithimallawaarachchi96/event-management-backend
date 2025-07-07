package com.event.management.service;

import java.util.List;
import java.util.UUID;

import com.event.management.dto.AttendanceRequestDto;
import com.event.management.model.Attendance;

public interface AttendanceService {
    void markAttendance(UUID eventId, AttendanceRequestDto dto);

    List<Attendance> getAttendees(UUID eventId);
}
