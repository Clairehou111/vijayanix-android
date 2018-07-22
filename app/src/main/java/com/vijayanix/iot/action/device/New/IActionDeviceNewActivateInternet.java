package com.vijayanix.iot.action.device.New;


import com.vijayanix.iot.action.IActionDB;
import com.vijayanix.iot.action.IActionInternet;
import com.vijayanix.iot.action.IActionNew;
import com.vijayanix.iot.model.device.IDevice;

public interface IActionDeviceNewActivateInternet extends IActionNew, IActionInternet, IActionDB
{
    /**
     * activate device in the Internet, if suc, delete the negative device id device in local db
     * 
     * @param userId the user's id
     * @param userKey the user's key
     * @param randomToken the random token
     * @param negativeDeviceId the negative device id(negative device id is used by activating device)
     * @return IDevice the device
     * @throws InterruptedException when the action is interrupted
     */
    IDevice doActionDeviceNewActivateInternet(long userId, String userKey, String randomToken, long negativeDeviceId) throws InterruptedException;
}
