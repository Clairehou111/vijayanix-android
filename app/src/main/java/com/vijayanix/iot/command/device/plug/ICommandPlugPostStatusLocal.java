package com.vijayanix.iot.command.device.plug;

import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.command.device.ICommandPlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;

import java.net.InetAddress;
import java.util.List;

public interface ICommandPlugPostStatusLocal extends ICommandLocal, ICommandPlug
{
    /**
     * @deprecated Use {@link #doCommandPlugPostStatusLocal(InetAddress, IStatusPlug, String, String)} instead of it,
     * and the deviceBssid=null when you call the method
     * 
     * post the statusPlug to the Plug by Local
     * @param inetAddress the Plug's ip address
     * @param statusPlug the status of Plug
     * @return whether the command executed suc
     */
    boolean doCommandPlugPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug);
    
    /**
     * post the statusPlug to the Plug by Local
     * 
     * @param inetAddress the Plug's ip address
     * @param statusPlug the status of Plug
     * @param deviceBssid the Plug's bssid
     * @param isMeshDevice whether the Plug is mesh device
     * @return whether the command executed suc
     */
    boolean doCommandPlugPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug, String deviceBssid,
                                         boolean isMeshDevice);
    
    /**
     * post multicast command
     * 
     * @param inetAddress
     * @param statusPlug
     * @param bssids
     * @return
     */
    boolean doCommandMulticastPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug, List<String> bssids);
}
