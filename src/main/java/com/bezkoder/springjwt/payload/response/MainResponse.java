package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MainResponse {
    private String message;

    private Integer responseCode;

    private Boolean flag;

    private Long id;

    public MainResponse(String message, Integer responseCode, Boolean flag) {
        this.message = message;
        this.responseCode = responseCode;
        this.flag = flag;
    }
}
