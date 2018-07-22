package com.vijayanix.iot.model.device.plugs;


import com.vijayanix.iot.command.device.IDeviceStatus;

import java.util.List;

public interface IStatusPlugs extends IDeviceStatus {
    // Aperture means hole, Plugs have more than one aperture usually
    interface IAperture
    {
        /**
         * the id is according to the order of the aperture,
         * they are 0,1,2,3...
         * 
         * the device will tell app how many apertures the device has
         * 
         * get the aperture's id in the plugs
         * @return the aperture's id in the plugs
         */
        int getId();
        
        /**
         * set the title of the aperture
         */
        void setTitle(String title);
        
        /**
         * get the title of the aperture
         * @return the title of the aperture
         */
        String getTitle();
        
        /**
         * set whether the aperture is on
         * @param isOn whether the aperture is on
         */
        void setOn(boolean isOn);
        
        /**
         * get whether the aperture is on
         * @return whether the aperture is on
         */
        boolean isOn();
    }
    
    /**
     * set the IAperture list
     * @param list the IAperture list to be set
     */
    void setStatusApertureList(List<IAperture> list);
    
    /**
     * get the IAperture list
     * @return the IAperture list
     */
    List<IAperture> getStatusApertureList();
    
    /**
     * update the aperture thoroughly if the aperture exist,
     * add a new aperture if the aperture doesn't exist
     * 
     * @param newAperture the aperture to be updated or added
     */
    void updateOrAddAperture(IAperture newAperture);
    
    /**
     * update the specific aperture's isOn state( it will only update the specific apertures's isOn state)
     * 
     * @param newAperture the aperture to be updated
     * @return whether the aperture is exist( it will update the specific aperture's isOn state , it the newAperture's
     *         id dones't exist in IStatusPlugs, it will return false)
     */
    boolean updateApertureOnOff(IAperture newAperture);
}
