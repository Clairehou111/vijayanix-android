package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.action.IActionLocal;
import com.vijayanix.iot.action.device.IActionActivated;
import com.vijayanix.iot.action.device.IActionUnactivated;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDeviceGetStatusLocal extends IActionActivated, IActionUnactivated, IActionLocal
{
    /**
     * get the current status of device via local
     * 
     * @param device the device
     * @return whether the get action is suc
     */
    boolean doActionDeviceGetStatusLocal(final IDevice device);
    boolean doActionDeviceGetStatusLocal(int datastreamType, final IDevice device);
}
