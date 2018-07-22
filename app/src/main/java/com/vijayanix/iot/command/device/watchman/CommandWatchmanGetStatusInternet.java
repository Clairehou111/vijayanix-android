package com.vijayanix.iot.command.device.watchman;


import com.vijayanix.iot.util.DeviceDatastreamUtil;
import com.vijayanix.iot.command.device.ICommandGetStatusInternet;
import com.vijayanix.iot.command.device.ICommandWatchman;
import com.vijayanix.iot.common.net.rest2.MqttRequestUtil;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;

import org.apache.log4j.Logger;

public class CommandWatchmanGetStatusInternet implements ICommandGetStatusInternet,ICommandWatchman
{
    
    private final static Logger log = Logger.getLogger(CommandWatchmanGetStatusInternet.class);
    
    private boolean getCurrentStatus(String deviceKey)
    {

        String topic = DeviceDatastreamUtil.status2Topic(SubDatastreamType.ALL,deviceKey);

        return MqttRequestUtil.Get(topic);

    }
    
    @Override
    public boolean doCommandGetStatusInternet(String deviceKey)
    {
        boolean result = getCurrentStatus(deviceKey);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusInternet(deviceKey=[" + deviceKey
            + "]): " + result);
        return result;
    }
    
}
