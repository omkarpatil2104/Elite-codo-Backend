package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class VerificationOTPRequest {
    private Long id;
    private Integer otp;
}
