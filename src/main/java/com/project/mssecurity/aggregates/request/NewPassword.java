package com.project.mssecurity.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPassword {
    private String newPassword;
    private String repeatNewPassword;
}
