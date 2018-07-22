package com.vijayanix.iot.ui.main;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.Drawer;
import com.vijayanix.iot.R;
import com.vijayanix.iot.base.SingleLayoutBaseActivity;
import com.vijayanix.iot.common.net.proxy.ProxyServerImpl;
import com.vijayanix.iot.common.net.udp.UdpServer;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.mqtt.ReceivedMessage;
import com.vijayanix.iot.ui.config.SmartCongfigChooseWiFiActivity;
import com.vijayanix.iot.ui.main.section.DeviceSection;
import com.vijayanix.iot.ui.main.section.DeviceUtil;
import com.vijayanix.iot.ui.main.section.MySectionedRecyclerViewAdapter;
import com.vijayanix.iot.ui.main.section.SectionFilterable;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.VijStrings;
import com.vijayanix.iot.util.UtilsUI;
import com.vijayanix.iot.widget.VerticalSwipeRefreshLayout;

import org.apache.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import butterknife.BindView;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.vijayanix.iot.IOTApplication.getContext;


public class MainActivity extends SingleLayoutBaseActivity implements SearchView.OnQueryTextListener {
	private static final Logger log = Logger.getLogger(MainActivity.class);
	@BindView(R.id.device_list)
	RecyclerView mDeviceRecyclerView;
	@BindView(R.id.swipe_refresh)
	VerticalSwipeRefreshLayout mSwipeRefresh;
	@BindView(R.id.no_results)
	LinearLayout mNoResults;
	@BindView(R.id.fab)
	FloatingActionButton fab;


	private MySectionedRecyclerViewAdapter deviceAdapter;
	private MenuItem searchItem;

	private IUser mUser;
	private Handler mRefreshHandler;
	private static final int MSG_AUTO_REFRESH = 0;
	private static final int MSG_UPDATE_TEMPDEVICE = 1;
	private static final int MSG_TWINKLE = 2;
	private static final int MSG_STOP_REFRESH = 3;
	private static final int MSG_UPDATE_ITEM = 4;

	private static final int MAX_TASKS_IN_SYN = 1;

	private BlockingQueue<String> taskQueue = new ArrayBlockingQueue(MAX_TASKS_IN_SYN);
	private static final String STRUCTURE = "STRUCTURE";
	private static final String SIGNLEITEM = "SIGNLEITEM";



	@Override
	protected int getLayout() {
		return R.layout.activity_main;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setToolBar("IOT app", false);
		log.debug("Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);

		mUser = BUser.getBuilder().loadUser();

		BaseApiUtil.submit(new Runnable() {
			@Override
			public void run() {
				// Start local proxy server
				ProxyServerImpl.getInstance().start();
				// Start UDP server
				UdpServer.INSTANCE.open();
			}
		});


		setUIElements();

		registerBroadcaster();

		mRefreshHandler = new RefreshHandler();

		BaseApiUtil.submit(new Runnable() {
			@Override
			public void run() {
				log.debug("task for refreshview view is running");
				while (true) {
				/*	try {
						taskQueue.take();
					*//*	taskQueue.poll();
						taskQueue.remove();*//*
						refreshView();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					if (taskQueue.poll() != null) {
						log.debug("poll from taskQueue");
						refreshView();
					}

				}
			}
		});

		EventBus.getDefault().register(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		mUser.doActionUserLoginDB2();
	}

	private void setUIElements() {

		Drawer drawer = UtilsUI.setNavigationDrawer(this, this, mToolbar);
		mDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mDeviceRecyclerView.setItemAnimator(new DefaultItemAnimator());//增加、移除动画效果

		((SimpleItemAnimator) mDeviceRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false); //不闪烁

		deviceAdapter = new MySectionedRecyclerViewAdapter();
		mDeviceRecyclerView.setAdapter(deviceAdapter);

		setFab();
		setPullToRefreshView();
	}

	private void setFab() {

	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			fab.setBackgroundTintList(ColorStateList.valueOf(appPreferences.getFABColorPref()));
		}

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, SmartCongfigChooseWiFiActivity.class));
			}
		});*/


		fab.setColorNormal(appPreferences.getFABColorPref());
		fab.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//UtilsDialog.showProFeatures(context);
				startActivity(new Intent(MainActivity.this, SmartCongfigChooseWiFiActivity.class));
			}
		});

	/*	FloatingActionButton fab_smart = (FloatingActionButton) findViewById(R.id.fab_add_smartconfig);
		FloatingActionButton fab_softap = (FloatingActionButton) findViewById(R.id.fab_add_softap);
		fab_smart.setColorNormal(appPreferences.getFABColorPref());
		fab_smart.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));

		fab_softap.setColorNormal(appPreferences.getFABColorPref());
		fab_softap.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));

		fab_smart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//UtilsDialog.showProFeatures(context);
				startActivity(new Intent(MainActivity.this, SmartCongfigChooseWiFiActivity.class));
			}
		});


		fab_softap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//UtilsDialog.showProFeatures(context);
				log.info("go to softap config");
			}
		});*/
	}

	private void setPullToRefreshView() {
		mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				scanAll();
			}
		});

	}

	private void registerBroadcaster() {
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

		IntentFilter deviceFilter = new IntentFilter(VijStrings.Action.UI_REFRESH_LOCAL_DEVICES);
		deviceFilter.addAction(VijStrings.Action.DEVICES_ARRIVE_PULLREFRESH);
		deviceFilter.addAction(VijStrings.Action.DEVICES_ARRIVE_STATEMACHINE);
		broadcastManager.registerReceiver(mDeviceReceiver, deviceFilter);
	}

/*

	private void prepare() {

		mUser.doActionUserLoginDB2();
		// Start local proxy server
		ProxyServerImpl.getInstance().start();
		// Start UDP server
		UdpServer.INSTANCE.open();


	}
*/

	private BroadcastReceiver mDeviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(VijStrings.Action.DEVICES_ARRIVE_STATEMACHINE)) {
				Message message = Message.obtain();
				message.what = MSG_UPDATE_ITEM;
				message.setData(intent.getExtras());
				mRefreshHandler.sendMessage(message);

			} else if (action.equals(VijStrings.Action.DEVICES_ARRIVE_PULLREFRESH)) {
				mRefreshHandler.sendEmptyMessage(MSG_AUTO_REFRESH);
			}
		}
	};


	private class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_AUTO_REFRESH:
					taskQueue.offer(STRUCTURE);
					log.debug("offer to taskQueue");
					break;

				case MSG_UPDATE_ITEM:
					String bssid = msg.getData().getString("bssid");
					refreshItem(bssid);
					break;

			}
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		mSwipeRefresh.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefresh.setRefreshing(true);
			}
		});

		scanAll();
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String newText) {

		for (Section section : deviceAdapter.getSectionsMap().values()) {
			int result;
			if (section instanceof SectionFilterable) {
				result = ((SectionFilterable) section).filter(newText);
				section.setVisible(result != 0);
			}
		}
		deviceAdapter.notifyDataSetChanged();
		Log.i(TAG, "deviceAdapter.getItemCount()" + deviceAdapter.getItemCount());
		setResultsMessage(deviceAdapter.getItemCount() > 0);
		return false;
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setOnQueryTextListener(this);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}


	private void setResultsMessage(Boolean result) {
		if (result) {
			mNoResults.setVisibility(View.GONE);
		} else {
			mNoResults.setVisibility(View.VISIBLE);
		}
	}


	EnumMap<DeviceType, Map<String, Integer>> postions = null;
	HashMap<String, String> tags = null;
	private List<DeviceSection> mDeviceSections;


	class GetDevicesTask extends AsyncTask<List<IDevice>, Void, List<IDevice>> {
		@Override
		protected void onPreExecute() {
			log.debug("onPreExecute");
			if (!mSwipeRefresh.isRefreshing()) {

				mSwipeRefresh.post(new Runnable() {
					@Override
					public void run() {
						mSwipeRefresh.setEnabled(true);
						mSwipeRefresh.setRefreshing(true);
					}
				});
			}

			log.debug("before removeAllSections");
			deviceAdapter.removeAllSections();
			mDeviceSections = null;
			postions = null;
			log.debug("removeAllSections" + Thread.currentThread() + "      " + this.toString());
		}

		@Override
		protected List<IDevice> doInBackground(List<IDevice>... params) {

			log.debug("doInBackground" + Thread.currentThread() + "      " + this.toString());

			if (this.isCancelled()) {
				mDeviceSections = Collections.emptyList();
				log.debug("doInBackground   isCancelled" + isCancelled() + "  " + Thread.currentThread() + "      " + this.toString());
				return null;
			}

			postions = new EnumMap<>(DeviceType.class);
			tags = new HashMap<>();
			mDeviceSections = DeviceUtil.getDeviceSections(getContext(), params[0], postions);
			return params[0];

		}

		@Override
		protected void onPostExecute(List<IDevice> deviceList) {

			if (mDeviceSections.isEmpty()) {
				/*MySection section = new NewFoundDeviceSection("无设备", Collections.<DeviceDisplay>emptyList(), getContext());
				section.setState(Section.State.EMPTY);
				section.setHasHeader(false);
				deviceAdapter.addSection(section);
				Log.i(TAG,"new Empty section"+ "      " + this.toString());*/
				setResultsMessage(false);
			} else {
				setResultsMessage(true);
				for (DeviceSection section : mDeviceSections) {
					tags.put(section.getTitle(),deviceAdapter.addSection(section));
				}
				deviceAdapter.notifyDataSetChanged();
			}

			if (searchItem != null) {
				searchItem.setVisible(true);
			}

			mSwipeRefresh.post(new Runnable() {
				@Override
				public void run() {
					if (mSwipeRefresh.isRefreshing()) {
						mSwipeRefresh.setRefreshing(false);
					}
				}
			});
			mSwipeRefresh.setEnabled(true);

		}
	}


	/**
	 * update one particular item
	 *
	 * @param bssid
	 */
	private void refreshItem(String bssid) {

		IDevice espDevice = mUser.getFoundedDeviceByssid(bssid);
		DeviceType deviceType = espDevice.getDeviceType();

		Map<String, Integer> posMap = postions.get(deviceType);
		String tag = tags.get(deviceType.toString());
		int pos = posMap.get(bssid);
		deviceAdapter.notifyItemChangedInSection(tag,pos);


/*
		MySection section = (MySection) deviceAdapter.getSection(tag);
		section.getBinderDataSorce(pos) = espDevice; //first:update sourcedata
		deviceAdapter.notifyItemChangedInSection(tag, pos); //second: update recyclerview*/
	}

	/**
	 * update whole recyclerview
	 */
	private void refreshView() {
		log.debug("refreshView ..... ");
		new GetDevicesTask().execute(mUser.loadAllDevices());
	}


	private void scanAll() {
		log.debug("scaning devices ");

		if (!BaseApiUtil.isNetworkAvailable()) {
			Toast.makeText(this, R.string.esp_main_network_enable_msg, Toast.LENGTH_SHORT).show();
			mUser.doActionRefreshStaDevices(false);
			return;
		}

		mUser.doActionRefreshDevices();

		mSwipeRefresh.post(new Runnable() {
			@Override
			public void run() {
				if (mSwipeRefresh.isRefreshing()) {
					mSwipeRefresh.setRefreshing(false);
				}
			}
		});

	}




	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void processMqttMessage(ReceivedMessage receivedMessage) {
		mUser.processMqttMessage(receivedMessage);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}


}
