package com.vijayanix.iot.model.device.builder;

import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.base.object.IObjectBuilder;

public interface IBDevicePlug extends IObjectBuilder
{
    IDevicePlug alloc();
}
