package com.fmh.commons.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class HttpClient implements Closeable, AutoCloseable {

    protected static final int connectTimeout = 30000;
    protected static final int connectionRequestTimeout = 30000;
    protected static final int socketTimeout = 30000;
    protected static final int maxRedirects = 4;
    protected static final int defaultMaxTimeout = 30000;

    protected HttpClientContext context = null;
    protected CloseableHttpResponse response = null;
    protected CloseableHttpClient client = null;
    protected HttpRequestBase request = null;
    protected int maxRedirect = -1;
    protected int maxTimeout = -1;
    protected HttpProxy proxy = null;
    protected int lastStatus;
    protected CookieStore cookieStore = new BasicCookieStore();


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


    protected HttpResponse execute(HttpMethod method, String url, Map<String, String> headers, List<NameValuePair> params, String encoding) {
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
        lastStatus = 0;
        if (params != null && request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(params, Charset.forName(encoding)));
        }
        setHeaders(request, headers);
        setRequestConfig(request);
        return doRequest(method,url);
    }

    protected HttpResponse execute(HttpMethod method, String url, Map<String,String>headers, List<NameValuePair>params){
        return execute(method,url,headers,params,"utf-8");
    }

    protected HttpResponse execute(HttpMethod method, String url, Map<String, String> headers, Map<String, ?> params, String encoding) {
        return execute(method, url, headers, parseParams(params), encoding);
    }

    protected HttpResponse execute(HttpMethod method, String url, Map<String, String> headers, Map<String, ?> params) {
        return execute(method, url, headers, params, "utf-8");
    }

    protected HttpResponse execute(String url, Map<String, String> headers, HttpEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        request = httpPost;
        lastStatus = 0;
        setHeaders(request, headers);
        setRequestConfig(request);
        return doRequest(HttpMethod.POST,url);
    }

    protected HttpResponse execute(String url, Map<String, String> headers, String data, ContentType type) {
        return execute(url, headers, new StringEntity(data, type));
    }


    private String getRedirectUrl(String url, String location) {
        if (location.startsWith("http")) {
            return location;
        } else {
            String baseUrl = url;
            if (baseUrl.contains("?")) {
                baseUrl = baseUrl.substring(0, baseUrl.indexOf("?"));
            }
            int p = baseUrl.indexOf("/", 8);
            if (p != -1) {
                baseUrl = baseUrl.substring(0, p);
            }
            if (location.startsWith("/")) {
                return baseUrl + location;
            } else {
                return baseUrl + "/" + location;
            }
        }
    }

    private HttpResponse doRequest(HttpMethod method, String url) {
        FutureTask<HttpResponse> task = new FutureTask<>(() ->{
            try{
                response = client.execute(request,context);
                lastStatus = response.getStatusLine().getStatusCode();
                switch (method){
                    case POST:
                        if (lastStatus / 100 == 3){
                            String location = response.getFirstHeader("Location").getValue();
                            response.close();
                            return null;
                        }
                        break;
                    case GET:
                    case DELETE:
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                return new HttpResponse(context,lastStatus);
            }catch (ConnectTimeoutException e){
                e.printStackTrace();
                return null;
            }finally {
                if (response != null){
                    try{
                        response.close();
                    }catch (IOException e){
                       LoggerFactory.getLogger("BaseLog").error("response 关闭失败",e);
                    }
                }
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        try{
            return task.get(maxTimeout == -1 ? defaultMaxTimeout : maxTimeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("遇到系统问题",e);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }finally {
            thread.interrupt();
        }
    }

    public HttpResponse get(String url){
        return get(url,null);
    }

    public HttpResponse get(String url, Map<String,String>headers){
        return execute(HttpMethod.GET,url,headers,(List<NameValuePair>) null);

    }

    public HttpResponse post(String url,List<NameValuePair> params){
        return post(url,null,params);
    }

    public HttpResponse post(String url, Map<String,String>headers,List<NameValuePair>params){
        return execute(HttpMethod.POST,url,headers,params);
    }

    public HttpResponse post(String url, Map<String,String> headers, String params,ContentType contentType){
        return execute(url,headers,params,contentType);
    }

    public HttpResponse post(String url, String params, ContentType contentType){
        return post(url,null,params,contentType);
    }

    public HttpResponse post(String url, String params){
        return post(url,params,ContentType.TEXT_PLAIN);
    }

    public HttpResponse post(String url, Map<String,String>headers, HttpEntity entity){
        return execute(url,headers,entity);
    }

    public HttpResponse post(String url, Map<String,String> headers, Map<String,?> params, String encoding){
        return execute(HttpMethod.POST,url,headers,params,encoding);
    }

    public HttpResponse post(String url, Map<String,String> headers, Map<String,?> params){
        return post(url,headers,params,"utf-8");
    }

    public HttpResponse post(String url, Map<String,?> params){
        return post(url,null,params);
    }

    public HttpResponse delete(String url, Map<String,String> headers){
        return execute(HttpMethod.DELETE,url,headers, (List<NameValuePair>) null);
    }

    public HttpResponse delete(String url){
        return delete(url,null);
    }

    public List<Cookie> getCookies(){
        return cookieStore.getCookies();
    }

    public HttpProxy getProxy(){
        return proxy;
    }

    public void setProxy(HttpProxy proxy){
        this.proxy = proxy;
    }

    public void setProxy(String host, String port){
        this.proxy = new HttpProxy(host,Integer.parseInt(port),null);
    }

    public void setProxy(String host, int port){
        this.proxy = new HttpProxy(host,port,null);
    }

    public void setMaxTimeout(int mill){
        this.maxTimeout = mill;
    }

    public void removeProxy(){
        this.proxy = null;
    }

    @Override
    public void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            System.err.println("SimpleHttpClient close error");
        }
    }
}
