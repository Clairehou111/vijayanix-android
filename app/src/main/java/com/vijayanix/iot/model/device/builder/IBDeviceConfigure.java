package com.vijayanix.iot.model.device.builder;


import com.vijayanix.iot.model.device.configure.IDeviceConfigure;
import com.vijayanix.iot.base.object.IObjectBuilder;

public interface IBDeviceConfigure extends IObjectBuilder
{
    IDeviceConfigure alloc(String bssid, String randomToken);
}
