package com.project.mssecurity.controller;

import com.project.mssecurity.aggregates.request.NewPassword;
import com.project.mssecurity.aggregates.request.SignInRequest;
import com.project.mssecurity.aggregates.request.UserRequest;
import com.project.mssecurity.aggregates.response.AuthenticationResponse;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.service.AuthenticationService;
import com.project.mssecurity.service.JWTService;
import com.project.mssecurity.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OpenAPIDefinition(
        info = @Info(
                title = "API-AUTENTICACION",
                version = "1.0",
                description = "Api de autenticacion"
        )
)
@RestController
@RequestMapping("/api/v1/autenticacion")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JWTService jwtService;
    private final UserService userService;
    @Operation(summary = "Register user")
    @PostMapping("/signupuser")
    public BaseResponse signUpUser(@RequestBody UserRequest signUpRequest){
        return authenticationService.signUpUser(signUpRequest);
    }
    @Operation(summary = "Iniciar sesion user")
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(authenticationService.signin(signInRequest));
    }
    @Operation(summary = "get random number and send message")
    @PostMapping("/RandomNumber")
    public BaseResponse getRandomNumber(@RequestParam String Email){
        return authenticationService.getRandomNumber(Email);
    }
    @Operation(summary = "recover password")
    @PostMapping("/recoveredPassword")
    public BaseResponse recoveredPassword(@RequestParam String randomNumber, @RequestParam String Email, @RequestBody NewPassword newPassword){
        return authenticationService.recoveredPassword(randomNumber,Email,newPassword);
    }
    @PostMapping("/validateToken")
    public ResponseEntity<?> validateApiToken(@RequestParam String token) {
        try {
            String email = jwtService.extractUserName(token);
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(email);
            boolean isValid = jwtService.validateToken(token, userDetails);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
