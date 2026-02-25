package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString(exclude = {
        "standardMasters",
        "subjectMasters", // or any sets referencing back
        "testMasters"     // if it has that relationship
})
public class EntranceExamMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entranceExamId;

    private String image;

    private String entranceExamName;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "entrance_standard",
            joinColumns = @JoinColumn(name = "entranceExamId"),
            inverseJoinColumns = @JoinColumn(name = "standardId"))
    private Set<StandardMaster> standardMasters = new HashSet<>();
}
