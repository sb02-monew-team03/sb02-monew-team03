package com.team03.monew.controller;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> register (@RequestBody @Valid UserRegisterRequest request) {
        UserDto userDto = userService.register(request);
        return ResponseEntity.status(201).body(userDto);
    }
}
