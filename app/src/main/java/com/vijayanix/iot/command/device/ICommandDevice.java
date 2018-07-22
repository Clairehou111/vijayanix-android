package com.vijayanix.iot.command.device;

import com.vijayanix.iot.command.ICommand;

/**
 * ICommandDevice indicate that the command is belong to devices
 * 

 * 
 */
public interface ICommandDevice extends ICommand
{
    String URL_MULTICAST =
        "https://iot.vijayanix.cn/v1/device/rpc/?deliver_to_device=true&action=multicast&bssids=";
    
    int MULTICAST_GROUP_LENGTH_LIMIT = 50;
    
    String KEY_GROUP = "group";
}
