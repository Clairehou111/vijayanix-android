package com.vijayanix.iot.action.device.configure;


import com.vijayanix.iot.model.device.IDevice;

public class ActionDeviceConfigureActivateInternet implements IActionDeviceConfigureActivateInternet
{
    
    @Override
    public IDevice doActionDeviceConfigureActivateInternet(long userId, String userKey, String randomToken)
    {
        // for the historical reason, we use EspCommandDeviceNewActivateInternet to do action belong to
        // the configure device
  /*      IEspCommandDeviceNewActivateInternet command = new EspCommandDeviceNewActivateInternet();
        return command.doCommandNewActivateInternet(userId, userKey, randomToken);*/

        return null;
    }
    
}
