package com.bezkoder.springjwt.payload.response;

import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntranceExamMasterResponse {
    private Integer entranceExamId;

    private String entranceExamName;

    private Date date;

    private String status;
}
