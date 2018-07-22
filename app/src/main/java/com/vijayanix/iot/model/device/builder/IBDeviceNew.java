package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.model.device.newd.IDeviceNew;
import com.vijayanix.iot.base.object.IObjectBuilder;

public interface IBDeviceNew extends IObjectBuilder
{
    IDeviceNew alloc(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi);
    
    IDeviceNew alloc(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi, int state);
}
