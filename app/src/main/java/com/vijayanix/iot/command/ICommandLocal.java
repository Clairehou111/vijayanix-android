package com.vijayanix.iot.command;

import java.net.InetAddress;

/**
 * ICommandLocal indicate that the action is related to local
 * 

 * 
 */
public interface ICommandLocal extends ICommand
{
    /**
     * get the local url by device's inetAddress
     * @param inetAddress device's inetAddress
     * @return local url
     */
    String getLocalUrl(InetAddress inetAddress);
}
