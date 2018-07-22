package com.vijayanix.iot.ui.config;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.vijayanix.iot.R;
import com.vijayanix.iot.base.SingleLayoutBaseActivity;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.ui.main.MainActivity;
import com.vijayanix.iot.widget.DiffuseView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SmartConfigCountdownActivity extends SingleLayoutBaseActivity {


	@BindView(R.id.diffuse_config)
	DiffuseView mDiffuseConfig;
	@BindView(R.id.btn_cancel)
	Button mBtnCancel;
	MaterialDialog mMaterialDialog;
	@BindView(R.id.tv_config)
	TextView mTvConfig;
	private IUser mUser;
	private List<String> newDeviceList;
	private List<IOTAddress> mIOTAddressList;
	private int found;

	String workSSID, workSSIDPsw;

	private AsyncTask configTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setToolBar(R.string.configcountDown_title, true);

		initData();
		startConfig();

	}

	@Override
	protected int getLayout() {
		return R.layout.activity_smart_config_countdown;
	}


	private void initData() {
		Intent intent = getIntent();
		workSSID = intent.getStringExtra("workSSID");
		workSSIDPsw = intent.getStringExtra("workSSIDPsw");
		mUser = BUser.getBuilder().getInstance();
		newDeviceList = new ArrayList<String>();
	}

	void startConfig() {
		mDiffuseConfig.setCoreColor(appPreferences.getFABColorPref());
		mDiffuseConfig.start();
		configTask = new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] params) {

				String bssid = getConnectionBssid();
				boolean isSsidHidden = false;
				mIOTAddressList = mUser.configDeviceEsptouch(workSSID, bssid, workSSIDPsw, isSsidHidden, mEsptouchListener);
				if(mIOTAddressList.isEmpty()){
					return null;
				}
				for(IOTAddress address: mIOTAddressList){
					newDeviceList.add(address.getBSSID()+"      "+address.getDeviceTypeEnum().toString());
				}
				mUser.doneAllAddDevices();
				return null;
			}




			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				finishConfig();
			}
		};

		configTask.execute();
	}

	private IEsptouchListener mEsptouchListener = new IEsptouchListener() {

		@Override
		public void onEsptouchResultAdded(final IEsptouchResult result) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					found++;
					mTvConfig.setText(getResources().getString(R.string.found_device,found));
				}

			});
		}
	};


	private String getConnectionBssid() {
		WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			return wifiInfo.getBSSID();
		}

		return null;
	}


	void finishConfig() {
		if (mDiffuseConfig != null){
			mDiffuseConfig.stop();
		}
		mMaterialDialog = getMaterialDialog();
		mMaterialDialog.show();
	}


	private MaterialDialog getMaterialDialog() {
		Context context = this;
		final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(context);

		for (int i = 0; i < newDeviceList.size(); i++) {
			adapter.add(new MaterialSimpleListItem.Builder(context)
					.content(newDeviceList.get(i))
					.backgroundColor(Color.WHITE)
					.build()
			);
		}


		MaterialDialog.Builder materialBuilder = new MaterialDialog.Builder(context)
				.title(context.getResources().getString(R.string.found_device, newDeviceList.size()))
				.icon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_pro))
				.adapter(adapter, new MaterialDialog.ListCallback() {
					@Override
					public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
					}
				})
				.widgetColor(Color.RED)//改变checkbox的颜色
				//多选框添加
				.itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
						Log.i("MaterialDialog","onSelection is clicked");
						return true;//false 的时候没有选中样式
					}

				})
				.positiveText("confirm")
				.negativeText("cancel")
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						Log.i("MaterialDialog","onPositive is clicked");
						startActivity(new Intent(SmartConfigCountdownActivity.this, MainActivity.class));

						mUser.addLocalDeviceListAysnc(mIOTAddressList);
					}


				})
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						Log.i("MaterialDialog","onNegative is clicked");
						startActivity(new Intent(SmartConfigCountdownActivity.this, MainActivity.class));
					}
				});

		return materialBuilder.build();
	}


	@OnClick(R.id.btn_cancel)
	public void onViewClicked() {
		mDiffuseConfig.stop();
		if (configTask != null) {
			configTask.cancel(true);
			startActivity(new Intent(SmartConfigCountdownActivity.this, MainActivity.class));
		}
		mUser.cancelAllAddDevices();
	}
}
