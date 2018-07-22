package com.vijayanix.iot.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vijayanix.iot.R;

public class AppPreferences {
    private static final String BEEN_LOGIN = "hasBeenLogin";
    private static final String CLIENT_ID = "clientId";
    private static final String AUTH_INFO = "authInfo";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String KeyPrimaryColor = "prefPrimaryColor";
    public static final String KeyFABColor = "prefFABColor";
    public static final String KeyFABShow = "prefFABShow";
    public static final String KeyNavigationBlack = "prefNavigationBlack";
    public static final String KeyCustomFilename = "prefCustomFilename";
    public static final String KeySortMode = "prefSortMode";
    public static final String KeyIsRooted = "prefIsRooted";
    public static final String KeyCustomPath = "prefCustomPath";

    // List
    public static final String KeyFavoriteApps = "prefFavoriteApps";
    public static final String KeyHiddenApps = "prefHiddenApps";

    public AppPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.context = context;
    }

    public int getRootStatus() {
        return sharedPreferences.getInt(KeyIsRooted, 0);
    }

    public void setRootStatus(int rootStatus) {
        editor.putInt(KeyIsRooted, rootStatus);
        editor.commit();
    }

    public int getPrimaryColorPref() {
        return sharedPreferences.getInt(KeyPrimaryColor, context.getResources().getColor(R.color.primary));
    }
    public void setPrimaryColorPref(Integer res) {
        editor.putInt(KeyPrimaryColor, res);
        editor.commit();
    }

    public int getFABColorPref() {
        return sharedPreferences.getInt(KeyFABColor, context.getResources().getColor(R.color.fab));
    }
    public void setFABColorPref(Integer res) {
        editor.putInt(KeyFABColor, res);
        editor.commit();
    }

    public Boolean getNavigationBlackPref() {
        return sharedPreferences.getBoolean(KeyNavigationBlack, false);
    }
    public void setNavigationBlackPref(Boolean res) {
        editor.putBoolean(KeyNavigationBlack, res);
        editor.commit();
    }

    public Boolean getFABShowPref() {
        return sharedPreferences.getBoolean(KeyFABShow, false);
    }
    public void setFABShowPref(Boolean res) {
        editor.putBoolean(KeyFABShow, res);
        editor.commit();
    }



    public void setBeenLogin(boolean hasBeenLogin){

        editor.putBoolean(BEEN_LOGIN,hasBeenLogin).commit();
    }

    public boolean getHasBeenLogin(){
        return sharedPreferences.getBoolean(BEEN_LOGIN,false);
    }

	public  String getClientId() {
		return sharedPreferences.getString(CLIENT_ID,null);

	}


    public  String getAuthInfo() {
        return sharedPreferences.getString(AUTH_INFO,null);

    }

	public void saveMqttClient(String clientId,String authInfo){
		editor.putString(CLIENT_ID, clientId);
        editor.putString(AUTH_INFO,authInfo);
		editor.commit();
	}
}
