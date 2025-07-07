package com.event.management.service.impl;

import com.event.management.dto.AttendanceRequestDto;
import com.event.management.exception.ResourceNotFoundException;
import com.event.management.model.Attendance;
import com.event.management.model.Event;
import com.event.management.model.User;
import com.event.management.repository.AttendanceRepository;
import com.event.management.repository.EventRepository;
import com.event.management.repository.UserRepository;
import com.event.management.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public void markAttendance(UUID eventId, AttendanceRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already responded
        Attendance existing = attendanceRepository.findByUserAndEvent(user, event).orElse(null);

        if (existing != null) {
            existing.setStatus(dto.getStatus());
            existing.setRespondedAt(LocalDateTime.now());
            attendanceRepository.save(existing);
        } else {
            Attendance attendance = Attendance.builder()
                    .event(event)
                    .user(user)
                    .status(dto.getStatus())
                    .respondedAt(LocalDateTime.now())
                    .build();
            attendanceRepository.save(attendance);
        }
    }

    @Override
    public List<Attendance> getAttendees(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return attendanceRepository.findByEvent(event);
    }
}
