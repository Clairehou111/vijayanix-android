package com.vijayanix.iot.action.device.New;


import com.vijayanix.iot.action.IActionDB;
import com.vijayanix.iot.action.IActionLocal;
import com.vijayanix.iot.action.IActionNew;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;

public interface IActionDeviceNewConfigureLocal extends IActionNew, IActionLocal, IActionDB
{
    /**
     * configure the new device to an AP accessible to Internet (if configure suc, save the device into local db with
     * negative device id)
     * 
     * @param deviceBssid the device's bssid
     * @param deviceSsid the device's ssid(softap)
     * @param deviceWifiCipherType the device's wifi cipher type
     * @param devicePassword the device's password
     * @param apSsid Ap's ssid
     * @param apWifiCipherType Ap's wifi cipher type
     * @param apPassword Ap's password
     * @param randomToken the random token
     * @return whether the device id(if suc, deviceId<0, else return 0)
     * @throws InterruptedException when the action is interrupted
     */
    long doActionDeviceNewConfigureLocal(String deviceBssid, String deviceSsid, WifiCipherType deviceWifiCipherType,
                                         String devicePassword, String apSsid, WifiCipherType apWifiCipherType, String apPassword, String randomToken) throws InterruptedException;
}
