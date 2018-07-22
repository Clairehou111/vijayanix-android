package com.vijayanix.iot.command.device.plug;

import com.vijayanix.iot.command.ICommandInternet;
import com.vijayanix.iot.command.device.ICommandPlug;

public interface ICommandPlugGetStatusInternet extends ICommandInternet, ICommandPlug
{

    /**
     * get the statusPlug to the Plug by Internet
     * 
     * @param deviceKey the device key
     * @return the status of the Plug or null(if executed fail)
     */
    boolean doCommandPlugGetStatusInternet(String deviceKey);
}
