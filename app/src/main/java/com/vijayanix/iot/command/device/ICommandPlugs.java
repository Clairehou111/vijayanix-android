package com.vijayanix.iot.command.device;

/**
 * ICommandPlugs indicate that the command is belong to plugs
 * 
*
 */
public interface ICommandPlugs extends ICommandDevice
{
    String URL = "https://iot.vijayanix.cn/v1/datastreams/plugs/datapoint/?deliver_to_device=true";
    
    String KEY_PLUGS_STATUS = "plugs_status";
    
    String KEY_PLUGS_VALUE = "plugs_value";
    
    String KEY_APERTURE_COUNT = "plugs_num";
}
