package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandardMasterResponse {
    public Integer standardId;

    private String standardName;
}
