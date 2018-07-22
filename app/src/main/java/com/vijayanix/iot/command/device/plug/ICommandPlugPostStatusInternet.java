package com.vijayanix.iot.command.device.plug;

import com.vijayanix.iot.command.ICommandInternet;
import com.vijayanix.iot.command.device.ICommandPlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;

import java.util.List;

public interface ICommandPlugPostStatusInternet extends ICommandInternet, ICommandPlug
{

    /**
     * post the statusPlug to the Plug by Internet
     * 
     * @param deviceKey the device's key
     * @param statusPlug the status of Plug
     * @return whether the command executed suc
     */
    boolean doCommandPlugPostStatusInternet(String deviceKey, IStatusPlug statusPlug);
    
    /**
     * post multicast internet
     * 
     * @param deviceKey
     * @param statusPlug
     * @param bssids
     * @return
     */
    boolean doCommandMulticastPostStatusInternet(String deviceKey, IStatusPlug statusPlug, List<String> bssids);
}
