package com.team03.monew.controller;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.dto.user.UserUpdateRequest;
import com.team03.monew.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserRegisterRequest request) {
        UserDto userDto = userService.register(request);
        return ResponseEntity.status(201).body(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateNickname(
        @PathVariable UUID userId,
        @RequestBody @Valid UserUpdateRequest request,
        @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        UserDto updated = userService.updateNickname(userId, requesterId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        userService.deleteUser(userId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> deleteUserHard(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        userService.deleteUserHard(userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
