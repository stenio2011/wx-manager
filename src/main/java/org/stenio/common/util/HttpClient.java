package org.stenio.common.util;


import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bjhexin3 on 2017/2/7.
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static String get(String url) {
        logger.info("send get request to url : {}", url);
        HttpGet httpGet = new HttpGet(url);
        return invoke(httpGet);
    }

    public static <T> T get(String url, Class<T> type) {
        String response = get(url);
        return jsonToObject(response, type);
    }

    public static String get(String url, Map<String, Object> params) {
        url = buildURL(url, params);
        return get(url);
    }

    private static String buildURL(String url, Map<String, Object> params) {
        String urlResult = null;
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(url);
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value instanceof List) {
                    List values = (List) value;
                    for (Object val : values) {
                        builder.addParameter(key, val.toString());
                    }
                } else {
                    builder.addParameter(key, value.toString());
                }
            }
            URI uri = builder.build();
            urlResult = uri.toURL().toString();
        } catch (URISyntaxException e) {
            logger.error("error uri syntax", e);
        } catch (MalformedURLException e) {
            logger.error("malformed url", e);
        }
        return urlResult;
    }

    public static <T> T get(String url, Map<String, Object> params, Class<T> type) {
        String response = get(url, params);
        return jsonToObject(response, type);
    }

    public static String postForm(String url) {
        HttpPost httpPost = new HttpPost(url);
        return invoke(httpPost);
    }

    public static <T> T postForm(String url, Class<T> type) {
        HttpPost httpPost = new HttpPost(url);
        String response = invoke(httpPost);
        return jsonToObject(response, type);
    }

    public static String postForm(String url, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof List) {
                List values = (List) value;
                for (Object val : values) {
                    nvps.add(new BasicNameValuePair(key, val.toString()));
                }
            } else {
                nvps.add(new BasicNameValuePair(key, value.toString()));
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
        httpPost.setEntity(entity);
        return invoke(httpPost);
    }

    public static <T> T postForm(String url, Map<String, Object> params, Class<T> type) {
        String response = postForm(url, params);
        return jsonToObject(response, type);
    }

    public static String postForm(String url, Object paramObj) {
        throw new IllegalStateException("has not impl method");
    }

    public static <T> T postForm(String url, Object paramObj, Class<T> type) {
        throw new IllegalStateException("has not impl method");
    }

    public static String postJson(String url, String json) {
        JSONEntity entity = new JSONEntity(json);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        return invoke(httpPost);
    }

    public static <T> T postJson(String url, String json, Class<T> type) {
        String response = postJson(url, json);
        return jsonToObject(response, type);
    }

    public static String postJson(String url, Map<String, Object> params) {
        String json = objectToJson(params);
        return postJson(url, json);

    }

    public static <T> T postJson(String url, Map<String, Object> params, Class<T> type) {
        String response = postJson(url, params);
        return jsonToObject(response, type);
    }

    public static String postJson(String url, Object ParamObj) {
        String json = objectToJson(ParamObj);
        return postJson(url, json);
    }

    public static <T> T postJson(String url, Object ParamObj, Class<T> type) {
        String json = objectToJson(ParamObj);
        String response = postJson(url, json);
        return jsonToObject(response, type);
    }

    private static String invoke(HttpUriRequest request) {
        HttpResponse httpResponse = sendRequest(request);
        String entityString = parseResponse(httpResponse);
        return entityString;
    }

    private static HttpResponse sendRequest(HttpUriRequest request) {
        logger.info("execute request...");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (ClientProtocolException e) {
            logger.error("client protocol exception", e);
        } catch (IOException e) {
            logger.error("io exception", e);
        }
        return response;
    }

    private static String parseResponse(HttpResponse response) {
        logger.info("get response from http server..");
        CloseableHttpResponse closeableHttpResponse = null;
        String body = null;
        try {
            if (response instanceof CloseableHttpResponse) {
                closeableHttpResponse = (CloseableHttpResponse) response;
            }
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 300) {
                logger.error("invalid request, status code : {}", statusLine.getStatusCode());
                return null;
            }
            HttpEntity entity = response.getEntity();
            logger.info("response status: " + response.getStatusLine());
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset() == null ? Consts.UTF_8 : contentType.getCharset();
            body = EntityUtils.toString(entity, charset);
            logger.info(body);
        } catch (ParseException e) {
            logger.error("parse exception", e);
        } catch (IOException e) {
            logger.error("io exception", e);
        } finally {
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {

                }
            }
        }
        return body;
    }

    private static <T> T jsonToObject(String jsonStr, Class<T> type) {
        return JsonUtil.parse(jsonStr, type);
    }

    private static <T> String objectToJson(T obj) {
        return JsonUtil.toJson(obj);
    }
}
