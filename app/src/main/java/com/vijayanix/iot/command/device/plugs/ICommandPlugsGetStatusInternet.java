package com.vijayanix.iot.command.device.plugs;

import com.vijayanix.iot.command.ICommandInternet;
import com.vijayanix.iot.command.device.ICommandPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;

public interface ICommandPlugsGetStatusInternet extends ICommandInternet, ICommandPlugs
{
    /**
     * get the statusPlugs to the Plugs by Internet
     * 
     * @param deviceKey the device key
     * @return the status of the Plugs or null(if executed fail)
     */
    IStatusPlugs doCommandPlugsGetStatusInternet(String deviceKey);
}
