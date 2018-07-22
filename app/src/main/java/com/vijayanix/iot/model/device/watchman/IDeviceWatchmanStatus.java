package com.vijayanix.iot.model.device.watchman;

import com.vijayanix.iot.command.device.IDeviceStatus;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface IDeviceWatchmanStatus extends IDeviceStatus {
	int getLightA();

	void setLightA(int lightA);

	int getLightB();

	void setLightB(int lightB);

}
