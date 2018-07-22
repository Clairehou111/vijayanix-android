package com.vijayanix.iot.action.device.common;



import com.vijayanix.iot.action.IActionDevice;
import com.vijayanix.iot.action.IActionLocal;

import java.net.InetAddress;

public interface IActionDeviceConfigureLocal extends IActionLocal, IActionDevice
{
    /**
     * configure the device to an AP accessible to Internet, and make the device activate on Server
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do the configure
     *            action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param inetAddress the device's ip address
     * @param apSsid the Ap's ssid
     * @param apPassword the Ap's password
     * @param randomToken 40 randomToken
     * @param deviceBssid the device's bssid
     * @return whether the action executed suc
     */
    boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String apSsid,
                                         String apPassword, String randomToken, String deviceBssid);
    
    /**
     * configure the device to an AP
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do the configure
     *            action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param inetAddress the device's ip address
     * @param apSsid the Ap's ssid
     * @param apPassword the Ap's password
     * @param deviceBssid the device's bssid
     * @return whether the action executed suc
     */
    boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String apSsid,
                                         String apPassword, String deviceBssid);
    
    /**
     * make the device activate on Server
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do the configure
     *            action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param inetAddress the device's ip address
     * @param randomToken 40 randomToken
     * @param deviceBssid the device's bssid
     * @return whether the action executed suc
     */
    boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String randomToken,
                                         String deviceBssid);
    
    /**
     * configure the mesh device to an AP accessible to Internet, and make the mesh device activate on Server
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do
     *            the configure action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param deviceBssid the mesh device's bssid
     * @param inetAddress the mesh device's ip address
     * @param apSsid the Ap's ssid
     * @param apPassword the Ap's password
     * @param randomToken 40 randomToken
     * @return whether the action executed suc
     */
    boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid, InetAddress inetAddress,
                                             String apSsid, String apPassword, String randomToken);
    
    /**
     * configure the mesh device to an AP
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do
     *            the configure action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param deviceBssid the mesh device's bssid
     * @param inetAddress the device's ip address
     * @param apSsid the Ap's ssid
     * @param apPassword the Ap's password
     * @return whether the action executed suc
     */
    boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid, InetAddress inetAddress,
                                             String apSsid, String apPassword);
    
    /**
     * make the mesh device activate on Server
     * 
     * @param discoverRequired whether it is necessary to discover the device's new inetAddress before do
     *            the configure action, if we can't find the new inetAddress, we will use the old inetAddress
     * @param deviceBssid the mesh device's bssid
     * @param inetAddress the mesh device's ip address
     * @param randomToken 40 randomToken
     * @return whether the action executed suc
     */
    boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid, InetAddress inetAddress,
                                             String randomToken);
}
