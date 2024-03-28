package com.project.mssecurity.service.impl;

import com.project.mssecurity.aggregates.constants.Constant;
import com.project.mssecurity.aggregates.request.UpdateRequest;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.aggregates.response.ReniecResponse;
import com.project.mssecurity.aggregates.response.ResponseUser;
import com.project.mssecurity.entity.UserEntity;
import com.project.mssecurity.entity.UserTypeEntity;
import com.project.mssecurity.feignClient.ReniecClient;
import com.project.mssecurity.repository.UserRepository;
import com.project.mssecurity.repository.UserTypeRepository;
import com.project.mssecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository usuarioRepository;
    private final UserTypeRepository userTypeRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return usuarioRepository.findByEmail(username).orElseThrow( ()->
                        new UsernameNotFoundException("user not found "));
            }
        };
    }

        @Override
        public BaseResponse deleteUser(Long id) {
            try {
                Optional<UserEntity>userEntity=usuarioRepository.findById(id);
                if (userEntity.isPresent()){
                    LocalDateTime now = LocalDateTime.now();
                    Timestamp timestamp = Timestamp.valueOf(now);
                    userEntity.get().setDateDelet(timestamp);
                    userEntity.get().setState(0);
                    userEntity.get().setUsuaDelet(userEntity.get().getFirstName()+" "+userEntity.get().getLastName());
                    UserEntity userDelete=usuarioRepository.save(userEntity.get());
                    return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(userDelete));
                }else {
                    return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
                }
            }catch (Exception exception)
            {
                log.error(exception.getMessage());
                return null;
            }
        }

    @Override
    public BaseResponse updateUser(Long id , UpdateRequest updateRequest) {
        try {
            Optional<UserEntity>userEntity=usuarioRepository.findById(id);
            if (userEntity.isPresent()){
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);
                userEntity.get().setDateModif(timestamp);
                userEntity.get().setFirstName(updateRequest.getName());
                userEntity.get().setLastName(updateRequest.getLastName());
                userEntity.get().setMiddleName(updateRequest.getMiddleName());
                userEntity.get().setEmail(updateRequest.getEmail());
                userEntity.get().setPassword((new BCryptPasswordEncoder().encode(updateRequest.getPassword())));
                userEntity.get().setUsuaModif(updateRequest.getName()+" "+updateRequest.getLastName());
                return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(userEntity.get()));
            }else {
                return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
            }
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public BaseResponse getUserForID(Long id) {
        try {
            Optional<UserEntity>userEntity=usuarioRepository.findById(id);
            if (userEntity.isPresent()){
                return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(userEntity.get()));
            }else {
                return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
            }
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public BaseResponse getUserForEmail(String Email) {
        try {
            Optional<UserEntity>userEntity=usuarioRepository.findByEmail(Email);
            if (userEntity.isPresent()){
                List<String> roleUser = userEntity.get().getUserTypeId().stream()
                        .findFirst()
                        .map(UserTypeEntity::getRoleUser)
                        .map(List::of)
                        .orElse(List.of());
                ResponseUser responseUser=  ResponseUser.builder()
                        .id(userEntity.get().getId()).firstName(userEntity.get().getFirstName()).lastName(userEntity.get().getLastName()).middleName(userEntity.get().getMiddleName()).email(userEntity.get().getEmail()).numDoc(userEntity.get().getNumDoc())
                        .password(userEntity.get().getPassword()).RoleUser(roleUser).build();
                return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(responseUser));
            }else {
                return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
            }
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return null;
        }
    }


}
