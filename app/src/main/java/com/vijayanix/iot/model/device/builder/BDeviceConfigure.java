package com.vijayanix.iot.model.device.builder;


import com.vijayanix.iot.model.device.configure.DeviceConfigure;
import com.vijayanix.iot.model.device.configure.IDeviceConfigure;

public class BDeviceConfigure implements IBDeviceConfigure
{
    /*
     * Singleton lazy initialization start
     */
    private BDeviceConfigure()
    {
    }
    
    private static class InstanceHolder
    {
        static BDeviceConfigure instance = new BDeviceConfigure();
    }
    
    public static BDeviceConfigure getInstance()
    {
        return InstanceHolder.instance;
    }
    
    @Override
    public IDeviceConfigure alloc(String bssid, String randomToken)
    {
        IDeviceConfigure device = new DeviceConfigure(bssid, randomToken);
        return device;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
}
