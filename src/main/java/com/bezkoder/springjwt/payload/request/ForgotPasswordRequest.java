package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {

    private String email;
}
