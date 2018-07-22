package com.vijayanix.iot.command.device.common;

import com.vijayanix.iot.command.ICommand;
import com.vijayanix.iot.command.ICommandInternet;

public interface ICommandDeviceDeleteInternet extends ICommandInternet, ICommand {
    String URL = "https://iot.vijayanix.cn/v1/key/?method=DELETE";
    /**
     * delete the device on Server
     * @param deviceKey the device's key
     * @return whether the command executed suc
     */
    boolean doCommandDeviceDeleteInternet(String deviceKey);
}
