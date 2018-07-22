package com.vijayanix.iot.model.device.plug;


import com.vijayanix.iot.model.device.Device;

public class DevicePlug extends Device implements IDevicePlug
{
    private IStatusPlug mStatusPlug;
    
    public DevicePlug()
    {
        super();
        mStatusPlug = new StatusPlug();

    }
    
    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        DevicePlug device = (DevicePlug)super.clone();
        // deep copy
        IStatusPlug status = device.getStatusPlug();
        device.mStatusPlug = (IStatusPlug)((StatusPlug)status).clone();
        return device;
    }
    
    @Override
    public IStatusPlug getStatusPlug()
    {
        return mStatusPlug;
    }
    
    @Override
    public void setStatusPlug(IStatusPlug statusPlug)
    {
        mStatusPlug = statusPlug;
    }
}
