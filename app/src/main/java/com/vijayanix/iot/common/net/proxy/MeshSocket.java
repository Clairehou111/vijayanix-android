package com.vijayanix.iot.common.net.proxy;

import java.net.InetAddress;
import java.util.List;

public interface MeshSocket
{
    
    InetAddress getInetAddress();
    
    /**
     * get the refresh proxy task list which hasn't been sent
     * 
     * @return the refresh proxy task list which hasn't been sent
     */
    List<ProxyTask> getRefreshProxyTaskList();
    
    /**
     * offer the new proxy task
     * 
     * @param proxyTask the new proxy task
     */
    void offer(ProxyTask proxyTask);
    
    /**
     * close the MeshSocket half, don't accept more new request
     */
    void halfClose();
    
    /**
     * close the MeshSocket
     */
    void close();
    
    /**
     * check whether the VijSocket is expired
     * 
     * @return whether the VijSocket is expired
     */
    boolean isExpired();
    
    /**
     * check the proxy tasks' states and proceed them 
     */
    void checkProxyTaskStateAndProc();
    
    /**
     * get whether the MeshSocket is connected to remote device
     * 
     * @return whether the MeshSocket is connected to remote device
     */
    boolean isConnected();
    
    /**
     * get whether the MeshSocket is closed
     * 
     * @return whether the MeshSocket is closed
     */
    boolean isClosed();
    
}
