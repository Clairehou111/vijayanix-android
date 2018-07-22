package com.vijayanix.iot.model.device.plugs;



import com.vijayanix.iot.model.device.IDevice;

import java.util.List;

public interface IDevicePlugs extends IDevice, Cloneable
{
    int TIMER_TAIL_LENGTH = 2;
    
    IStatusPlugs getStatusPlugs();
    
    void setStatusPlugs(IStatusPlugs status);
    
    List<IStatusPlugs.IAperture> getApertureList();
    
    void setApertureList(List<IStatusPlugs.IAperture> apertures);
    
    void updateAperture(IStatusPlugs.IAperture newAperture);
    
    boolean updateApertureOnOff(IStatusPlugs.IAperture newAperture);
}
