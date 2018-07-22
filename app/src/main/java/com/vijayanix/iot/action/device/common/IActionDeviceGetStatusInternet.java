package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.action.IActionInternet;
import com.vijayanix.iot.action.device.IActionActivated;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDeviceGetStatusInternet extends IActionActivated, IActionInternet
{
    /**
     * get the current status of device via internet)
     * 
     * @param device the device
     * @return whether the get action is suc
     */
    boolean doActionDeviceGetStatusInternet(final IDevice device);
    boolean doActionDeviceGetStatusInternet(int datastreamType, final IDevice device);
}
