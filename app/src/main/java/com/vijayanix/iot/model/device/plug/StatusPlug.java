package com.vijayanix.iot.model.device.plug;

public class StatusPlug implements IStatusPlug, Cloneable
{
    private boolean mIsOn;
    
    @Override
    public boolean isOn()
    {
        return mIsOn;
    }
    
    @Override
    public void setIsOn(boolean isOn)
    {
        mIsOn = isOn;
    }
    
    @Override
    public String toString()
    {
        return "StatusPlug: (mIsOn=[" + mIsOn + "])";
    }
    
    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
