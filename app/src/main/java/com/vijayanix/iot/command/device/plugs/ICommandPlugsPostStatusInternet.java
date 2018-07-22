package com.vijayanix.iot.command.device.plugs;

import com.vijayanix.iot.command.ICommandInternet;
import com.vijayanix.iot.command.device.ICommandPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;

public interface ICommandPlugsPostStatusInternet extends ICommandInternet, ICommandPlugs
{
    /**
     * post the statusPlugs to the Plugs by Internet
     * 
     * @param deviceKey the device's key
     * @param status the status of Plugs
     * @return whether the command executed suc
     */
    boolean doCommandPlugsPostStatusInternet(String deviceKey, IStatusPlugs status);
}
