package com.vijayanix.iot.ui.config;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vijayanix.iot.R;
import com.vijayanix.iot.base.SingleLayoutBaseActivity;
import com.vijayanix.iot.util.BaseApiUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vijayanix.iot.ui.config.ConfigConstant.SSID;
import static com.vijayanix.iot.ui.config.ConfigConstant.SoftAP_Start;

public class SelectWifiActivity extends SingleLayoutBaseActivity {


	@BindView(R.id.recycle_wifi_list)
	RecyclerView mRecycleWifiList;
	MyRecyclerViewAdapter mAdapter;

	List<String> wifiList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		mRecycleWifiList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		mAdapter = new MyRecyclerViewAdapter(wifiList);
		mRecycleWifiList.setAdapter(mAdapter);

		mAdapter.setItemClickListener(new AdapterOnclick());


	}

	private interface OnItemClickListener{
		void onItemClick(View view);
	}

	private class AdapterOnclick implements OnItemClickListener{

		@Override
		public void onItemClick(View view) {
			int position = mRecycleWifiList.getChildAdapterPosition(view);
			MyRecyclerViewAdapter.ViewHolder myHodler = (MyRecyclerViewAdapter.ViewHolder) mRecycleWifiList.getChildViewHolder(view);
			 CharSequence ssid = myHodler.mTxtSsid.getText();
			Toast.makeText(SelectWifiActivity.this, "onItemClick : " + position+"ssid:  "+ ssid, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra(SSID,ssid);
			setResult(RESULT_OK,intent);
			finish();
		}
	}

	private void initData(){
		List<ScanResult> rsList = BaseApiUtil.scan();
		wifiList.clear();
		for (ScanResult result : rsList) {
			if(!result.SSID.contains(SoftAP_Start)){
				wifiList.add(result.SSID);
			}
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_select_wifi;
	}


	class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

		private OnItemClickListener mItemClickListener;

		public void setItemClickListener(OnItemClickListener itemClickListener) {
			mItemClickListener = itemClickListener;
		}

		private List<String> list;

		public MyRecyclerViewAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gos_wifi_list, parent, false);
			ViewHolder viewHolder = new ViewHolder(view);
			view.setOnClickListener(this);
			return viewHolder;

		}

		public void onClick(View v) {
			if (mItemClickListener!=null){
				mItemClickListener.onItemClick(v);
			}
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.mTxtSsid.setText(list.get(position));
		}

		@Override
		public int getItemCount() {
			return list.size();
		}



		class ViewHolder extends RecyclerView.ViewHolder {
			@BindView(R.id.txt_ssid)
			TextView mTxtSsid;
			public ViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}


		}
	}

}
