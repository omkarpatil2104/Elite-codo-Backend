package com.bezkoder.springjwt.payload.response;


import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandardResponse {

    private Integer entranceExamId;

    private String entranceExamName;

    private List<StandardMaster> standardMasters;
}
