package com.event.management.mapper;

import com.event.management.dto.EventRequestDto;
import com.event.management.dto.EventResponseDto;
import com.event.management.model.Event;
import com.event.management.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventMapper {

    // Convert Request DTO to Entity
    public static Event toEntity(EventRequestDto dto, User host) {
        return Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .visibility(dto.getVisibility())
                .host(host)
                .deleted(false)
                .build();
    }

    // Convert Entity to Response DTO
    public static EventResponseDto toDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .visibility(event.getVisibility())
                .hostId(event.getHost() != null ? event.getHost().getId() : null)
                .hostName(event.getHost() != null ? event.getHost().getName() : null)
                .attendeeCount(0) // Default value when not provided
                .build();
    }

    // Convert Entity to Response DTO with attendee count
    public static EventResponseDto toDto(Event event, long attendeeCount) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .visibility(event.getVisibility())
                .hostId(event.getHost() != null ? event.getHost().getId() : null)
                .hostName(event.getHost() != null ? event.getHost().getName() : null)
                .attendeeCount(attendeeCount)
                .build();

    }

    // Update existing Event entity with new data
    public static void updateEntityFromDto(EventRequestDto dto, Event event, User host) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setVisibility(dto.getVisibility());
        event.setHost(host);
        event.setUpdatedAt(LocalDateTime.now());
    }
}
