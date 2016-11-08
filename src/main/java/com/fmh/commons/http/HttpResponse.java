package com.fmh.commons.http;

import org.apache.http.Header;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class HttpResponse {
    private String url = null;
    private Header[] headers = null;
    private int statusCode = 0;
    private byte[] content = null;
    private ContentType contentType = null;
    private HttpProxy proxy = null;

    public HttpResponse(int statusCode, HttpProxy proxy){
        this.statusCode =statusCode;
        this.proxy = proxy;
    }

    public HttpResponse(HttpClientContext context, int statusCode)throws IOException{

    }
}
