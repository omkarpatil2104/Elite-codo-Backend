package com.bezkoder.springjwt.config;

import java.util.Random;

public class OtpGenerator {
    Integer otp;

    public Integer generateOtp(){
        Random random = new Random();
        int otp = random.nextInt(9000) + 1000;
        return otp;
    }
}
