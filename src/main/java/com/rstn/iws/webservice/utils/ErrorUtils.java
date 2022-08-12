package com.rstn.iws.webservice.utils;


import org.apache.commons.lang.exception.ExceptionUtils;


public class ErrorUtils {
    public static String stackTraceToString(Throwable ex) {
        return ExceptionUtils.getFullStackTrace(ex);
    }

}
