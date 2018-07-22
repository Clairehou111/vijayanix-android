package com.vijayanix.iot.model.device.array;

import com.vijayanix.iot.model.device.IDevice;

import java.util.List;

public interface IDeviceArray extends IDevice
{
    void addDevice(IDevice device);
    
    void removeDevice(IDevice device);
    
    List<IDevice> getDeviceList();
}
