package com.bezkoder.springjwt.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_question_usage")
@IdClass(TeacherQuestionUsage.PK.class)
@Getter
@Setter
@NoArgsConstructor
public class TeacherQuestionUsage {

    @Id
    private Long     teacherId;
    @Id
    private Integer  questionId;

    private LocalDateTime usedAt;

    @Data
    public static class PK implements Serializable {
        private Long teacherId;
        private Integer questionId;
    }
}