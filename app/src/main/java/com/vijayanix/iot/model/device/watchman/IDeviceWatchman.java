package com.vijayanix.iot.model.device.watchman;

import com.vijayanix.iot.model.device.IDevice;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface IDeviceWatchman extends IDevice {
	IDeviceWatchmanStatus getWatchmanStatus();

	void setWatchmanStatus(IDeviceWatchmanStatus watchmanStatus);
}
