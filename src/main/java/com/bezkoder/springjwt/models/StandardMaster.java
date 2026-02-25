package com.bezkoder.springjwt.models;

import javax.persistence.*;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class StandardMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer standardId;

    private String standardName;

//    @ManyToMany
//    private EntranceExamMaster entranceExamMaster;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @Override
    public String toString() {
        return "StandardMaster{" +
                "standardId=" + standardId +
                ", standardName='" + standardName + '\'' +
                // do NOT include subjects, or other relationships
                '}';
    }


}
