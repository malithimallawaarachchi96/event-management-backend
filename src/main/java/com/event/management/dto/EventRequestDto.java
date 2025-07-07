package com.event.management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.event.management.enums.Visibility;

import lombok.Data;

@Data
public class EventRequestDto {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Visibility visibility;
    private UUID hostId;
}