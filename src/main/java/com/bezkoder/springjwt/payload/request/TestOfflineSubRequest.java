package com.bezkoder.springjwt.payload.request;
import com.bezkoder.springjwt.models.TestMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOfflineSubRequest {


        private int testId;
        private int studentId;
        private Map<String, Integer> grades;



}
