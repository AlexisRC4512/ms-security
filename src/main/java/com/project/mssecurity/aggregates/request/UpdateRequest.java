package com.project.mssecurity.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
    private String name;
    private String lastName;
    private String middleName;
    private String password;
    private String email;
}
