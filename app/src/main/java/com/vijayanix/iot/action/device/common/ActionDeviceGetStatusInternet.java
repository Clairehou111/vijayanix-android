package com.vijayanix.iot.action.device.common;


import com.vijayanix.iot.command.device.ICommandGetStatusInternet;
import com.vijayanix.iot.command.device.plug.CommandPlugGetStatusInternet;
import com.vijayanix.iot.command.device.plug.ICommandPlugGetStatusInternet;
import com.vijayanix.iot.command.device.plugs.CommandPlugsGetStatusInternet;
import com.vijayanix.iot.command.device.plugs.ICommandPlugsGetStatusInternet;
import com.vijayanix.iot.command.device.watchman.CommandWatchmanGetStatusInternet;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;

public class ActionDeviceGetStatusInternet implements IActionDeviceGetStatusInternet
{
    
    @Override
    public boolean doActionDeviceGetStatusInternet(IDevice device)
    {
       return doActionDeviceGetStatusInternet(SubDatastreamType.ALL,device);
    }

    @Override
    public boolean doActionDeviceGetStatusInternet(int datastreamType, IDevice device) {
        DeviceType deviceType = device.getDeviceType();
        switch (deviceType)
        {
            case PLUG:
                return executeGetPlugStatusInternet(device);
            case PLUGS:
                return executeGetPlugsStatusInternet(device);
            case WATCHMAN:
	            return executeGetWatchmanStatusInternet(device);

        }
        throw new IllegalArgumentException();
    }


    private boolean executeGetPlugStatusInternet(IDevice device)
    {
        ICommandPlugGetStatusInternet plugCommand = new CommandPlugGetStatusInternet();
        return plugCommand.doCommandPlugGetStatusInternet(device.getKey());

    }
    

    
    private boolean executeGetPlugsStatusInternet(IDevice device)
    {
        boolean result = false;
        
        ICommandPlugsGetStatusInternet plugsCommand = new CommandPlugsGetStatusInternet();
        IStatusPlugs plugsStatus = plugsCommand.doCommandPlugsGetStatusInternet(device.getKey());
        if (plugsStatus != null)
        {
            result = true;
            IDevicePlugs plugs = (IDevicePlugs)device;
            plugs.setStatusPlugs(plugsStatus);
        }
        
        return result;
    }


	private boolean executeGetWatchmanStatusInternet(IDevice device)
	{
		ICommandGetStatusInternet command = new CommandWatchmanGetStatusInternet();
		return command.doCommandGetStatusInternet(device.getKey());

	}
}
