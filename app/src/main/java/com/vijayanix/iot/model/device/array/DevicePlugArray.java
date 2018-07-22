package com.vijayanix.iot.model.device.array;

import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.plug.DevicePlug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DevicePlugArray extends DevicePlug implements IDeviceArray
{
    private Set<IDevice> mDeviceSet;
    
    public DevicePlugArray()
    {
        mDeviceSet = new HashSet<IDevice>();
    }
    
    @Override
    public synchronized void addDevice(IDevice device)
    {
        mDeviceSet.add(device);
    }
    
    @Override
    public synchronized void removeDevice(IDevice device)
    {
        if (mDeviceSet.contains(device))
        {
            mDeviceSet.remove(device);
        }
    }
    
    @Override
    public synchronized List<IDevice> getDeviceList()
    {
        List<IDevice> devices = new ArrayList<IDevice>();
        devices.addAll(mDeviceSet);
        return devices;
    }
}
