package com.vijayanix.iot.util;

import android.content.Context;
import android.content.Intent;

import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.ui.device.DevicePlugActivity;


import org.apache.log4j.Logger;

public class DeviceUtil {
    private DeviceUtil() {
    }

    private static final Logger log = Logger.getLogger(DeviceUtil.class);

    /**
     * Get device type's function use class
     * 
     * @param device
     * @return
     */
    public static Class<?> getDeviceClass(IDevice device) {
        IDeviceState state = device.getDeviceState();
        Class<?> _class = null;
        switch (device.getDeviceType()) {
            case PLUG:
                if (state.isStateInternet() || state.isStateLocal()) {
                    _class = DevicePlugActivity.class;
                }
                break;

          /*  case PLUGS:
                if (state.isStateInternet() || state.isStateLocal()) {
                    _class = DevicePlugsActivity.class;
                }
                break;*/

            case NEW:
                log.warn("Click on NEW device, it shouldn't happen");
                break;
        }

        return _class;
    }

    public static Class<?> getLocalDeviceClass(DeviceType type) {
        Class<?> cls = null;
        switch (type) {
            case PLUG:
                cls = DevicePlugActivity.class;
                break;

            default:
                break;
        }

        return cls;
    }

    /**
     * Get the intent which can start device Activity
     * 
     * @param context
     * @param device
     * @return
     */
    public static Intent getDeviceIntent(Context context, IDevice device) {
        Class<?> cls = getDeviceClass(device);
        if (cls != null) {
            Intent intent = new Intent(context, cls);
            intent.putExtra(VijStrings.Key.DEVICE_KEY_KEY, device.getKey());
            return intent;
        } else {
            return null;
        }
    }

    public static int getDeviceIconRes(DeviceType type) {
        int res = 0;
        switch (type) {

            case PLUG:
                res = R.drawable.device_filter_icon_plug;
                break;
            case PLUGS:
                res = R.drawable.device_filter_icon_plugs;
                break;

        }

        return res;
    }

    public static int getDeviceIconRes(IDevice device) {
        int res = 0;
        IDeviceState state = device.getDeviceState();
        boolean isOffline = state.isStateOffline();
        switch (device.getDeviceType()) {

            case PLUG:
                res = isOffline ? R.drawable.device_plug_offline : R.drawable.device_plug_online;
                break;
            case PLUGS:
                res = isOffline ? R.drawable.device_plugs_offline : R.drawable.device_plugs_online;
                break;

        }

        return res;
    }

    public static int getDeviceTypeNameRes(DeviceType type) {
        int result = 0;
        switch (type) {
            case ROOT:
                break;
            case NEW:
                break;
            case REMOTE:
                break;

            case PLUG:
                result = R.string.esp_main_type_plug;
                break;
            case PLUGS:
                result = R.string.esp_main_type_plugs;
                break;

        }

        return result;
    }


}
