package com.event.management.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.event.management.dto.EventRequestDto;
import com.event.management.dto.EventResponseDto;
import com.event.management.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/")
    public ResponseEntity<List<EventResponseDto>> getAllEvents(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<EventResponseDto> events = eventService.filterEvents(location, visibility, dateFrom, dateTo);

        events.forEach(e -> e.add(linkTo(methodOn(EventController.class).getEvent(e.getId())).withSelfRel()));

        return ResponseEntity.ok(events);
    }

    @PostMapping("/")
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody @Valid EventRequestDto dto) {
        EventResponseDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable UUID id) {
        EventResponseDto dto = eventService.getEventById(id);
        dto.add(linkTo(methodOn(EventController.class).getEvent(id)).withSelfRel());
        dto.add(linkTo(methodOn(EventController.class).getUserEvents(dto.getHostId())).withRel("hosted-events"));
        dto.add(linkTo(methodOn(AttendanceController.class).getAttendees(id)).withRel("attendees"));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable UUID id,
            @RequestBody @Valid EventRequestDto dto) {
        EventResponseDto updatedEvent = eventService.updateEvent(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponseDto>> getUpcomingEvents() {
        List<EventResponseDto> events = eventService.getUpcomingEvents();
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDto>> getUserEvents(@PathVariable UUID userId) {
        return ResponseEntity.ok(eventService.getEventsByUserId(userId));
    }
}
