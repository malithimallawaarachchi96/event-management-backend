package com.event.management.dto;

import com.event.management.enums.AttendanceStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceRequestDto {
    private UUID userId;
    private AttendanceStatus status;
}
