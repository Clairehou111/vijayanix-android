package com.vijayanix.iot.action.device.array;

import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.model.device.array.IDeviceArray;

public interface IActionDeviceArrayPostStatus
{
    /**
     * Post IDeviceArray status command
     * 
     * @param deviceArray
     * @param status
     */
    void doActionDeviceArrayPostStatus(IDeviceArray deviceArray, IDeviceStatus status);
}
