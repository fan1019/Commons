package com.fmh.commons.http;

import org.junit.Test;

public class HttpClientTest {

    @Test
    public void T1(){
        HttpClient httpClient = new HttpClient();
        HttpResponse httpResponse = httpClient.get("http://news.baidu.com/");
        System.out.println(httpResponse.getContentCharset());
        System.out.println(httpResponse.getDocument());
        System.out.println(httpResponse.getStatusCode());
    }

    @Test
    public void T2(){
        HttpClient httpClient = new HttpClient();
        HttpResponse httpResponse = httpClient.get("http://news.baidu.com/");
        System.out.println(httpResponse.getDocument());
    }
    @Test
    public void T3(){
        HttpClient httpClient = new HttpClient();
        HttpResponse httpResponse = httpClient.get("http://192.168.0.57:18080/solr/gbi_clinicaltrial/select?q=*%3A*&wt=json&indent=true");
        System.out.println(httpResponse.getDocument());
    }
}
