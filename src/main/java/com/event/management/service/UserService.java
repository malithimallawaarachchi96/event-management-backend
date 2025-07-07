package com.event.management.service;

import com.event.management.dto.UserResponseDto;
import com.event.management.model.User;

import java.util.UUID;

public interface UserService {
    UserResponseDto getUserProfileByEmail(String email);
    User getUserById(UUID id);
} 