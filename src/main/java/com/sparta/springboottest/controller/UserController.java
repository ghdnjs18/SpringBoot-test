package com.sparta.springboottest.controller;

import com.sparta.springboottest.dto.MessageResponseDto;
import com.sparta.springboottest.dto.SignupRequestDto;
import com.sparta.springboottest.security.ValidationGroups.ValidationSequence;
import com.sparta.springboottest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<MessageResponseDto> signup(@Validated(ValidationSequence.class) @RequestBody SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }
}
