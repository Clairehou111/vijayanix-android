package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.action.IActionLocal;
import com.vijayanix.iot.action.device.IActionActivated;
import com.vijayanix.iot.action.device.IActionUnactivated;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDevicePostStatusLocal extends IActionActivated, IActionUnactivated, IActionLocal
{
    int FIRST_CHILD_LEVEL = 2;
    
    /**
     * post the status to device via local
     * 
     * @param device the device
     * @param status the new status
     * @return whether the post action is suc
     */
    boolean doActionDevicePostStatusLocal(final IDevice device, final IDeviceStatus status);

    boolean doActionDevicePostStatusLocal(int datastreamType, final IDevice device, final IDeviceStatus status);

}
