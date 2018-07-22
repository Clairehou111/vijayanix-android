package com.vijayanix.iot.model.device.plug;


import com.vijayanix.iot.model.device.IDevice;

public interface IDevicePlug extends IDevice
{
    /**
     * Get the status of the plug
     * 
     * @return the status @see IStatusPlug
     */
    IStatusPlug getStatusPlug();
    
    /**
     * Set the status of the plug
     * 
     * @param statusPlug @see IStatusPlug
     */
    void setStatusPlug(IStatusPlug statusPlug);
}
