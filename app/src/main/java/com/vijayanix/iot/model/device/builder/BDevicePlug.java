package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.plug.DevicePlug;

public class BDevicePlug implements IBDevicePlug
{
    /*
     * Singleton lazy initialization start
     */
    private BDevicePlug()
    {
    }
    
    private static class InstanceHolder
    {
        static BDevicePlug instance = new BDevicePlug();
    }
    
    public static BDevicePlug getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    @Override
    public IDevicePlug alloc()
    {
        return new DevicePlug();
    }
}
