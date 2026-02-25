package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardResponse1 {
    private  Integer id;
    private String name;

    public StandardResponse1() {

    }
}
