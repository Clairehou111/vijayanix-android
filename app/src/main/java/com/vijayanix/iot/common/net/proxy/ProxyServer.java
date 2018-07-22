package com.vijayanix.iot.common.net.proxy;

public interface ProxyServer
{
    /**
     * start the ProxyServer
     */
    void start();
    
    /**
     * stop the ProxyServer
     */
    void stop();
    
    /**
     * get the ProxyServer port
     * 
     * @return the ProxyServer port
     */
    int getEspProxyServerPort();
    
}
