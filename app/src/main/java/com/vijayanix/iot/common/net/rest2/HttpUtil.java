package com.vijayanix.iot.common.net.rest2;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.VijStrings;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil
{
    private final static Logger log = Logger.getLogger(HttpUtil.class);
    
    private final static int SOCKET_CONNECT_RETRY_TIME = 3;
    
    private final static String KEY_STATUS = "status";
    
    public static JSONObject Get(String url, HeaderPair... headers)
    {
        return Get(url, null, headers);
    }
    
    public static JSONObject Get(String url, JSONObject json, HeaderPair... headers)
    {
        String logTag =
            Thread.currentThread().toString() + "##" + HttpLogUtil.convertToCurl(true, null, url, headers);
        log.debug(logTag);
        // create httpClient
        OkHttpClient httpclient = HttpClient.getOkHttpClient();

        // create httpGet
        Request httpget = createHttpRequest(false, true, url, json, headers);
        // execute
        JSONObject result = executeHttpRequest(httpclient, httpget, null);
        log.debug(logTag + ":result=" + result);
        return result;
    }
    
    public static JSONObject Post(String url, JSONObject json, HeaderPair... headers)
    {
        log.debug("postJSON is "+json.toString());
        String logTag =
            Thread.currentThread().toString() + "##" + HttpLogUtil.convertToCurl(false, json, url, headers);
        log.debug(logTag);
        // create httpClient
        OkHttpClient httpclient = HttpClient.getOkHttpClient();
        // create httpPost
        Request httppost = createHttpRequest(false, false, url, json, headers);
        // execute
        JSONObject result = executeHttpRequest(httpclient, httppost, null);
        log.debug(logTag + ":result=" + result);
        
        return result;
    }
    
    public static void PostInstantly(String url, JSONObject json, Runnable disconnectedCallback, HeaderPair... headers)
    {
        disconnectedCallback = null;
        String logTag =
            Thread.currentThread().toString() + "##" + HttpLogUtil.convertToCurl(false, json, url, headers);
        log.debug(logTag);
        // create httpClient
        OkHttpClient httpclient = HttpClient.getOkHttpClient();
        // create httpPost
        Request httppost = createHttpRequest(true, false, url, json, headers);
        // execute
        executeHttpRequest(httpclient, httppost, disconnectedCallback);
    }
    
    private static boolean __isHttpsSupported()
    {
        SharedPreferences sp =
            IOTApplication.sharedInstance().getSharedPreferences(VijStrings.Key.SYSTEM_CONFIG, Context.MODE_PRIVATE);
        return sp.getBoolean(VijStrings.Key.HTTPS_SUPPORT, true);
    }
    

    
    private static Request createHttpRequest(boolean isInstantly, boolean isGet, String url, JSONObject json,
                                             HeaderPair... headers)
    {
        url = encodingUrl(url);
        if (!__isHttpsSupported())
        {
            url = url.replace("https", "http");
        }

     
        Request.Builder request = new Request.Builder();
        request.url(url);



        RequestBody requestBody = null;
        if (isGet)
        {
            request = request.get();
        }
        else
        {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
            RequestBody body = null;
            try {
                body = RequestBody.create(JSON, json.toString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            request = request.post(body);
        }
        if (isInstantly)
        {
           /* BasicHttpParams params = new BasicHttpParams();
            params.setParameter(EspHttpRequest.ESP_INSTANTLY, true);
            request.setParams(params);*/     
           


        }
        // Add Headers
        for (int i = 0; i < headers.length; i++)
        {
            HeaderPair header = headers[i];
            request.addHeader(header.getName(), header.getValue());
        }
        
        
    
        return request.build();
    }
    
    private static String encodingUrl(String url)
    {
        final char[] specChars = {'+', ':'};
        
        String header;
        String body;
        if (url.startsWith("https://"))
        {
            header = "https://";
        }
        else if (url.startsWith("http://"))
        {
            header = "http://";
        }
        else
        {
            header = "";
        }
        
        body = url.substring(header.length());
        for (char specChar : specChars)
        {
            String encodedStr = encodingSpecUrlChar(specChar);
            body = body.replace("" + specChar, encodedStr);
        }
        
        return header + body;
    }
    
    private static String encodingSpecUrlChar(char c)
    {
        int asciiCode = (int)c;
        String hexStr = Integer.toHexString(asciiCode).toUpperCase(Locale.ENGLISH);
        if (hexStr.length() < 2)
        {
            hexStr = "0" + hexStr;
        }

        return "%" + hexStr;
    }
    
    public static JSONObject executeHttpRequest(OkHttpClient httpclient, Request request,
                                                Runnable disconnectedCallback)
    {
        boolean isRetry = true;
        JSONObject result = null;

            Response response;

            try {
                response = httpclient.newCall(request).execute();

            int statusCode = response.code();
                if(!response.isSuccessful())   {
                    log.info("responese is failed ,result code is " + statusCode);
	                return null;
                }

               ResponseBody responseBody = response.body();
                
                if (responseBody == null && disconnectedCallback == null)
                {
                    log.warn("executeHttpRequest responseBody == null && disconnectedCallback == null");
                }
                
                String resultStr = responseBody.string();

                if (!TextUtils.isEmpty(resultStr))
                {
                    log.info("executeHttpRequest result str = " + resultStr);
                    resultStr = unescapeHtml(resultStr);
                    try
                    {
                        result = new JSONObject(resultStr);
                        if (!result.has(KEY_STATUS))
                        {
                            result.put(KEY_STATUS, statusCode);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        result = null;
                    }
                }
                else
                {
                    log.info("executeHttpRequest result str = null");
                    result = new JSONObject();
                }
                if (result != null && disconnectedCallback == null)
                {
                    responseBody.close();

                    response.close();
                }

        } catch (IOException e) {
        e.printStackTrace();
    }


        
        if (disconnectedCallback != null)
        {
            disconnectedCallback.run();
        }
        
        return result;
    }

    /**
     * Device can't decode slash in JSON like {"url":"http\/\/:iot.vijayanix.cn"}
     * 
     * @param json
     * @return
     */
    public static String decoceJSON(JSONObject json) {
        String jsonStr = json.toString();
        jsonStr = jsonStr.replace("\\/", "/");

        return jsonStr;
    }

    private static String unescapeHtml(String str) {
        return str.replace("&quot;", "\\\"");
    }

    private static final String HEADER_END = "\r\n\r\n";
    private static final String HEADER_SEPARATOR = "\r\n";
    private static final String HEADER_CONTENT_SEPARATOR = ": ";


}
