package com.vijayanix.iot.model.device.cache;

/**
 * the default Handler for IUser to process the device in @see DeviceCache
 * 

 * 
 */
public interface IDeviceCacheHandler
{
    /**
     * when IUser start handle, it can't be interruptible
     * 
     * @return nothing(use Void to indicate it is a blocking method)
     */
    Void handleUninterruptible(boolean isStateMachine);
}
