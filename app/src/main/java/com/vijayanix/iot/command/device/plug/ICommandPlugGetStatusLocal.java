package com.vijayanix.iot.command.device.plug;

import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.command.device.ICommandPlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;

import java.net.InetAddress;

public interface ICommandPlugGetStatusLocal extends ICommandLocal, ICommandPlug
{
    /**
     * @deprecated Use {@link #doCommandPlugGetStatusLocal(InetAddress, String, String)} instead of it,
     * and the deviceBssid=null when you call the method
     * 
     * get the statusPlug to the Plug by Local
     * @param inetAddress the Plug's ip address
     * @return the status of the Plug or null(if executed fail)
     */
    IStatusPlug doCommandPlugGetStatusLocal(InetAddress inetAddress);
    
    /**
     * get the statusPlug to the Plug by Local
     * 
     * @param inetAddress the Plug's ip address
     * @param deviceBssid the Plug's bssid
     * @param isMeshDevice whether the Plug is mesh device
     * @return the status of the Plug or null(if executed fail)
     */
    IStatusPlug doCommandPlugGetStatusLocal(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice);

}
