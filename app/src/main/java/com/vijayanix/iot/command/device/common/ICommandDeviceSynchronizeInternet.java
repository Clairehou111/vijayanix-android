package com.vijayanix.iot.command.device.common;

import com.vijayanix.iot.command.ICommand;
import com.vijayanix.iot.command.ICommandInternet;
import com.vijayanix.iot.model.device.IDevice;

import java.util.List;

public interface ICommandDeviceSynchronizeInternet extends ICommandInternet, ICommand {

    /**
     * synchronize the user's device from the Server
     * 
     * @return the device list of the user
     */
    List<IDevice> doCommandDeviceSynchronizeInternet(String userKey);


	/**
	 * get devicetype by publish mqtt topic
	 * @param deviceKey
	 */
	void getDeviceType(String deviceKey);


}
