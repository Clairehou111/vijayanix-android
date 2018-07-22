package com.vijayanix.iot.model.device.watchman;

/**
 * Created by hxhoua on 2018/7/12.
 */

public class DeviceWatchmanStatus implements IDeviceWatchmanStatus {
	private int lightA;
	private int lightB;

	@Override
	public int getLightA() {
		return lightA;
	}

	@Override
	public void setLightA(int lightA) {
		this.lightA = lightA;
	}

	@Override
	public int getLightB() {
		return lightB;
	}

	@Override
	public void setLightB(int lightB) {
		this.lightB = lightB;
	}


}
