package com.sandi.jasperreports.dto;

import com.sun.istack.NotNull;
import lombok.Value;

@Value
public class LoginRequest {

    @NotNull
    String username;
    @NotNull
    String password;

}
