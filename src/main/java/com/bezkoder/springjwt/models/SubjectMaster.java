package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "subject_master")
@ToString(exclude = {
        "standardMaster",   // set of StandardMaster
        "entranceExamMaster"
})
public class SubjectMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subjectId;

    private String subjectName;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Subject_Standared",
            joinColumns = @JoinColumn(name = "subjectId"),
            inverseJoinColumns = @JoinColumn(name = "standardId"))
    private Set<StandardMaster> standardMaster = new HashSet<>();


    @ManyToOne
    private EntranceExamMaster entranceExamMaster;
}
