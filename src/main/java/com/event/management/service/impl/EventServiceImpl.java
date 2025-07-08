package com.event.management.service.impl;

import com.event.management.dto.EventRequestDto;
import com.event.management.dto.EventResponseDto;
import com.event.management.exception.ResourceNotFoundException;
import com.event.management.mapper.EventMapper;
import com.event.management.model.Attendance;
import com.event.management.model.Event;
import com.event.management.model.User;
import com.event.management.repository.EventRepository;
import com.event.management.repository.UserRepository;
import com.event.management.repository.AttendanceRepository;
import com.event.management.enums.AttendanceStatus;
import com.event.management.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

        private final EventRepository eventRepository;
        private final UserRepository userRepository;
        private final AttendanceRepository attendanceRepository;

        @Override
        public EventResponseDto createEvent(EventRequestDto dto) {
                log.info("Creating new event with title: {}", dto.getTitle());

                User host = userRepository.findById(dto.getHostId())
                                .orElseThrow(() -> new ResourceNotFoundException("Host user not found"));

                Event event = EventMapper.toEntity(dto, host);
                var currentTime = LocalDateTime.now();
                event.setCreatedAt(currentTime);
                event.setUpdatedAt(currentTime);

                Event saved = eventRepository.save(event);
                log.info("Event created successfully with ID: {}", saved.getId());
                return EventMapper.toDto(saved);
        }

        @Override
        public EventResponseDto updateEvent(UUID eventId, EventRequestDto dto) {
                log.info("Updating event with ID: {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

                User host = userRepository.findById(dto.getHostId())
                                .orElseThrow(() -> new ResourceNotFoundException("Host user not found"));

                EventMapper.updateEntityFromDto(dto, event, host);

                Event saved = eventRepository.save(event);
                log.info("Event updated successfully with ID: {}", saved.getId());

                return EventMapper.toDto(saved);
        }

        @Override
        public void deleteEvent(UUID eventId) {
                log.info("Deleting event with ID: {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

                // Delete all attendance records for this event
                List<Attendance> attendances = attendanceRepository.findByEvent(event);
                if (!attendances.isEmpty()) {
                        attendanceRepository.deleteAll(attendances);
                        log.info("Deleted {} attendance records for event ID: {}", attendances.size(), eventId);
                }

                event.setDeleted(true);
                eventRepository.save(event);

                log.info("Event deleted successfully with ID: {}", eventId);
        }

        @Override
        public EventResponseDto getEventById(UUID eventId) {
                log.info("Fetching event with ID: {}", eventId);

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

                log.debug("Retrieved event: {}", event);

                long attendeeCount = attendanceRepository.findByEvent(event).stream()
                                .filter(a -> a.getStatus() == AttendanceStatus.GOING)
                                .count();

                return EventMapper.toDto(event, attendeeCount);
        }

        @Override
        public List<EventResponseDto> getAllEvents() {
                log.info("Fetching all events");

                List<Event> events = eventRepository.findAll().stream()
                                .filter(e -> !e.isDeleted())
                                .collect(Collectors.toList());

                // Fetch all attendance data in a single query
                Map<UUID, Long> eventAttendeeCounts = getEventAttendeeCounts(events);

                return events.stream()
                                .map(event -> {
                                        long attendeeCount = eventAttendeeCounts.getOrDefault(event.getId(), 0L);
                                        return EventMapper.toDto(event, attendeeCount);
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EventResponseDto> filterEvents(String location, String visibility, LocalDate dateFrom,
                        LocalDate dateTo) {
                List<Event> all = eventRepository.findAllNotDeleted();

                List<Event> filtered = all.stream()
                                .filter(e -> location == null || e.getLocation().equalsIgnoreCase(location))
                                .filter(e -> visibility == null
                                                || e.getVisibility().name().equalsIgnoreCase(visibility))
                                .filter(e -> dateFrom == null || !e.getStartTime().toLocalDate().isBefore(dateFrom))
                                .filter(e -> dateTo == null || !e.getEndTime().toLocalDate().isAfter(dateTo))
                                .collect(Collectors.toList());

                Map<UUID, Long> eventAttendeeCounts = getEventAttendeeCounts(filtered);

                return filtered.stream()
                                .map(event -> {
                                        long attendeeCount = eventAttendeeCounts.getOrDefault(event.getId(), 0L);
                                        return EventMapper.toDto(event, attendeeCount);
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EventResponseDto> getUpcomingEvents() {
                log.info("Fetching upcoming events");

                List<Event> events = eventRepository.findByStartTimeAfterAndDeletedFalse(LocalDateTime.now());

                log.debug("Retrieved {} upcoming events: {}", events.size(), events);

                // Fetch all attendance data in a single query
                Map<UUID, Long> eventAttendeeCounts = getEventAttendeeCounts(events);

                return events.stream()
                                .map(event -> {
                                        long attendeeCount = eventAttendeeCounts.getOrDefault(event.getId(), 0L);
                                        return EventMapper.toDto(event, attendeeCount);
                                })
                                .collect(Collectors.toList());
        }

        // Helper method to fetch attendee counts for multiple events efficiently
        private Map<UUID, Long> getEventAttendeeCounts(List<Event> events) {
                if (events.isEmpty()) {
                        return Map.of();
                }

                try {
                        log.info("eventIds {}", events.stream().map(Event::getId).collect(Collectors.toList()));
                        return attendanceRepository.findByEventIn(events).stream()
                                        .filter(a -> a.getStatus() == AttendanceStatus.GOING)
                                        .collect(Collectors.groupingBy(
                                                        a -> a.getEvent().getId(),
                                                        Collectors.counting()));
                } catch (Exception e) {
                        log.warn("Could not fetch attendee counts: {}", e.getMessage());
                        return Map.of();
                }
        }

        @Override
        public List<EventResponseDto> getEventsByUserId(UUID userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Get hosted events that are not deleted
                List<Event> hosted = eventRepository.findByHostAndDeletedFalse(user);
                
                // Get attending events that are not deleted
                List<Attendance> attending = attendanceRepository.findByUser(user);
                List<Event> attendingEvents = attending.stream()
                                .map(Attendance::getEvent)
                                .filter(event -> !event.isDeleted())
                                .collect(Collectors.toList());

                Set<Event> allEvents = new HashSet<>(hosted);
                allEvents.addAll(attendingEvents);

                return allEvents.stream()
                                .map(EventMapper::toDto)
                                .collect(Collectors.toList());
        }

}
