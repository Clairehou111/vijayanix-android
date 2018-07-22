package com.vijayanix.iot.command.device.plugs;

import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.command.device.ICommandPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;

import java.net.InetAddress;

public interface ICommandPlugsGetStatusLocal extends ICommandLocal, ICommandPlugs
{
    /**
     * get the statusPlugs to the Plugs by Local
     * 
     * @param inetAddress the Plugs's ip address
     * @param deviceBssid the Plugs's bssid
     * @param isMeshDevice whether the Plugs is mesh device
     * @return the status of the Plugs or null(if executed fail)
     */
    IStatusPlugs doCommandPlugsGetStatusLocal(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice);
}
