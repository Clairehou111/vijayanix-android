package com.vijayanix.iot.ui.config;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.vijayanix.iot.R;
import com.vijayanix.iot.base.SingleLayoutBaseActivity;

import butterknife.BindView;


public class SmartReadyActivity extends SingleLayoutBaseActivity implements OnClickListener {

	@BindView(R.id.btnNext)
	Button mBtnNext;
	@BindView(R.id.cbSelect)
	CheckBox mCbSelect;
	@BindView(R.id.tvSelect)
	TextView mTvSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToolBar(R.string.airlink_ready_title, true);

		initEvent();
	}

	@Override
	protected int getLayout() {
		return R.layout.actvity_smart_config_ready;
	}


	private void initEvent() {
		mBtnNext.setOnClickListener(this);
		mTvSelect.setOnClickListener(this);
		mBtnNext.setClickable(false);
		mBtnNext.setBackgroundResource(R.drawable.button_shape_gray);

		mCbSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//mBtnNext.setBackgroundDrawable(GosDeploy.setButtonBackgroundColor());
					mBtnNext.setClickable(true);
				} else {
					mBtnNext.setBackgroundResource(R.drawable.button_shape_gray);
					mBtnNext.setClickable(false);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnNext:
				Intent lastIntent = getIntent();
				Intent intent = new Intent(this, SmartConfigCountdownActivity.class);
				intent.putExtras(lastIntent.getExtras());
				startActivity(intent);
				break;

			case R.id.tvSelect:
				if (mCbSelect.isChecked()) {
					mCbSelect.setChecked(false);
				} else {
					mCbSelect.setChecked(true);
				}
				break;

			default:
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, SmartCongfigChooseWiFiActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				this.finish();
				break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(this, SmartCongfigChooseWiFiActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		this.finish();
		return true;
	}

}
