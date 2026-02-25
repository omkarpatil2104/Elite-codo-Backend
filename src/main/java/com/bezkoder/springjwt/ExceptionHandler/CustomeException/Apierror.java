package com.bezkoder.springjwt.ExceptionHandler.CustomeException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Apierror extends RuntimeException{

    private String errorMessage;

    private String errorCode;

    private Date date;

    public Apierror(Apierror apierror)
    {
        super(apierror.getErrorMessage());
    }

}
