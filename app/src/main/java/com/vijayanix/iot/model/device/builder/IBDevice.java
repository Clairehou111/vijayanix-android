package com.vijayanix.iot.model.device.builder;


import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.db.interfa.IDeviceDB;

public interface IBDevice
{
    IDevice alloc(String deviceName, long deviceId, String deviceKey, boolean isOwner, String bssid, int state,
                  int ptype, String rom_version, String latest_rom_version, long userId, long... timestamp);
    
    IDevice alloc(IDeviceDB deviceDB);
}
