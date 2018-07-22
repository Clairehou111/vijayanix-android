package com.vijayanix.iot.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.model.device.cache.IDeviceCache;

/**
 * Created by hxhoua on 2018/6/30.
 */

public class NotifyUIUtil {

	public static void notifyIUser(IDeviceCache.NotifyType type, Object obj)
	{
		//log.info(Thread.currentThread().toString() + "##notifyIUser(NofityType=[" + type + "])");
		Context context = IOTApplication.sharedInstance().getApplicationContext();
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
		Intent intent = null;
		switch (type)
		{
			case PULL_REFRESH:
				intent = new Intent(VijStrings.Action.DEVICES_ARRIVE_PULLREFRESH);
				broadcastManager.sendBroadcast(intent);
				break;
			case STATE_MACHINE_UI:
				intent = new Intent(VijStrings.Action.DEVICES_ARRIVE_STATEMACHINE);
				Bundle bundle = new Bundle();

				bundle.putString("bssid",((String) obj));
				intent.putExtras(bundle);
				broadcastManager.sendBroadcast(intent);

				break;

		}

	}

}
