package com.vijayanix.iot.action.device.New;


import com.vijayanix.iot.action.IActionLocal;
import com.vijayanix.iot.action.IActionNew;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.device.newd.IDeviceNew;

public interface IActionDeviceNewGetInfoLocal extends IActionNew, IActionLocal
{
    /**
     * Get the sta-device information
     * @param device
     * @return the IOTAddress of the device
     */
    IOTAddress doActionNewGetInfoLocal(IDeviceNew device);
}
