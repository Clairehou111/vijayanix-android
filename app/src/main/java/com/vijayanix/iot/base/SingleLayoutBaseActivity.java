package com.vijayanix.iot.base;

import android.os.Bundle;

import butterknife.ButterKnife;

/**
 * Created by hxhoua on 2018/7/11.
 */

public abstract class SingleLayoutBaseActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		ButterKnife.bind(this);
	}

	protected abstract int getLayout();
}
