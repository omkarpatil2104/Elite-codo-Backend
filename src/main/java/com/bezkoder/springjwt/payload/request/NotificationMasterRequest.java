package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.models.User;
import lombok.Data;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
public class NotificationMasterRequest {
    private Integer notificationId;

    private Long senderId;

    private List<String> userType;

    private List<Long> recipients;

    private String subject;

    private String message;

    private Date date;

    private String status;
}
