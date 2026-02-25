package com.bezkoder.springjwt.models;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class StudentTestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long studentTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private TestMaster testMaster;

}
