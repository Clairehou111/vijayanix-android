package com.vijayanix.iot.command.device.common;



import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.List;

public class CommandDeviceDiscoverLocal implements ICommandDeviceDiscoverLocal
{
    private final static Logger log = Logger.getLogger(CommandDeviceDiscoverLocal.class);
    
    @Override
    public List<IOTAddress> doCommandDeviceDiscoverLocal()
    {
        List<IOTAddress> result = BaseApiUtil.discoverDevices();
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceDiscoverLocal(): " + result);
        return result;
    }
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        throw new RuntimeException("EspCommandDeviceSleepRebootLocal don't support getLocalUrl");
    }
    
}
