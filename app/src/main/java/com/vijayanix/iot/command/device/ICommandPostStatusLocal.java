package com.vijayanix.iot.command.device;

import com.vijayanix.iot.command.ICommandLocal;

import java.net.InetAddress;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface ICommandPostStatusLocal extends ICommandLocal {

	boolean doCommandPostStatusLocal(int datastreamType, InetAddress inetAddress, IDeviceStatus deviceStatus);

	boolean doCommandPostStatusLocal(int datastreamType, InetAddress inetAddress, IDeviceStatus deviceStatus, String deviceBssid,
	                                 boolean isMeshDevice);


//	 void setDeviceStatus(int datastreamType,  IDeviceStatus sourceStatus, IDeviceStatus targetStatus);

}
