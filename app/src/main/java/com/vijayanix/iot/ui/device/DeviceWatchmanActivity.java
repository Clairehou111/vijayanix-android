package com.vijayanix.iot.ui.device;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.watchman.DeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchman;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;

import butterknife.BindView;

public class DeviceWatchmanActivity extends DeviceActivityAbs2 implements View.OnClickListener {


	@BindView(R.id.lightA_switch)
	CheckBox mLightASwitch;
	@BindView(R.id.lightB_switch)
	CheckBox mLightBSwitch;
	private IDeviceWatchman mWatchman;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWatchman = (IDeviceWatchman) mIDevice;


		boolean compatibility = isDeviceCompatibility();
		if (compatibility && !isDeviceArray()) {
			executeGet(SubDatastreamType.ALL);
		}

	}

	@Override
	protected View initControlView() {
		View view = View.inflate(this, R.layout.activity_device_watchman, null);

		mLightASwitch = (CheckBox) view.findViewById(R.id.lightA_switch);
		mLightBSwitch = (CheckBox) view.findViewById(R.id.lightB_switch);
		mLightASwitch.setOnClickListener(this);
		mLightBSwitch.setOnClickListener(this);
		return view;
	}

	@Override
	protected void executePrepare() {

	}

	@Override
	protected void executeFinish(int datastreamType, int command, boolean result) {
		IDeviceWatchmanStatus status = mWatchman.getWatchmanStatus();

		switch (datastreamType) {
			case SubDatastreamType.ALL:
				mLightASwitch.setChecked(status.getLightA() == 1);
				mLightBSwitch.setChecked(status.getLightB() == 1);
				break;
			case SubDatastreamType.DEVICE_TYPE:
				break;
			case SubDatastreamType.WATCHMAN_LIGHTA:
				mLightASwitch.setChecked(status.getLightA() == 1);
				break;
			case SubDatastreamType.WATCHMAN_LIGHTB:
				mLightBSwitch.setChecked(status.getLightB() == 1);
				break;

		}

	}

	@Override
	protected void setViewOffline() {
		Log.d("DeviceWatchmanActivity","setViewOffline");
		mControlView.setBackgroundColor(getResources().getColor(R.color.background_gray));
		mControlView.getBackground().setAlpha(200); //透明度
		mLightASwitch.setClickable(false);
		mLightBSwitch.setClickable(false);

	}

	@Override
	protected void setViewOnline() {
		Log.d("DeviceWatchmanActivity","setViewOnline");
		mControlView.setBackgroundColor(getResources().getColor(R.color.white));

	}


	private void switchLightA() {
		boolean isOn = mLightASwitch.isChecked();
		DeviceWatchmanStatus status = new DeviceWatchmanStatus();
		status.setLightA(isOn ? 1 : 0);
		executePost(SubDatastreamType.WATCHMAN_LIGHTA, status);
	}

	private void switchLightB() {
		boolean isOn = mLightBSwitch.isChecked();
		DeviceWatchmanStatus status = new DeviceWatchmanStatus();
		status.setLightB(isOn ? 1 : 0);
		executePost(SubDatastreamType.WATCHMAN_LIGHTB, status);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.lightA_switch:
				switchLightA();
				break;
			case R.id.lightB_switch:
				switchLightB();
				break;

		}
	}
}
