package com.vijayanix.iot.command.device.New;


import com.vijayanix.iot.command.ICommandLocal;
import com.vijayanix.iot.command.device.ICommandActivated;
import com.vijayanix.iot.command.device.ICommandNew;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;

public interface ICommandDeviceNewConfigureLocal extends ICommandNew, ICommandActivated, ICommandLocal
{
    /**
     * configure the new device to an AP accessible to Internet
     * 
     * @param deviceSsid the device's ssid(softap)
     * @param deviceWifiCipherType the device's wifi cipher type
     * @param devicePassword the device's password
     * @param apSsid Ap's ssid
     * @param apWifiCipherType Ap's wifi cipher type
     * @param apPassword Ap's password
     * @param randomToken 40 randomToken
     * @return whether the command executed suc
     * @throws InterruptedException when the command is interrupted
     */
    boolean doCommandDeviceNewConfigureLocal(String deviceSsid, WifiCipherType deviceWifiCipherType,
                                             String devicePassword, String apSsid, WifiCipherType apWifiCipherType, String apPassword, String randomToken);
    
    /**
     * configure the new mesh device to an AP accessible to Internet
     * 
     * @param deviceBssid the device's bssid
     * @param deviceSsid the device's ssid(softap)
     * @param deviceWifiCipherType the device's wifi cipher type
     * @param devicePassword the device's password
     * @param randomToken 40 randomToken
     * @return whether the command executed suc
     * @throws InterruptedException when the command is interrupted
     */
    boolean doCommandMeshDeviceNewConfigureLocal(String deviceBssid, String deviceSsid,
                                                 WifiCipherType deviceWifiCipherType, String devicePassword, String randomToken);
}
