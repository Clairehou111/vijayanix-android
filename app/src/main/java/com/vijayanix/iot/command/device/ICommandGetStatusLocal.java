package com.vijayanix.iot.command.device;

import com.vijayanix.iot.command.ICommandLocal;

import java.net.InetAddress;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface ICommandGetStatusLocal extends ICommandLocal {

	IDeviceStatus doCommandGetStatusLocal(InetAddress inetAddress);

	IDeviceStatus doCommandGetStatusLocal(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice);
}
