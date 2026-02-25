package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContents {
    private Integer id;
    private String url;
    private String type;
    private String title;
    private String description;
}
