package com.vijayanix.iot.action.device.configure;


import com.vijayanix.iot.action.IActionConfigure;
import com.vijayanix.iot.action.IActionInternet;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDeviceConfigureActivateInternet extends IActionConfigure, IActionInternet
{
    /**
     * activate device in the Internet
     * 
     * @param userId the user's id
     * @param userKey the user's key
     * @param randomToken the random token
     * @return IDevice the device
     */
    IDevice doActionDeviceConfigureActivateInternet(long userId, String userKey, String randomToken);
    
}
