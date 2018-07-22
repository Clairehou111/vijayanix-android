package com.vijayanix.iot.command.device.plug;



import com.vijayanix.iot.common.net.rest2.MqttRequestUtil;

import org.apache.log4j.Logger;

import static com.vijayanix.iot.mqtt.MqttConstant.TOPIC_APP;

public class CommandPlugGetStatusInternet implements ICommandPlugGetStatusInternet
{
    
    private final static Logger log = Logger.getLogger(CommandPlugGetStatusInternet.class);
    
    private boolean getCurrentPlugStatus(String deviceKey)
    {

        String topic = TOPIC_APP + deviceKey;

        return MqttRequestUtil.Get(topic);

    }
    
    @Override
    public boolean doCommandPlugGetStatusInternet(String deviceKey)
    {
        boolean result = getCurrentPlugStatus(deviceKey);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusInternet(deviceKey=[" + deviceKey
            + "]): " + result);
        return result;
    }
    
}
