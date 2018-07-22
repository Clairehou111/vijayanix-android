package com.vijayanix.iot.action.device.common;


import com.vijayanix.iot.command.device.ICommandPostStatusInternet;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.command.device.plug.CommandPlugPostStatusInternet;
import com.vijayanix.iot.command.device.plug.ICommandPlugPostStatusInternet;
import com.vijayanix.iot.command.device.plugs.CommandPlugsPostStatusInternet;
import com.vijayanix.iot.command.device.plugs.ICommandPlugsPostStatusInternet;
import com.vijayanix.iot.command.device.watchman.CommandWatchmanPostStatusInternet;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchman;

public class ActionDevicePostStatusInternet implements IActionDevicePostStatusInternet
{
    
    @Override
    public boolean doActionDevicePostStatusInternet(IDevice device, IDeviceStatus status)
    {
	    return doActionDevicePostStatusInternet(SubDatastreamType.ALL,device,status);
    }

    @Override
    public boolean doActionDevicePostStatusInternet(int datastreamType, IDevice device, IDeviceStatus status) {
        DeviceType deviceType = device.getDeviceType();
        switch (deviceType)
        {
            case PLUG:
                return executePostPlugStatusInternet((IDevicePlug)device, (IStatusPlug)status);

            case PLUGS:
                return executePostPlugsStatusInternet((IDevicePlugs)device, (IStatusPlugs)status);
            case WATCHMAN:
                return executePostWatchmanStatusInternet(datastreamType,(IDeviceWatchman)device, (IDeviceWatchmanStatus)status);

        }
        throw new IllegalArgumentException();
    }


    private boolean executePostPlugStatusInternet(IDevicePlug plug, IStatusPlug status)
    {
        boolean result = false;
        
        ICommandPlugPostStatusInternet plugCommand = new CommandPlugPostStatusInternet();
        result = plugCommand.doCommandPlugPostStatusInternet(plug.getKey(), status);
        if (result)
        {
            plug.getStatusPlug().setIsOn(status.isOn());
        }
        
        return result;
    }
    

    
    private boolean executePostPlugsStatusInternet(IDevicePlugs plugs, IStatusPlugs status)
    {
        boolean result = false;
        
        ICommandPlugsPostStatusInternet plugsCommand = new CommandPlugsPostStatusInternet();
        result = plugsCommand.doCommandPlugsPostStatusInternet(plugs.getKey(), status);
        if (result)
        {
            for (IStatusPlugs.IAperture postAperture : status.getStatusApertureList())
            {
                plugs.updateApertureOnOff(postAperture);
            }
        }
        
        return result;
    }



	private boolean executePostWatchmanStatusInternet(int datastreamType, IDeviceWatchman watchman, IDeviceWatchmanStatus status)
	{
		boolean result = false;

		ICommandPostStatusInternet command = new CommandWatchmanPostStatusInternet();
		result = command.doCommandPostStatusInternet(datastreamType,watchman.getKey(),status);


		return result;
	}

    
}
