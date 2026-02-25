package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.payload.request.EntranceExamRequest;
import com.bezkoder.springjwt.payload.request.StudentEntranceExamRequest;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentMappedData {
    private Long studentId;
    private Long teacherId;
    private List<StudentEntranceExamRequest> entranceExamRequests;

}
