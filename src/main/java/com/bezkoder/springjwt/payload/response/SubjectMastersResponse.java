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
public class SubjectMastersResponse {
    private Integer subjectId;

    private String subjectName;

    private Date date;

    private String status;
}
