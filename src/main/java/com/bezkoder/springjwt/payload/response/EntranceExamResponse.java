package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntranceExamResponse {
    private Integer entranceExamId;

    private String image;

    private String entranceExamName;

    private String status;
}
