package com.vijayanix.iot;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import com.facebook.stetho.Stetho;
import com.vijayanix.iot.common.log.InitLogger;
import com.vijayanix.iot.common.net.wifi.WifiAdmin;
import com.vijayanix.iot.common.threadpool.CachedThreadPool;
import com.vijayanix.iot.db.ApDBManager;
import com.vijayanix.iot.db.DeviceDBManager;
import com.vijayanix.iot.db.UserDBManager;
import com.vijayanix.iot.db.greendao.gen.DaoMaster;
import com.vijayanix.iot.db.greendao.gen.DaoSession;
import com.vijayanix.iot.mqtt.Mqtt;
import com.vijayanix.iot.util.AppPreferences;
import com.vijayanix.iot.util.VijStrings;

public class IOTApplication extends Application {
    private static AppPreferences sAppPreferences;
    private static IOTApplication instance;
    private static Context context;

    public static IOTApplication sharedInstance()
    {
        if (instance == null)
        {
            throw new NullPointerException(
                    "IOTApplication instance is null, please register in AndroidManifest.xml first");
        }
        return instance;
    }


   /* public static Context getContext(){
        return context;
    }*/


    @Override
    public void onCreate() {
        super.onCreate();

        // Load Shared Preference
        sAppPreferences = new AppPreferences(this);

        instance = this;
        context = getApplicationContext();
        initAsyn();
        initSyn();
        Stetho.initializeWithDefaults(this);

        // Register custom fonts like this (or also provide a font definition file)
        //Iconics.registerFont(new GoogleMaterial());
    }

    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }


    public static Context getContext()
    {
        return context;
    }



    public String getVersionName()
    {
        String version = "";
        try
        {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pi.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            version = "Not found version";
        }
        return version;
    }

    public int getVersionCode()
    {
        int code = 0;
        try
        {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            code = pi.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return code;
    }

    public String getEspRootSDPath()
    {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            path = Environment.getExternalStorageDirectory().toString() + "/Espressif/";
        }
        return path;
    }

    public String getContextFilesDirPath()
    {
        return getFilesDir().toString();
    }

    private String __formatString(int value)
    {
        String strValue = "";
        byte[] ary = __intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--)
        {
            strValue += (ary[i] & 0xFF);
            if (i > 0)
            {
                strValue += ".";
            }
        }
        return strValue;
    }

    private byte[] __intToByteArray(int value)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte)((value >>> offset) & 0xFF);
        }
        return b;
    }

    public String getGateway()
    {
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wm.getDhcpInfo();
        return __formatString(d.gateway);
    }

    private void initSyn()
    {
        // init db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, VijStrings.DB.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UserDBManager.init(daoSession);
        DeviceDBManager.init(daoSession);
           ApDBManager.init(daoSession);

    }

    private void initAsyn()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                InitLogger.init();
                CachedThreadPool.getInstance();
                WifiAdmin.getInstance();
                Mqtt.getInstance().connect();
            }
        }.start();
    }


}
