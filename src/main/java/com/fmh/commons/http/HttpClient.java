package com.fmh.commons.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient implements Closeable, AutoCloseable {

    protected static final int connectTimeout = 30000;
    protected static final int connectionRequestTimeout = 30000;
    protected static final int socketTimeout = 30000;
    protected static final int maxRedirects = 4;

    protected HttpClientContext context = null;
    protected CloseableHttpResponse response = null;
    protected CloseableHttpClient client = null;
    protected HttpRequestBase request = null;
    protected int maxRedirect = -1;
    protected int maxTimeout = -1;
    protected HttpProxy proxy = null;


    private void setHeaders(HttpRequestBase requestBase, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setRequestConfig(HttpRequestBase requestBase) {
        RequestConfig config = RequestConfig.custom()
                .setMaxRedirects(maxRedirect == -1 ? maxRedirects : maxRedirect)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setProxy(proxy == null ? null : proxy.getHttpHost())
                .build();
        requestBase.setConfig(config);
    }

    private List<NameValuePair> parseParams(Map<String, ?> params) {
        List<NameValuePair> data = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                if (entry.getValue() instanceof Collection<?>) {
                    data.addAll(((Collection<?>) entry.getValue()).stream().map(x -> new BasicNameValuePair(entry.getKey(), x.toString())).collect(Collectors.toList()));
                } else {
                    data.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
        }
        return data;
    }


    protected void execute(HttpMethod method, String url, Map<String, String> headers, List<NameValuePair> params, String encoding) {
        switch (method) {
            case POST:
                request = new HttpPost(url);
                break;
            case GET:
                request = new HttpGet(url);
                break;
            case DELETE:
                request = new HttpDelete(url);
                break;
            default:
                break;
        }
        if (params != null && request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(params, Charset.forName(encoding)));
        }
        setHeaders(request, headers);
        setRequestConfig(request);
    }

    protected void execute(HttpMethod method, String url, Map<String, String> headers, Map<String, ?> params, String encoding) {
        execute(method,url,headers,parseParams(params),encoding);
    }

    protected void execute(HttpMethod method, String url, Map<String,String> headers, Map<String,?>params){
        execute(method,url,headers,params,"utf-8");
    }

    protected void execute(String url, Map<String,String> headers, HttpEntity entity){
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        request = httpPost;
        setHeaders(request,headers);
        setRequestConfig(request);
    }

    protected void execute(String url, Map<String,String> headers, String data, ContentType type){
        execute(url,headers,new StringEntity(data,type));
    }


    @Override
    public void close() {

    }
}
