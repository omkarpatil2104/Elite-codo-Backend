package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class PatternMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer patternId;

    private String patternName;

    private String patternActualName;

    private String status;

    @Temporal(TemporalType.DATE)
    private Date date;


//    @ManyToMany(mappedBy = "assignedPatterns", fetch = FetchType.LAZY)
//    private Set<User> assignedTeachers = new HashSet<>();

}
