package com.vijayanix.iot.action.device.common;


import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.command.device.ICommandPostStatusLocal;
import com.vijayanix.iot.command.device.plug.CommandPlugPostStatusLocal;
import com.vijayanix.iot.command.device.plug.ICommandPlugPostStatusLocal;
import com.vijayanix.iot.command.device.plugs.CommandPlugsPostStatusLocal;
import com.vijayanix.iot.command.device.plugs.ICommandPlugsPostStatusLocal;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;
import com.vijayanix.iot.util.DeviceDatastreamUtil;
import com.vijayanix.iot.command.device.watchman.CommandWatchmanPostStatusLocal;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.model.device.watchman.DeviceWatchman;

import java.net.InetAddress;

public class ActionDevicePostStatusLocal implements IActionDevicePostStatusLocal
{
    private boolean __doActionDevicePostStatusLocal(IDevice device, IDeviceStatus status)
    {
        return doActionDevicePostStatusLocal(SubDatastreamType.ALL,device,status);
    }
    
    @Override
    public boolean doActionDevicePostStatusLocal(IDevice device, IDeviceStatus status)
    {
        return __doActionDevicePostStatusLocal(device, status);
    }

    @Override
    public boolean doActionDevicePostStatusLocal(int datastreamType, IDevice device, IDeviceStatus status) {
        DeviceType deviceType = device.getDeviceType();
        switch (deviceType)
        {   case PLUG:
                return executePostPlugStatusLocal(device, (IStatusPlug)status);

            case PLUGS:
                return executePostPlugsStatusLocal(device, (IStatusPlugs)status);

            case WATCHMAN:
                return executePostWatchmanStatusLocal(datastreamType,device, status);

        }
        throw new IllegalArgumentException();
    }


    private boolean executePostPlugStatusLocal(IDevice device, IStatusPlug status)
    {
        InetAddress inetAddress = device.getInetAddress();
        String deviceBssid = device.getBssid();
        boolean isMeshDevice = device.getIsMeshDevice();
        boolean result = false;
        
        ICommandPlugPostStatusLocal plugCommand = new CommandPlugPostStatusLocal();
        result = plugCommand.doCommandPlugPostStatusLocal(inetAddress, status, deviceBssid, isMeshDevice);
        if (result)
        {
            IStatusPlug plugStatus = ((IDevicePlug)device).getStatusPlug();
            plugStatus.setIsOn(status.isOn());
        }
        
        return result;
    }
    

    
    private boolean executePostPlugsStatusLocal(IDevice device, IStatusPlugs status)
    {
        InetAddress inetAddress = device.getInetAddress();
        String deviceBssid = device.getBssid();
        boolean isMeshDevice = device.getIsMeshDevice();
        boolean result = false;
        
        ICommandPlugsPostStatusLocal plugsCommand = new CommandPlugsPostStatusLocal();
        result = plugsCommand.doCommandPlugsPostStatusLocal(inetAddress, status, deviceBssid, isMeshDevice);
        if (result) {
            IDevicePlugs devicePlugs = (IDevicePlugs)device;
            for (IStatusPlugs.IAperture postAperture : status.getStatusApertureList()) {
                devicePlugs.updateApertureOnOff(postAperture);
            }
        }
        
        return result;
    }



	private boolean executePostWatchmanStatusLocal(int datastreamType, IDevice device, IDeviceStatus status)
	{
		InetAddress inetAddress = device.getInetAddress();
		String deviceBssid = device.getBssid();
		boolean isMeshDevice = device.getIsMeshDevice();
		boolean result = false;

		ICommandPostStatusLocal watchmanCommand = new CommandWatchmanPostStatusLocal();
		result = watchmanCommand.doCommandPostStatusLocal(datastreamType,inetAddress, status, deviceBssid, isMeshDevice);

        if (result) {
            //修改device
	        DeviceDatastreamUtil.copyDeviceStatus(datastreamType,status,((DeviceWatchman)device).getWatchmanStatus());
        }

		return result;
	}


}
