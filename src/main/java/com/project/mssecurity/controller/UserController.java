package com.project.mssecurity.controller;

import com.project.mssecurity.aggregates.request.UpdateRequest;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@OpenAPIDefinition(
        info = @Info(
                title = "API-USER",
                version = "1.0",
                description = "Mantenimiento de usuario"
        )
)
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "select user by id")
    @GetMapping("/{id}")
    public BaseResponse getUserForId(@PathVariable("id") Long id)
    {
        return userService.getUserForID(id);
    }
    @Operation(summary = "update user by id and other parameters")
    @PutMapping("/{id}")
    public BaseResponse updateUser(@PathVariable Long id ,@RequestBody UpdateRequest updateRequest)
    {
        return userService.updateUser(id,updateRequest);
    }
    @Operation(summary = "delete user by id")
    @DeleteMapping("/{id}")
    public BaseResponse deleteUser(@PathVariable Long id)
    {
        return userService.deleteUser(id);
    }
    @Operation(summary = "obtein user for email")
    @GetMapping("/getForEmail")
    public BaseResponse getUserForEmail(@RequestParam String Email)
    {
       return userService.getUserForEmail(Email);

    }
}
