package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.model.device.plugs.IDevicePlugs;
import com.vijayanix.iot.base.object.IObjectBuilder;

public interface IBDevicePlugs extends IObjectBuilder
{
    IDevicePlugs alloc();
}
