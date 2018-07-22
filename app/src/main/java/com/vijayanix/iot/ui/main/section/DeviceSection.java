package com.vijayanix.iot.ui.main.section;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.ui.device.DevicePlugActivity;
import com.vijayanix.iot.ui.device.DeviceWatchmanActivity;
import com.vijayanix.iot.util.VijStrings;

import org.apache.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by hxhoua on 2017/11/5.
 */

public abstract class DeviceSection extends AbstractDeviceSection{

	private static final Logger log = Logger.getLogger(PlugSection.class);



	public DeviceSection(String title, List<IDevice> list, Context context) {
		//this(title,list,context,R.layout.item_device,R.layout.device_header,R.layout.device_group_empty	);

		super(title,list,context);
	}





	protected void setClickEvent(final ItemViewHolder itemHolder, final IDevice espDevice){

		itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rootViewOnClick(v,itemHolder,espDevice);
			}
		});


	/*	itemHolder.powerSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				powerSwitchOnCheckedChanged((Switch)v,espDevice);
			}
		}
		);*/



	}



	private void rootViewOnClick(View view, ItemViewHolder itemHolder, IDevice espDevice){

		Toast.makeText(mContext, String.format("Clicked on position #%s of Section %s", mAdapter.getPositionInSection(itemHolder.getAdapterPosition()), title), Toast.LENGTH_SHORT).show();

		Bundle bundle = new Bundle();
		//bundle.putParcelable("espDevice", espDevice);
		Intent intent = new Intent(mContext,getDeviceControllerActivity(espDevice));
		intent.putExtra(VijStrings.Key.DEVICE_KEY_KEY,espDevice.getKey());
		intent.putExtras(bundle);
		mContext.startActivity(intent);
		Log.i(TAG,"in didSetSubscribe GIZ_SDK_SUCCESS");

	}


	protected abstract void powerSwitchOnCheckedChanged(CompoundButton buttonView, IDevice espDevice);

	private Class getDeviceControllerActivity(IDevice espDevice){
		switch (espDevice.getDeviceType()){
			case PLUG:
				return DevicePlugActivity.class;

			case WATCHMAN:
				return DeviceWatchmanActivity.class;
		}
		throw  new InvalidParameterException();

	}




/*
	@Override
	public int filter(String query) {

		final List<DeviceDisplay> results = new ArrayList<>();
		if (query != null) {
			filteredList.clear();
			if (list != null && list.size() > 0) {
				for (final DeviceDisplay device : list) {
					if (device.getEspDevice().getName().toLowerCase().contains(query)) {
						results.add(device);
					}
				}
			}

			filteredList = results;
		}

		return results.size();
	}*/


}