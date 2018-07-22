package com.vijayanix.iot.ui.main.section;

import com.vijayanix.iot.model.device.IDevice;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

/**
 * Created by hxhoua on 2017/11/6.
 */

public abstract class MySection extends Section {

   protected MySectionedRecyclerViewAdapter mAdapter ;

	public MySection(SectionParameters sectionParameters) {
		super(sectionParameters);
	}

	public MySectionedRecyclerViewAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(MySectionedRecyclerViewAdapter adapter) {
		mAdapter = adapter;
	}

	public abstract IDevice getBinderDataSorce(int position);
}
