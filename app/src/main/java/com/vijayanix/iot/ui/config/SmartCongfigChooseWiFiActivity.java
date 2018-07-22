package com.vijayanix.iot.ui.config;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vijayanix.iot.R;
import com.vijayanix.iot.base.SingleLayoutBaseActivity;
import com.vijayanix.iot.db.ApDBManager;
import com.vijayanix.iot.db.interfa.IApDB;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.UtilsDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vijayanix.iot.ui.config.ConfigConstant.*;


public class SmartCongfigChooseWiFiActivity extends SingleLayoutBaseActivity implements OnClickListener {

	public static final int WIFI_LIST_REQUEST_CODE = 999;
	@BindView(R.id.etSSID)
	EditText mEtSSID;
	@BindView(R.id.imgWiFiList)
	ImageView mImgWiFiList;
	@BindView(R.id.etPsw)
	EditText mEtPsw;
	@BindView(R.id.cbLaws)
	CheckBox mCbLaws;
	@BindView(R.id.btnNext)
	Button mBtnNext;




	/**
	 * 配置用参数
	 */
	private String workSSID, workSSIDPsw,bssid;

	private ApDBManager mApDBManager;

	private WifiManager mWifiManager;


	@Override
	protected int getLayout() {
		return R.layout.activity_choose_device_workwifi;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setToolBar(R.string.add_device, true);
		ButterKnife.bind(this);
		initData();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		workSSID = BaseApiUtil.getWifiConnectedSsid();
		bssid = getConnectionBssid();
		workSSIDPsw = getCurrentWifiPassword(bssid);
		mEtSSID.setText(workSSID);
		mEtPsw.setText(workSSIDPsw);
		Log.i("AirlinkChooseDevice","onResume");
	}

	private void initData() {

		mApDBManager = ApDBManager.getInstance();
		mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	}


	private void initEvent() {
		mBtnNext.setOnClickListener(this);
		mImgWiFiList.setOnClickListener(this);

		mCbLaws.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String psw = mEtPsw.getText().toString();

				if (isChecked) {
					mEtPsw.setInputType(0x90);
				} else {
					mEtPsw.setInputType(0x81);
				}
				mEtPsw.setSelection(psw.length());
			}
		});
	}


	private String getConnectionBssid() {
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			return wifiInfo.getBSSID();
		}

		return null;
	}

	private String getCurrentWifiPassword(String currentBssid) {
		Log.i("currentBssid",currentBssid);
		List<IApDB> apDBList = mApDBManager.getAllApDBList();
		for (IApDB ap : apDBList) {
			if (ap.getBssid().equals(currentBssid)) {
				return ap.getPassword();
			}
		}

		return "";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnNext:
				workSSID = mEtSSID.getText().toString();
				workSSIDPsw = mEtPsw.getText().toString();

				mApDBManager.insertOrReplace(bssid, workSSID, workSSIDPsw);


				if (TextUtils.isEmpty(workSSID)) {
					Toast.makeText(SmartCongfigChooseWiFiActivity.this, R.string.choose_wifi_list_title,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(workSSIDPsw)) {

					MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							goToDeviceIsReadyStep(workSSID, workSSIDPsw);
						}
					};

					UtilsDialog.showTitleContent2(this, getResources().getString(R.string.prompt), getResources().getString(R.string.workwifi_isnull), positiveCallback, null);

				} else {
					goToDeviceIsReadyStep(workSSID, workSSIDPsw);
				}

				break;

			case R.id.imgWiFiList:

				//startActivityForResult(new Intent(this,SelectWifiActivity.class),WIFI_LIST_REQUEST_CODE);
				goToWifSettings();

				break;


			default:
				break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == WIFI_LIST_REQUEST_CODE&& resultCode == RESULT_OK ){
			Bundle extras = data.getExtras();
			workSSID = extras.getString(SSID);
			workSSIDPsw = extras.getString(SSID_PSW);
			mEtSSID.setText(workSSID);
			mEtPsw.setText(workSSIDPsw);
		}
	}

	private void goToDeviceIsReadyStep(String ssid, String pwd) {
		Intent intent = new Intent(this, SmartReadyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("workSSID", ssid);
		bundle.putString("workSSIDPsw", pwd);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void goToWifSettings(){
		Intent it = new Intent();
		ComponentName cn = new ComponentName("com.android.settings","com.android.settings.wifi.WifiSettings");
		it.setComponent(cn);
		startActivity(it);
	}



}
