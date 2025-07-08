package com.event.management.repository;

import com.event.management.model.Event;
import com.event.management.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT e FROM Event e WHERE e.startTime > :now AND e.deleted = false")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.deleted = false")
    List<Event> findAllNotDeleted();

    @Query("SELECT e FROM Event e WHERE e.startTime > :startTime AND e.deleted = false")
    List<Event> findByStartTimeAfterAndDeletedFalse(LocalDateTime startTime);

    List<Event> findByHost(User host);

    @Query("SELECT e FROM Event e WHERE e.host = :host AND e.deleted = false")
    List<Event> findByHostAndDeletedFalse(@Param("host") User host);
}
