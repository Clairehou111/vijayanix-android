package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.action.IActionInternet;
import com.vijayanix.iot.action.IActionLocal;
import com.vijayanix.iot.action.device.IActionActivated;
import com.vijayanix.iot.command.ICommandUser;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.mqtt.ReceivedMessage;

import java.util.List;

public interface IActionDeviceSynchronizeInterentDiscoverLocal extends ICommandUser, IActionActivated,
		IActionLocal, IActionInternet
{
    /**
     * The min times that send UDP Broadcast when onRefreshing
     */
    int UDP_EXECUTE_MIN_TIMES = 1;
    
    /**
     * The max times that send UDP Broadcast when onRefreshing
     */
    int UDP_EXECUTE_MAX_TIMES = 1;


	int UDP_EXECUTE_INTERVAL = 1000 ;
    
    /**
     * Synchronize devices from Server and Discovery devices on local
     * 
     * @param userKey the user key
     */
    void doActionDeviceSynchronizeInterentDiscoverLocal(final String userKey);
    
    /**
     * Synchronize devices from Discovery devices on local only
     * @param isSyn whether execute it syn or asyn
     */
    void doActionDeviceSynchronizeDiscoverLocal(boolean isSyn);
    
    /**
     * Synchronize devices from Server only
     * @param userKey the user key
     */
    void doActionDeviceSynchronizeInternet(final String userKey);



	IDevice getFoundedDeviceByssid(String ssid);

	List<IDevice> getFoundDevices();


	boolean putIntoDevice(IDevice espDevice);


	void processMqttMessage(ReceivedMessage receivedMessage);
}
