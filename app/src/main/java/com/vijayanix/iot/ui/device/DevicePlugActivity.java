package com.vijayanix.iot.ui.device;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.plug.StatusPlug;
import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;


public class DevicePlugActivity extends DeviceActivityAbs2 implements OnClickListener
{
    private IDevicePlug mDevicePlug;
    
    private CheckBox mPlugSwitch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDevicePlug = (IDevicePlug) mIDevice;
        
        boolean compatibility = isDeviceCompatibility();
        if (compatibility && !isDeviceArray())
        {
            executeGet(SubDatastreamType.ALL);
        }
    }



    @Override
    protected View initControlView()
    {
        View view = View.inflate(this, R.layout.device_activity_plug, null);
        mPlugSwitch = (CheckBox)view.findViewById(R.id.plug_switch);

        mPlugSwitch.setOnClickListener(this);
        
        return view;
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == mPlugSwitch)
        {
            boolean isOn = mPlugSwitch.isChecked();
            StatusPlug status = new StatusPlug();
            status.setIsOn(isOn);
            
            if (isDeviceArray())
            {
                mDevicePlug.setStatusPlug(status);
            }
            
            executePost(SubDatastreamType.ALL,status);
        }
    }
    
    @Override
    protected void executePrepare()
    {
        
    }

    @Override
    protected void executeFinish(int datastreamType, int command, boolean result) {
        boolean isOn = mDevicePlug.getStatusPlug().isOn();
        mPlugSwitch.setChecked(isOn);
    }

    @Override
    protected void setViewOffline() {

    }

    @Override
    protected void setViewOnline() {

    }


}
