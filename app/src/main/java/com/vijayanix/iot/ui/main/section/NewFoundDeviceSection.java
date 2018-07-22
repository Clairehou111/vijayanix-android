package com.vijayanix.iot.ui.main.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.vijayanix.iot.model.device.IDevice;

import java.util.List;

/**
 * Created by hxhoua on 2017/11/11.
 */

public class NewFoundDeviceSection extends DeviceSection {

	public NewFoundDeviceSection(String title, List<IDevice> list, Context context) {
		super(title, list, context);
	}

	@Override
	protected void powerSwitchOnCheckedChanged(CompoundButton buttonView, IDevice espDevice) {

	}

	public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {

		final ItemViewHolder itemHolder = (ItemViewHolder) holder;

		final IDevice espDevice = getBinderDataSorce(position);

		//DeviceUtil.printDevice("DeviceListFragment",espDevice);

		itemHolder.deviceName.setText(espDevice.getName());
		itemHolder.deviceImg.setImageResource(DeviceUtil.getDeviceImg(espDevice.getDeviceType()));

		itemHolder.deviceStateDesc.setText(espDevice.getName());
		//itemHolder.onOff.setText("未绑定");
//		itemHolder.deviceBind.setVisibility(View.VISIBLE);
//		itemHolder.powerSwitch.setVisibility(View.GONE);
		itemHolder.rootView.setClickable(false);
		Log.i(TAG,"rootView isClickable  "+ itemHolder.rootView.isClickable());

//		itemHolder.deviceBind.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				deviceBindOnClick(v,espDevice);
//			}
//		});

	}


	protected void deviceBindOnClick(View view, IDevice espDevice){
		Toast.makeText(mContext, "要绑定设备吗", Toast.LENGTH_SHORT).show();

		Log.i("DeviceListFragment", "要绑定设备" );
	}


}
