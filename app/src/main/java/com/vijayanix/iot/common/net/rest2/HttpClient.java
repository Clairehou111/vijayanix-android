package com.vijayanix.iot.common.net.rest2;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HttpClient
{
    
    private final static int MAX_RETRY_TIMES = 0;
    
    private static final int HTTP_DEFAULT_PORT = 80;
    
    private static final int HTTPS_DEFAULT_PORT = 443;
    
    private final static int CONNECTION_TIMEOUT = 2;
    
    private final static int SO_TIMEOUT = 20;

	 private static OkHttpClient  mClient;

    private static class EspHttpClientHolder
    {
        static HttpClient instance = new HttpClient();
    }

	public  static HttpClient getEspHttpClient()
    {
        return EspHttpClientHolder.instance;
    }

    public static OkHttpClient getOkHttpClient()
    {
        return mClient;
    }


    
    private HttpClient()
    {
	    OkHttpClient.Builder builder =  new OkHttpClient().newBuilder();

	    builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
	    builder.readTimeout(SO_TIMEOUT, TimeUnit.SECONDS);
	    builder.writeTimeout(SO_TIMEOUT, TimeUnit.SECONDS);
	    builder.addInterceptor(new OkHttpRetryIntercepter(MAX_RETRY_TIMES));
		//builder.connectionPool(new ConnectionPool(1,))
	    mClient = builder.build();
        init();

    }



	private void init()
    {
  /*      BasicHttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        params.setParameter("timemout", 6);
        this.setParams(params);
        // set keep alive strategy
        this.setKeepAliveStrategy(new ConnectionKeepAliveStrategy()
        {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context)
            { // Honor 'keep-alive' header
                HeaderElementIterator it =
                    new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext())
                {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout"))
                    {
                        try
                        {
                            return Long.parseLong(value) * 1000;
                        }
                        catch (NumberFormatException ignore)
                        {
                        }
                    }
                }
                Integer timeout = (Integer)context.getAttribute("timeout");
                if (timeout != null)
                {
                    return timeout.longValue();
                }
                HttpHost target = (HttpHost)context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if ("iot.vijayanix.cn".equalsIgnoreCase(target.getHostName()))
                {
                    // Keep alive for 30 seconds when connecting to server
                    return 30 * 1000;
                }
                else
                {
                    // otherwise keep alive for 8 seconds when connection to device
                    return 10 * 1000;
                }
            }
        });*/
    }


    
}
