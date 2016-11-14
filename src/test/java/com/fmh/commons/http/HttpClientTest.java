package com.fmh.commons.http;

import org.junit.Test;

public class HttpClientTest {

    @Test
    public void T1(){
        HttpClient httpClient = new HttpClient();
        HttpResponse httpResponse = httpClient.get("http://news.baidu.com/");
        System.out.println(httpResponse);
    }
}
