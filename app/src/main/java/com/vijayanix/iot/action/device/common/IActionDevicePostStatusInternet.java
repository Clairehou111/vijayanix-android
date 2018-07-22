package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.action.IActionInternet;
import com.vijayanix.iot.action.device.IActionActivated;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDevicePostStatusInternet extends IActionActivated, IActionInternet
{
    int FIRST_CHILD_LEVEL = 1;
    /**
     * post the status to device via Internet
     * 
     * @param device the device
     * @param status the new status
     * @return whether the post action is suc
     */
    boolean doActionDevicePostStatusInternet(final IDevice device, final IDeviceStatus status);
    boolean doActionDevicePostStatusInternet(int datastreamType, final IDevice device, final IDeviceStatus status);

}
