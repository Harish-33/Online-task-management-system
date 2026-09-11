package com.taskmanagement.service;

import com.taskmanagement.dto.request.UserCreateRequest;
import com.taskmanagement.dto.response.UserResponse;
import com.taskmanagement.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    User getUserEntityById(Long id);
}
