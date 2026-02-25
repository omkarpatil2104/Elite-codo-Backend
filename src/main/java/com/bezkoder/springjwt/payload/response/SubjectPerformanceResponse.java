package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectPerformanceResponse {
    private double value;
    private String name;
    private ItemStyle itemStyle = new ItemStyle();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemStyle {
        private String color;
    }
}
