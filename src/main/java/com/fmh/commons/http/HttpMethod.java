package com.fmh.commons.http;

public enum  HttpMethod {
    GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE");

    private String val;

    HttpMethod(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
