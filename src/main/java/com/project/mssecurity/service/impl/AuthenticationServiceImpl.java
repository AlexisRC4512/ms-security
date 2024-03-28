package com.project.mssecurity.service.impl;

import com.project.mssecurity.aggregates.constants.Constant;
import com.project.mssecurity.aggregates.constants.RoleUser;
import com.project.mssecurity.aggregates.request.NewPassword;
import com.project.mssecurity.aggregates.request.SignInRequest;
import com.project.mssecurity.aggregates.request.UserRequest;
import com.project.mssecurity.aggregates.response.AuthenticationResponse;
import com.project.mssecurity.aggregates.response.BaseResponse;
import com.project.mssecurity.aggregates.response.ReniecResponse;
import com.project.mssecurity.entity.DocumentTypeEntity;
import com.project.mssecurity.entity.UserEntity;
import com.project.mssecurity.entity.UserTypeEntity;
import com.project.mssecurity.feignClient.ReniecClient;
import com.project.mssecurity.redis.RedisService;
import com.project.mssecurity.repository.DocumentTypeRepository;
import com.project.mssecurity.repository.UserRepository;
import com.project.mssecurity.repository.UserTypeRepository;
import com.project.mssecurity.service.AuthenticationService;
import com.project.mssecurity.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.*;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ReniecClient reniecClient;
    private final JavaMailSender mail;
    private final UserTypeRepository userTypeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RedisService redisService;
    @Value("${token.api}")
    private String tokenApi;
    @Override
    public BaseResponse signUpUser(UserRequest userRequest) {
        try {
            Optional<UserEntity> userByEmail = userRepository.findByEmail(userRequest.getEmail());
            Set<UserTypeEntity> userTypeEntityList= new HashSet<>();
            Optional<UserEntity> validatorDNINumber=userRepository.findByNumDoc(userRequest.getNumDoc());
            if (userByEmail.isPresent()  || validatorDNINumber.isPresent()) {
                return new BaseResponse(Constant.CODE_ERROR, "Ya existe un usuario con este email o DNI", Optional.empty());
            } else {
                ReniecResponse response = getInfoReniec(userRequest.getNumDoc());
                DocumentTypeEntity documentTypeEntity= new DocumentTypeEntity();
                documentTypeEntity.setCreatedUser(response.getNombres()+" "+response.getApellidoPaterno());
                documentTypeEntity.setCodeType(userRequest.getTipoDoc());
                documentTypeEntity.setState(1);
                documentTypeRepository.save(documentTypeEntity);
                UserTypeEntity typeEntity=userTypeRepository.save(UserTypeEntity.builder().roleUser(RoleUser.DEFAULT.name()).State(1).usuaCrea(response.getNombres()+" "+response.getApellidoPaterno()).build());
                userTypeEntityList.add(typeEntity);
                UserEntity userEntity= UserEntity.builder().state(1)
                        .password(new BCryptPasswordEncoder().encode(userRequest.getPassword()))
                        .email(userRequest.getEmail()).firstName(response.getNombres())
                        .lastName(response.getApellidoPaterno()).middleName(response.getApellidoMaterno())
                        .userTypeId(userTypeEntityList)
                        .enabled(true)
                        .numDoc(userRequest.getNumDoc())
                        .accountNonExpire(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .documentType(documentTypeEntity)
                        .build();
                UserEntity userReturn=userRepository.save(userEntity);
                return new BaseResponse(Constant.CODE_SUCCESS, Constant.MESS_SUCCESS, Optional.of(userReturn));
            }
        } catch (Exception e) {
            return new BaseResponse(Constant.CODE_ERROR, e.getMessage(), Optional.empty());
        }
    }
    @Override
    public AuthenticationResponse signin(SignInRequest signInRequest) {
        if (StringUtils.isEmpty(signInRequest.getEmail()) || StringUtils.isEmpty(signInRequest.getPassword())) {
            throw new IllegalArgumentException("El nombre de usuario y la contraseña son obligatorios");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getEmail(), signInRequest.getPassword()));
            var user = userRepository.findByEmail(signInRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            var jwt = jwtService.generateToken(user);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setToken(jwt);
            redisService.saveInRedis(signInRequest.getEmail(),authenticationResponse.getToken(),2);
            return authenticationResponse;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Nombre de usuario o contraseña incorrectos", e);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error durante la autenticación", e);
        }
    }
    @Override
    public BaseResponse recoveredPassword(String randomNumber, String email, NewPassword newPassword) {
        try {
            String NumberRedis = redisService.getFromRedis("random"+email);
           Optional <UserEntity> userEntity=userRepository.findByEmail(email);
            if (randomNumber.equals(NumberRedis))
            {
                if (newPassword.getNewPassword().length()>=6 && newPassword.getNewPassword().equals(newPassword.getRepeatNewPassword()))
                {
                    userEntity.get().setPassword(new BCryptPasswordEncoder().encode(newPassword.getNewPassword()));
                    return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(userEntity.get()));

                }else {
                    return new BaseResponse(Constant.CODE_ERROR,"los password no coincide o es menor a 6 caracter",Optional.empty());
                }
            }else {
                return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
            }

        }catch (Exception exception){
            return new BaseResponse(Constant.CODE_ERROR,exception.getMessage(),Optional.empty());
        }
    }
    @Override
    public BaseResponse getRandomNumber(String Email) {
        try {
            Optional<UserEntity> userEntity=userRepository.findByEmail(Email);
            if (userEntity.isPresent()){
                int randomNumber = (int) (Math.random() * 101);
                redisService.saveInRedis("random"+Email, String.valueOf(randomNumber),10);
                sendEmail(Email, String.valueOf(randomNumber));
                return new BaseResponse(Constant.CODE_SUCCESS,Constant.MESS_SUCCESS,Optional.of(userEntity.get()));
            }else {
                return new BaseResponse(Constant.CODE_ERROR,Constant.MESS_ERROR,Optional.empty());
            }
        }catch (Exception exception)
        {
            return new BaseResponse(Constant.CODE_ERROR,exception.getMessage(),Optional.empty());
        }
    }

    @Override
    public ReniecResponse getInfoReniec(String numero) {
        try {
            ReniecResponse response = getExecution(numero);
            if(response != null){
                return response;
            }else{
                return response;
            }
        }catch (Exception exception)
        {
            return null;
        }

    }
    private void sendEmail(String Email,String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(Email);
            email.setFrom("truequeapp4512@gmail.com");
            email.setText("El numero aleatorio es :::"+message+"::::::El numero aleatorio dura 10 minutos");
            email.setSubject("Recuperacion de contraseña");
            mail.send(email);
        }catch (Exception exception)
        {
            exception.printStackTrace();
        }

    }

    private ReniecResponse getExecution(String numero){
        String authorization = "Bearer "+tokenApi;
        return reniecClient.getInfo(numero,authorization);
    }

}
