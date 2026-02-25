package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDTO {
    private String name;         // "Algebra"
    private double averageScore; // e.g. 75
    private int questions;       // e.g. 20
}