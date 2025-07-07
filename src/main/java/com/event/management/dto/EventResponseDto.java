package com.event.management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import com.event.management.enums.Visibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventResponseDto extends RepresentationModel<EventResponseDto> {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private Visibility visibility;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID hostId;
    private String hostName;
    private long attendeeCount;

}