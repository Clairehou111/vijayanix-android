package com.vijayanix.iot.base;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.view.WindowManager;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.R;
import com.vijayanix.iot.util.AppPreferences;
import com.vijayanix.iot.util.UtilsUI;

public abstract class BaseActivity extends AppCompatActivity {

	// Load Settings
	protected AppPreferences appPreferences;
  protected  final String TAG= this.getClass().getSimpleName();
	/**
	 * 标题栏
	 */
	protected Toolbar mToolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.appPreferences = IOTApplication.getAppPreferences();
		//设为竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}



	protected void setToolBar(int title, boolean enableHomeButton) {
		String titleS = this.getString(title);
		setToolBar(titleS, enableHomeButton);

	}

	protected void setToolBar(CharSequence title, boolean enableHomeButton) {

		SpannableString ssTitle = new SpannableString(title);

		mToolbar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.tb_main);
		setSupportActionBar(mToolbar);

		if (getSupportActionBar() == null) {
			return;
		}
		getSupportActionBar().setTitle(ssTitle);

		if (enableHomeButton) {
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onBackPressed();
				}
			});
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
			mToolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
			if (!appPreferences.getNavigationBlackPref()) {
				getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
			}
		}



	}


}
