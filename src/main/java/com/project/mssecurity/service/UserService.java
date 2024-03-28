package com.project.mssecurity.service;

import com.project.mssecurity.aggregates.request.NewPassword;
import com.project.mssecurity.aggregates.request.UpdateRequest;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.aggregates.response.ReniecResponse;
import com.project.mssecurity.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    BaseResponse deleteUser(Long id);
    BaseResponse updateUser(Long id, UpdateRequest updateRequest);
    BaseResponse getUserForID(Long id);
    BaseResponse getUserForEmail(String Email);
}
