package com.vijayanix.iot.ui.about;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.R;
import com.vijayanix.iot.util.AppPreferences;
import com.vijayanix.iot.util.UtilsApp;
import com.vijayanix.iot.util.UtilsUI;

public class AboutActivity extends AppCompatActivity {
    // Load Settings
    AppPreferences appPreferences;

    // About variables
    //private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.appPreferences = IOTApplication.getAppPreferences();
        //this.context = this;

        setInitialConfiguration();
        setScreenElements();

    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_about);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
            }
        }

    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        TextView appNameVersion = (TextView) findViewById(R.id.app_name);

        header.setBackgroundColor(appPreferences.getPrimaryColorPref());
        appNameVersion.setText(getResources().getString(R.string.app_name) + " " + UtilsApp.getAppVersionName(getApplicationContext()) + " \"" + getResources().getString(R.string.app_codename) + "\"");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}
