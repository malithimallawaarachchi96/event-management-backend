package com.event.management.repository;

import com.event.management.model.Attendance;
import com.event.management.model.Event;
import com.event.management.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends CrudRepository<Attendance, UUID> {
    List<Attendance> findByEvent(Event event);

    List<Attendance> findByUser(User user);

    Optional<Attendance> findByUserAndEvent(User user, Event event);

    List<Attendance> findByEventIn(List<Event> events);
}
