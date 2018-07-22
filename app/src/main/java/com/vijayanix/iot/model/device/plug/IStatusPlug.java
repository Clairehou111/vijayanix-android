package com.vijayanix.iot.model.device.plug;


import com.vijayanix.iot.command.device.IDeviceStatus;

public interface IStatusPlug extends IDeviceStatus {
    /**
     * Check whether the plug is on
     * 
     * @return whether the plug is on
     */
    boolean isOn();
    
    /**
     * Set whether the plug is on
     * 
     * @param isOn whether the plug is on
     */
    void setIsOn(boolean isOn);
}
