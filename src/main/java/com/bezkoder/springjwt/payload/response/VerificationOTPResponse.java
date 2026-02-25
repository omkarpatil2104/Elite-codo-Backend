package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationOTPResponse {
    private Long id;
    private String message;
    private Boolean flag;
}
