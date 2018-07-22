package com.vijayanix.iot.model.device.plugs;

import com.vijayanix.iot.model.device.plugs.IStatusPlugs.IAperture;
import com.vijayanix.iot.model.device.Device;


import java.util.List;

public class DevicePlugs extends Device implements IDevicePlugs
{
    private IStatusPlugs mStatus;
    
    private List<IStatusPlugs.IAperture> mApertureList;
    
    public DevicePlugs()
    {
        mStatus = new StatusPlugs();
        mApertureList = mStatus.getStatusApertureList();
    }
    
    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        DevicePlugs device = (DevicePlugs)super.clone();
        
        device.setStatusPlugs((StatusPlugs)((StatusPlugs)mStatus).clone());
        
        return device;
    }

    @Override
    public IStatusPlugs getStatusPlugs()
    {
        return mStatus;
    }

    @Override
    public void setStatusPlugs(IStatusPlugs status)
    {
        mStatus.setStatusApertureList(status.getStatusApertureList());
    }

    @Override
    public List<IAperture> getApertureList()
    {
        return mStatus.getStatusApertureList();
    }

    @Override
    public void setApertureList(List<IStatusPlugs.IAperture> apertures)
    {
        mStatus.setStatusApertureList(apertures);
    }

    @Override
    public void updateAperture(IAperture newAperture)
    {
        for (IAperture aperture : mApertureList)
        {
            if (aperture.getId() == newAperture.getId())
            {
                aperture.setTitle(newAperture.getTitle());
                aperture.setOn(newAperture.isOn());
                return;
            }
        }
        
        mApertureList.add(newAperture);
    }

    @Override
    public boolean updateApertureOnOff(IAperture newAperture)
    {
        for (IAperture aperture : mApertureList)
        {
            if (aperture.getId() == newAperture.getId())
            {
                aperture.setOn(newAperture.isOn());
                return true;
            }
        }
        
        return false;
    }
}
