package com.vijayanix.iot.command.device.plugs;

import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.command.device.ICommandPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;

import java.net.InetAddress;

public interface ICommandPlugsPostStatusLocal extends ICommandLocal, ICommandPlugs
{
    /**
     * post the statusPlugs to the Plugs by Local
     * 
     * @param inetAddress the Plugs's ip address
     * @param statusPlugs the status of Plugs
     * @param deviceBssid the Plugs's bssid
     * @param isMeshDevice whether the Plugs is mesh device
     * @return whether the command executed suc
     */
    boolean doCommandPlugsPostStatusLocal(InetAddress inetAddress, IStatusPlugs status, String deviceBssid,
                                          boolean isMeshDevice);
}
