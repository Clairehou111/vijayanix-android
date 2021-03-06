package com.vijayanix.iot.model.device.configure;



import com.vijayanix.iot.action.device.common.IActionDeviceConfigureLocal;
import com.vijayanix.iot.action.device.configure.IActionDeviceConfigureActivateInternet;
import com.vijayanix.iot.model.device.IDevice;

import java.util.concurrent.Future;

/**
 * the device is configured already and will be activated on Server.
 * 

 * 
 */
public interface IDeviceConfigure extends IDevice, IActionDeviceConfigureActivateInternet,
		IActionDeviceConfigureLocal
{
    /**
     * @param mayInterruptIfRunning {@code true} if the thread executing this task should be interrupted; otherwise,
     *            in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already completed normally;
     *         {@code true} otherwise *
     */
    boolean cancel(boolean mayInterruptIfRunning);
    
    /**
     * set the future which is used to cancel activating task
     * 
     * @param future the future which is used to cancel activating task
     */
    void setFuture(Future<?> future);
    
}
