package com.fmh.commons.http;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpResponse {
    private static final Map<String, String> charsetMap = new HashMap<>();

    static {
        charsetMap.put("iso-88591", "iso-8859-1");
    }


    private String url = null;
    private Header[] headers = null;
    private int statusCode = 0;
    private byte[] content = null;
    private ContentType contentType = null;
    private HttpProxy proxy = null;


    private static String adapterCharset(String charset) {
        if (charset.startsWith("\"") && charset.endsWith("\"")) {
            return charset.substring(charset.indexOf("\"") + 1, charset.indexOf("\""));
        }
        String result = charsetMap.get(charset);
        return result == null ? charset : result;
    }

    public HttpResponse(int statusCode, HttpProxy proxy) {
        this.statusCode = statusCode;
        this.proxy = proxy;
    }

    public HttpResponse(HttpClientContext context, int statusCode) throws IOException {
        this.statusCode = statusCode;
        if (context != null) {
            HttpHost host = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
            HttpRequest request = (HttpRequest) context.getAttribute(HttpClientContext.HTTP_REQUEST);
            String hostUri = host.toURI();
            String requestLine = request.getRequestLine().getUri();
            while (requestLine.startsWith(hostUri)) {
                requestLine = requestLine.substring(hostUri.length());
            }
            while (hostUri.endsWith("http")) {
                hostUri = hostUri.substring(0, hostUri.length() - 4);
            }
            url = hostUri + requestLine;
            org.apache.http.HttpResponse response = (org.apache.http.HttpResponse) context.getAttribute(HttpClientContext.HTTP_RESPONSE);

            headers = response.getAllHeaders();
            if (response.getEntity() == null) {
                content = null;
            } else {
                content = toByteArray(response.getEntity());
                if (response.getEntity().getContentType() != null) {
                    String tmp = response.getEntity().getContentType().getValue();
                    if (tmp.indexOf(';') != -1) {
                        contentType = ContentType.create(tmp.substring(0, tmp.indexOf(';')).trim(),
                                adapterCharset(tmp.substring(tmp.lastIndexOf("=") + 1).trim()));
                    } else {
                        String contentTypeStr = tmp.trim();
                        if ("text/html".equals(contentTypeStr)) {
                            contentType = ContentType.create(contentTypeStr, searchContentCharset_html());
                        } else if ("application/xml".equals(contentTypeStr)) {
                            contentType = ContentType.create(contentTypeStr, searchContentCharset_xml());
                        } else if ("text/xml".equals(contentTypeStr)) {
                            contentType = ContentType.create(contentTypeStr, "US-ASCII");
                        } else {
                            contentType = ContentType.create(contentTypeStr);
                        }
                    }
                }
            }
        }
    }

    public static int indexOf(String source, String regex) {
        Pattern pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(source);
        int index = -1;
        if (mat.find()) {
            index = mat.start();
        }
        return index;
    }

    private String searchContentCharset_html() throws UnsupportedEncodingException {
        String tmp = new String(content, "ISO-8859-1");
        int index1 = indexOf(tmp, "<head>");
        int index2 = indexOf(tmp, "</head>");

        if (index1 == -1 && index2 != -1) {
            index1 = 0;
        }

        if (index1 != -1 && index2 != -1 && index1 < index2) {
            Document doc = Jsoup.parse(tmp.substring(index1, index2 + "</head>".length()), "ISO-8859-1");
            Element meta = doc.select("meta[content*=charset]").first();
            if (meta != null) {
                tmp = meta.attr("content");
                index1 = indexOf(tmp, "charset");
                index1 = tmp.indexOf("=", index1);
                return adapterCharset(tmp.substring(index1 + 1).trim());
            }
            meta = doc.select("meta[http-equiv=charset][content]").first();
            if (meta != null) {
                return adapterCharset(meta.attr("content").trim());
            }
            meta = doc.select("meta[charset]").first();

            if (meta != null) {
                return adapterCharset(meta.attr("charset").trim());
            }
            return null;
        } else {
            return null;
        }
    }

    private String searchContentCharset_xml() throws UnsupportedEncodingException {
        String tmp = new String(content, "ISO-8859-1");
        if (tmp.indexOf("<?xml") == 0) {
            int index1 = tmp.indexOf("encoding");
            int index2 = tmp.indexOf("?>");
            if (index1 != -1 && index2 != -1 && index1 < index2) {
                index1 = tmp.indexOf("\"", index1);
                index2 = tmp.indexOf("\"", index1 + 1);
                if (index1 != -1 && index2 != -1 && index1 < index2) {
                    return tmp.substring(index1 + 1, index2);
                }
            }
        }
        return null;
    }


    private static byte[] toByteArray(final HttpEntity entity) throws IOException {
        Args.notNull(entity, "entity");
        final InputStream inputStream = entity.getContent();
        if (inputStream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE, "HTTP entity too large to be buffered in memory");

            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            final ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            final byte[] temp = new byte[4096];
            int l;
            while ((l = inputStream.read(temp)) != -1) {
                buffer.append(temp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getContent() {
        return content.clone();
    }

    public Charset getContentCharset() {
        return contentType == null ? null : contentType.getCharset();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Document getDocument() {
        return getDocument(null);
    }

    public Document getDocument(Charset charset) {
        if (content == null) {
            return null;
        } else {
            if (contentType == null) {
                if (charset == null) {
                    return Jsoup.parse(new String(content, Charset.forName("ISO-8859-1")), url, Parser.htmlParser());
                } else {
                    return Jsoup.parse(new String(content, charset), url, Parser.htmlParser());
                }
            } else {
                if (charset == null) {
                    if (contentType.getCharset() == null) {
                        if (contentType.getMimeType() == null) {
                            return Jsoup.parse(new String(content, Charset.forName("ISO-8859-1")), url, Parser.htmlParser());
                        } else {
                            return Jsoup.parse(new String(content, Charset.forName("ISO-8859-1")), url,
                                    "text/html".equals(contentType.getMimeType()) ? Parser.htmlParser() : Parser.xmlParser());
                        }
                    } else {
                        if (contentType.getMimeType() == null) {
                            return Jsoup.parse(new String(content, contentType.getCharset()), url, Parser.htmlParser());
                        } else {
                            return Jsoup.parse(new String(content, contentType.getCharset()), url,
                                    "text/html".equals(contentType.getMimeType()) ?
                                            Parser.htmlParser() : Parser.xmlParser());
                        }
                    }
                } else {
                    if (contentType.getCharset() == null) {
                        if (contentType.getMimeType() == null) {
                            return Jsoup.parse(new String(content, charset), url, Parser.htmlParser());
                        } else {
                            return Jsoup.parse(new String(content, charset), url,
                                    "text/html".equals(contentType.getMimeType()) ?
                                            Parser.htmlParser() : Parser.xmlParser());
                        }
                    } else {
                        if (contentType.getMimeType() == null) {
                            return Jsoup.parse(new String(content, charset), url, Parser.htmlParser());
                        } else {
                            return Jsoup.parse(new String(content,charset), url,
                                    "text/html".equals(contentType.getMimeType()) ?
                                            Parser.htmlParser() : Parser.xmlParser());
                        }
                    }
                }
            }
        }
    }

    public Header[] getHeaders(){
        return headers.clone();
    }

    public String getMD5(){
        return DigestUtils.md5Hex(content);
    }


}
