package com.event.management.service;

import com.event.management.dto.EventRequestDto;
import com.event.management.dto.EventResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventService {

    EventResponseDto createEvent(EventRequestDto dto);

    EventResponseDto updateEvent(UUID eventId, EventRequestDto dto);

    void deleteEvent(UUID eventId);

    EventResponseDto getEventById(UUID eventId);

    List<EventResponseDto> getAllEvents();

    List<EventResponseDto> filterEvents(String location, String visibility, LocalDate dateFrom, LocalDate dateTo);

    List<EventResponseDto> getUpcomingEvents();

    List<EventResponseDto> getEventsByUserId(UUID userId);
}
