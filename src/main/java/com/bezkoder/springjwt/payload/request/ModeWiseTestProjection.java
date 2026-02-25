package com.bezkoder.springjwt.payload.request;

import java.util.Date;

public interface ModeWiseTestProjection {
    Integer getTestId();
    String getTestName();
    Date getTestDate();
    Double getMarks();
    String getStatus();
}
