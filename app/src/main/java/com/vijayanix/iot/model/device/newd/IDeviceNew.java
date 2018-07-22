package com.vijayanix.iot.model.device.newd;


import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.action.device.New.IActionDeviceNewActivateInternet;
import com.vijayanix.iot.action.device.New.IActionDeviceNewConfigureLocal;
import com.vijayanix.iot.model.device.IDevice;

import java.util.concurrent.Future;

public interface IDeviceNew extends IDevice, IActionDeviceNewActivateInternet,
        IActionDeviceNewConfigureLocal
{
    boolean cancel(boolean mayInterruptIfRunning);
    
    void resume();
    
    void setFuture(Future<?> future);
    
    /**
     * Set the wifi signal level in dBm
     * 
     * @param rssi
     */
    void setRssi(int rssi);
    
    /**
     * Get the wifi signal level in dBm
     * 
     * @return the wifi signal level in dBm
     */
    int getRssi();
    
    /**
     * Get the soft-ap secret type
     * 
     * @return the soft-ap secret type @see WifiCipherType
     */
    WifiCipherType getWifiCipherType();
    
    /**
     * get the default softap password (bssid + "_v*%W>L<@i&Nxe!" e.g.:"1a:fe:34:77:c0:00"+"_v*%W>L<@i&Nxe!")
     * 
     * @return the default softap password of the device
     */
    String getDefaultPassword();
    
    /**
     * Set the soft-ap ssid
     * 
     * @param ssid
     */
    void setSsid(String ssid);
    
    /**
     * Get the soft-ap ssid
     * 
     * @return the soft-ap ssid
     */
    String getSsid();
    
    /**
     * Set the configure target AP ssid
     * 
     * @param apSsid
     */
    void setApSsid(String apSsid);
    
    /**
     * Get the configure target AP ssid
     * 
     * @return the configure target AP ssid
     */
    String getApSsid();
    
    /**
     * Set the configure target AP secret type
     * 
     * @param apWifiCipherType @see WifiCipherType
     */
    void setApWifiCipherType(WifiCipherType apWifiCipherType);
    
    /**
     * Get the configure target AP secret type
     * 
     * @return @see WifiCipherType
     */
    WifiCipherType getApWifiCipherType();
    
    /**
     * Set the configure target AP password
     * 
     * @param apPassword
     */
    void setApPassword(String apPassword);
    
    /**
     * Get the configure target AP password
     * 
     * @return the configure target AP password
     */
    String getApPassword();
}
