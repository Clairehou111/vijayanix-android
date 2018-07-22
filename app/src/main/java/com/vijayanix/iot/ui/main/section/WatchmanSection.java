package com.vijayanix.iot.ui.main.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchman;

import java.util.List;


/**
 * Created by hxhoua on 2017/11/11.
 */

public class WatchmanSection extends DeviceSection{


	public WatchmanSection(String title, List<IDevice> list, Context context) {
		super(title, list, context);
	}




	@Override
	public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
		final ItemViewHolder itemHolder = (ItemViewHolder) holder;

		final IDeviceWatchman espDevice =  (IDeviceWatchman)getBinderDataSorce(position);


		Log.i("DeviceListFragment", "in onBindItemViewHolder");
//		DeviceUtil.printDevice("DeviceListFragment", espDevice);

		itemHolder.deviceName.setText(espDevice.getName());
		itemHolder.deviceImg.setImageResource(DeviceUtil.getDeviceImg(espDevice.getDeviceType()));
	/*	itemHolder.deviceBind.setVisibility(View.GONE);
		itemHolder.onOff.setVisibility(View.GONE);*/


		if (DeviceUtil.isOnline(espDevice)) {
			itemHolder.rootView.setEnabled(true);
			itemHolder.deviceStateDesc.setText(IOTApplication.getContext().getResources().getText(R.string.online));
			itemHolder.itemView.setBackgroundColor(IOTApplication.getContext().getResources().getColor(R.color.white));
			//itemHolder.onOff.setText(device.isPowerOn() ? "开" : "关");
//			itemHolder.powerSwitch.setChecked(lightAIsOn);

		} else {

			//itemHolder.rootView.setBackgroundColor(Color.TRANSPARENT);
			//itemHolder.itemView.setClickable(false);
			//itemHolder.rootView.setClickable(false);
			itemHolder.rootView.setEnabled(false);
			itemHolder.deviceStateDesc.setText(IOTApplication.getContext().getResources().getText(R.string.offline));
//			itemHolder.powerSwitch.setVisibility(View.INVISIBLE);

			itemHolder.itemView.setBackgroundColor(IOTApplication.getContext().getResources().getColor(R.color.background_gray));
		}



		super.setClickEvent(itemHolder,espDevice);


	}


	@Override
	protected void powerSwitchOnCheckedChanged(CompoundButton buttonView, IDevice espDevice) {

	}





}
