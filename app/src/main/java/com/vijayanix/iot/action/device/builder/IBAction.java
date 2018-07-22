package com.vijayanix.iot.action.device.builder;

import com.vijayanix.iot.action.IAction;
import com.vijayanix.iot.base.object.IObjectBuilder;

public interface IBAction extends IObjectBuilder
{
    /**
     * alloc the EspAction by its interfa class
     * 
     * @param clazz the interfa class
     * @return the new EspAction
     */
    IAction alloc(Class<?> clazz);
}
