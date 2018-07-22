package com.vijayanix.iot.command.device.common;

import com.vijayanix.iot.command.ICommand;
import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.model.device.IOTAddress;

import java.util.List;

public interface ICommandDeviceDiscoverLocal extends ICommandLocal, ICommand {
    /**
     * discover the @see IOTAddress in the same AP
     * @return the list of IOTAddress
     */
    List<IOTAddress> doCommandDeviceDiscoverLocal();
}
