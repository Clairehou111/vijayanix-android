package com.vijayanix.iot.common.net.rest2;

public interface IHttpRequest
{
    /**
     * put the header param into map
     * 
     * @param key the key of the param
     * @param value the value of the param
     */
    void putHeaderParams(String key, String value);
    
    /**
     * Get the relative Url
     * 
     * @return the relative Url
     */
    String getRelativeUrl();
    
    /**
     * Get the scheme of the uri
     * 
     * @return the scheme of the uri
     */
    String getScheme();
    
    /**
     * Get the host of the uri
     * 
     * @return the host of the uri
     */
    String getHost();
    
    /**
     * Get the content of the request(usually it is a json String)
     * 
     * @return the content of the request
     */
    String getContent();
    
}
