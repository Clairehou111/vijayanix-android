package com.vijayanix.iot.model.device.plugs;


public class PlugsAperture implements IStatusPlugs.IAperture
{
    private final int mId;
    
    private String mTitle;
    
    private boolean mOn;
    
    public PlugsAperture(int id)
    {
        mId = id;
    }
    
    @Override
    public int getId()
    {
        return mId;
    }
    
    @Override
    public void setTitle(String title)
    {
        mTitle = title;
    }
    
    @Override
    public String getTitle()
    {
        return mTitle;
    }
    
    @Override
    public void setOn(boolean isOn)
    {
        mOn = isOn;
    }
    
    @Override
    public boolean isOn()
    {
        return mOn;
    }
    
}
