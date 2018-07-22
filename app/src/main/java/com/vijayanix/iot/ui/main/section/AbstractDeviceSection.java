package com.vijayanix.iot.ui.main.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.IDevice;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

/**
 * Created by hxhoua on 2017/11/13.
 */

public abstract class AbstractDeviceSection extends MySection implements SectionFilterable {

	protected String title;
	protected List<IDevice> list;
	protected List<IDevice> filteredList;
	protected Context mContext;

	public static final String TAG = "section" ;






	public AbstractDeviceSection(String title, List<IDevice> list, Context context) {
		//this(title,list,context,R.layout.item_device,R.layout.device_header,R.layout.device_group_empty	);

		super(new SectionParameters.Builder(R.layout.item_device)
				.headerResourceId(R.layout.device_header)
				.emptyResourceId(R.layout.device_group_empty)
				.build());
		this.title = title;
		this.list = list;
		this.mContext = context;
		filteredList = new ArrayList<>(list);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}




	@Override
	public int getContentItemsTotal() {
		return filteredList.size();
	}

	@Override
	public RecyclerView.ViewHolder getItemViewHolder(View view) {
		return new ItemViewHolder(view);
	}



	@Override
	public abstract  void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position);

	@Override
	public IDevice getBinderDataSorce(int position) {
		return filteredList.get(position);
	}


	public  List<IDevice> getDatas(){

		return filteredList;
	}


	@Override
	public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
		return new HeaderViewHolder(view);
	}

	@Override
	public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
		DeviceSection.HeaderViewHolder headerHolder = (DeviceSection.HeaderViewHolder) holder;

		headerHolder.tvTitle.setText(title);
	}


	@Override
	public RecyclerView.ViewHolder getEmptyViewHolder(View view) {
		return new EmptyViewHolder(view);
	}


	@Override
	public void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {
		DeviceSection.EmptyViewHolder footerHeader = (DeviceSection.EmptyViewHolder) holder;

		footerHeader.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//loadData(DeviceGroupSection.this);
			}
		});
	}

	@Override
	public int filter(String query) {

		final List<IDevice> results = new ArrayList<>();
		if (query != null) {
			filteredList.clear();
			if (list != null && list.size() > 0) {
				for (final IDevice device : list) {
					if (device.getName().toLowerCase().contains(query)) {
						results.add(device);
					}
				}
			}

			filteredList = results;
		}

		return results.size();
	}


	protected class HeaderViewHolder extends RecyclerView.ViewHolder {

		protected TextView tvTitle;

		HeaderViewHolder(View view) {
			super(view);
			tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		}
	}

	protected class ItemViewHolder extends RecyclerView.ViewHolder {

		protected   View rootView;
		protected ImageView deviceImg;
		protected  TextView deviceName;
		protected  TextView deviceStateDesc;
		/*protected  TextView onOff;
		protected Switch powerSwitch;
		protected ImageButton deviceBind;*/

		ItemViewHolder(View view) {
			super(view);

			rootView = view;
			deviceImg = (ImageView) view.findViewById(R.id.device_img);
			deviceName = (TextView) view.findViewById(R.id.device_name);
			deviceStateDesc = (TextView) view.findViewById(R.id.device_state_desc);
	/*		onOff = (TextView) view.findViewById(R.id.device_onOff);
			powerSwitch = (Switch) view.findViewById(R.id.power_switch);
			deviceBind = (ImageButton) view.findViewById(R.id.device_bind);*/
		}
	}


	protected class EmptyViewHolder extends RecyclerView.ViewHolder {

		protected  View rootView;

		EmptyViewHolder(View itemView) {
			super(itemView);

			rootView = itemView.findViewById(R.id.rootView);
		}
	}




}
