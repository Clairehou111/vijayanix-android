package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.model.device.plugs.DevicePlugs;

public class BDevicePlugs implements IBDevicePlugs
{
    private BDevicePlugs()
    {
    }
    
    private static class InstanceHolder
    {
        static BDevicePlugs instance = new BDevicePlugs();
    }
    
    public static BDevicePlugs getInstance()
    {
        return InstanceHolder.instance;
    }
    
    @Override
    public IDevicePlugs alloc()
    {
        return new DevicePlugs();
    }
    
}
