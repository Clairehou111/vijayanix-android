package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.model.device.newd.DeviceNew;
import com.vijayanix.iot.model.device.newd.IDeviceNew;

public class BDeviceNew implements IBDeviceNew
{
    /*
     * Singleton lazy initialization start
     */
    private BDeviceNew()
    {
    }
    
    private static class InstanceHolder
    {
        static BDeviceNew instance = new BDeviceNew();
    }
    
    public static BDeviceNew getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    @Override
    public IDeviceNew alloc(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi)
    {
        return new DeviceNew(ssid, bssid, wifiCipherType, rssi);
    }
    
    @Override
    public IDeviceNew alloc(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi, int state)
    {
        return new DeviceNew(ssid, bssid, wifiCipherType, rssi, state);
    }
    
}
