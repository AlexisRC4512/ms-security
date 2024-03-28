package com.project.mssecurity.service;

import com.project.mssecurity.aggregates.request.NewPassword;
import com.project.mssecurity.aggregates.request.SignInRequest;
import com.project.mssecurity.aggregates.request.UserRequest;
import com.project.mssecurity.aggregates.response.AuthenticationResponse;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.aggregates.response.ReniecResponse;
import com.project.mssecurity.entity.UserEntity;

public interface AuthenticationService {
    BaseResponse signUpUser(UserRequest userRequest);
    AuthenticationResponse signin(SignInRequest signInRequest);
    BaseResponse recoveredPassword(String randomNumber,String email ,NewPassword newPassword);

    BaseResponse getRandomNumber(String Email);
    ReniecResponse getInfoReniec(String numero);


}
