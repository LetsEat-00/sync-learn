package com.synclearn.api;

import com.synclearn.auth.AuthApplicationService;
import com.synclearn.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        User saved = authApplicationService.registerFromLocal(req.email(), req.password(), req.nickname());
        return ResponseEntity.ok(new RegisterResponse(saved.id().toString(), saved.email(), saved.nickname(), saved.createdAt().toString()));
    }

    // 요청 DTO
    public record RegisterRequest(String email, String password, String nickname) {}

    // 응답 DTO
    public record RegisterResponse(String id, String email, String nickname, String createdAt) {}
}