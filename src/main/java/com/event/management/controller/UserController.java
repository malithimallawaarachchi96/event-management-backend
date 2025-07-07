package com.event.management.controller;

import com.event.management.dto.EventResponseDto;
import com.event.management.dto.UserResponseDto;
import com.event.management.model.User;
import com.event.management.security.CustomUserDetails;
import com.event.management.service.EventService;
import com.event.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @GetMapping("/me")
    public UserResponseDto getCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getUserProfileByEmail(userDetails.getUsername());
    }

    @GetMapping("/me/events")
    public Map<String, List<EventResponseDto>> getMyEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        // Hosted events
        List<EventResponseDto> hosted = eventService.getEventsByUserId(userId).stream()
                .filter(e -> e.getHostId().equals(userId))
                .toList();
        // Attending events (not hosted)
        List<EventResponseDto> attending = eventService.getEventsByUserId(userId).stream()
                .filter(e -> !e.getHostId().equals(userId))
                .toList();
        Map<String, List<EventResponseDto>> result = new HashMap<>();
        result.put("hosted", hosted);
        result.put("attending", attending);
        return result;
    }
} 