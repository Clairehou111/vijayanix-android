package com.vijayanix.iot.action.device.common;


import com.vijayanix.iot.command.device.ICommandGetStatusLocal;
import com.vijayanix.iot.command.device.plug.CommandPlugGetStatusLocal;
import com.vijayanix.iot.command.device.plug.ICommandPlugGetStatusLocal;
import com.vijayanix.iot.command.device.plugs.CommandPlugsGetStatusLocal;
import com.vijayanix.iot.command.device.plugs.ICommandPlugsGetStatusLocal;
import com.vijayanix.iot.command.device.watchman.CommandWatchmanGetStatusLocal;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;
import com.vijayanix.iot.util.DeviceDatastreamUtil;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchman;

import java.net.InetAddress;

public class ActionDeviceGetStatusLocal implements IActionDeviceGetStatusLocal
{
    
    @Override
    public boolean doActionDeviceGetStatusLocal(IDevice device)
    {
        return doActionDeviceGetStatusLocal(SubDatastreamType.ALL,device);
    }

    @Override
    public boolean doActionDeviceGetStatusLocal(int datastreamType, IDevice device) {
        DeviceType deviceType = device.getDeviceType();
        switch (deviceType)
        {  case PLUG:
                return executeGetPlugStatusLocal(device);
            case PLUGS:
                return executeGetPlugsStatusLocal(device);
            case WATCHMAN:
                return executeGetWatchmanStatusLocal(device);

        }
        throw new IllegalArgumentException();
    }


    private boolean executeGetPlugStatusLocal(IDevice device)
    {
        InetAddress inetAddress = device.getInetAddress();
        String deviceBssid = device.getBssid();
        boolean isMeshDevice = device.getIsMeshDevice();
        boolean result = false;
        
        ICommandPlugGetStatusLocal plugCommand = new CommandPlugGetStatusLocal();
        IStatusPlug plugStatus =
            plugCommand.doCommandPlugGetStatusLocal(inetAddress, deviceBssid, isMeshDevice);
        if (plugStatus != null)
        {
            result = true;
            IStatusPlug status = ((IDevicePlug)device).getStatusPlug();
            status.setIsOn(plugStatus.isOn());
        }
        
        return result;
    }
    

    
    private boolean executeGetPlugsStatusLocal(IDevice device)
    {
        InetAddress inetAddress = device.getInetAddress();
        String deviceBssid = device.getBssid();
        boolean isMeshDevice = device.getIsMeshDevice();
        boolean result = false;
        
        ICommandPlugsGetStatusLocal plugsCommand = new CommandPlugsGetStatusLocal();
        IStatusPlugs plugsStatus =
            plugsCommand.doCommandPlugsGetStatusLocal(inetAddress, deviceBssid, isMeshDevice);
        if (plugsStatus != null)
        {
            result = true;
            IDevicePlugs plugs = (IDevicePlugs)device;
            plugs.setStatusPlugs(plugsStatus);
        }
        
        return result;
    }



	private boolean executeGetWatchmanStatusLocal(IDevice device)
	{
		InetAddress inetAddress = device.getInetAddress();
		String deviceBssid = device.getBssid();
		boolean isMeshDevice = device.getIsMeshDevice();
		boolean result = false;

		ICommandGetStatusLocal commandGetStatusLocal = new CommandWatchmanGetStatusLocal();
		IDeviceWatchmanStatus watchmanStatus = (IDeviceWatchmanStatus) commandGetStatusLocal.doCommandGetStatusLocal(inetAddress,deviceBssid,isMeshDevice);
		if (watchmanStatus != null){
			result = true;
			DeviceDatastreamUtil.copyDeviceStatus(SubDatastreamType.ALL,watchmanStatus,((IDeviceWatchman)device).getWatchmanStatus());
		}

		return result;
	}
}
