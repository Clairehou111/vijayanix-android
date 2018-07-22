package com.vijayanix.iot.command.device;

import com.vijayanix.iot.command.ICommandInternet;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface ICommandPostStatusInternet extends ICommandInternet {

	boolean doCommandPostStatusInternet(int datastreamType, String deviceKey, IDeviceStatus deviceStatus);

}
