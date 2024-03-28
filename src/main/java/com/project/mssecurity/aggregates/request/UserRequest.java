package com.project.mssecurity.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String tipoDoc;
    private String numDoc;
    private String password;
    private String email;
}

