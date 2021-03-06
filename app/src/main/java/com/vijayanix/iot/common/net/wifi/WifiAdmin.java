package com.vijayanix.iot.common.net.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.base.object.ISingletonObject;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiAdmin implements IWifiAdmin, ISingletonObject
{
    private static final Logger log = Logger.getLogger(WifiAdmin.class);
    
    /**
     * Singleton lazy initialization start
     */
    private WifiAdmin()
    {
    }
    
    private static class InstanceHolder
    {
        static WifiAdmin instance = new WifiAdmin();
    }
    
    public static WifiAdmin getInstance()
    {
        return InstanceHolder.instance;
    }
    
    private WifiCipherType getWifiCipherType(WifiConfiguration wifiConfiguration)
    {
        String wifiConfigurationStr = wifiConfiguration.toString();
        boolean isWEP = wifiConfigurationStr.contains("SHARED");
        if (isWEP)
        {
            return WifiCipherType.WIFICIPHER_WEP;
        }
        boolean isWPA = wifiConfigurationStr.contains("WPA_PSK") || wifiConfigurationStr.contains("WPA2_PSK");
        if (isWPA)
        {
            return WifiCipherType.WIFICIPHER_WPA;
        }
        return WifiCipherType.WIFICIPHER_OPEN;
    }
    
    private NetworkInfo getWifiNetworkInfo()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext();
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        log.debug(Thread.currentThread().toString() + "##getWifiNetworkInfo(): " + mWiFiNetworkInfo);
        return mWiFiNetworkInfo;
    }
    
    // get the wifi info which is "connected" in wifi-setting
    private WifiInfo getConnectionInfo()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        log.debug(Thread.currentThread().toString() + "##getConnectionInfo(): " + wifiInfo);
        return wifiInfo;
    }
    
    // create wifi Configuration used to connect
    private WifiConfiguration createWifiConfirutaion(String ssid, WifiCipherType type, boolean isSsidHidden,
                                                     String password)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.hiddenSSID = isSsidHidden;
        switch (type)
        {
            case WIFICIPHER_OPEN:
                config.wepKeys[0] = "\"" + "\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFICIPHER_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (password.length() != 0)
                {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*"))
                    {
                        config.wepKeys[0] = password;
                    }
                    else
                    {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case WIFICIPHER_WPA:
                config.preSharedKey = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                // for WPA
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                // for WPA2
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                break;
            default:
                return null;
        }
        return config;
    }
    
    // get the existed wifi Configuration if it is exist in Android System
    private WifiConfiguration getExistWifiConfiguration(String ssid)
    {
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\"" + ssid + "\""))
            {
                log.debug(Thread.currentThread().toString() + "##getExistWifiConfiguration(ssid=[" + ssid + "]): "
                    + existingConfig);
                return existingConfig;
            }
        }
        log.debug(Thread.currentThread().toString() + "##getExistWifiConfiguration(ssid=[" + ssid + "]): null");
        return null;
    }
    
    @Override
    public boolean isWifiEnabled()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = mWifiManager.isWifiEnabled();
        log.info(Thread.currentThread().toString() + "##isWifiEnabled(): " + isWifiEnabled);
        return isWifiEnabled;
    }
    
    @Override
    public boolean isWifiAvailable()
    {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiAvailable = false;
        if (mWiFiNetworkInfo != null)
        {
            isWifiAvailable = mWiFiNetworkInfo.isAvailable();
        }
        log.info(Thread.currentThread().toString() + "##isWifiAvailable(): " + isWifiAvailable);
        return isWifiAvailable;
    }
    
    @Override
    public boolean isWifiConnected()
    {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null)
        {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        log.info(Thread.currentThread().toString() + "##isWifiConnected(): " + isWifiConnected);
        return isWifiConnected;
    }
    
    @Override
    public boolean isMobileAvailable()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext();
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = false;
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null)
        {
            isMobileAvailable = mMobileNetworkInfo.isAvailable();
        }
        log.info(Thread.currentThread().toString() + "##isMobileAvailable(): " + isMobileAvailable);
        return isMobileAvailable;
    }
    
    @Override
    public boolean isNetworkAvailable()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext();
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = false;
        if (mNetworkInfo != null)
        {
            isNetworkAvailable = mNetworkInfo.isAvailable();
        }
        log.info(Thread.currentThread().toString() + "##isNetworkAvailable(): " + isNetworkAvailable);
        return isNetworkAvailable;
    }
    
    @Override
    public boolean isWifiConnected(final String ssid)
    {
        WifiInfo mWifiInfo = getConnectionInfo();
        boolean isWifiConnected = false;
        if (mWifiInfo != null && isWifiConnected())
        {
            isWifiConnected = mWifiInfo.getSSID().equals("\"" + ssid + "\"") || mWifiInfo.getSSID().equals(ssid);
        }
        log.info(Thread.currentThread().toString() + "##isWifiConnected(String ssid): " + ssid + " " + isWifiConnected);
        return isWifiConnected;
    }
    
    @Override
    public boolean isConnectedOrConnecting()
    {
        NetworkInfo mNetworkInfo = getWifiNetworkInfo();
        boolean isConnectedOrConnecting = false;
        if (mNetworkInfo != null)
        {
            isConnectedOrConnecting = mNetworkInfo.isConnectedOrConnecting();
        }
        log.info(Thread.currentThread().toString() + "##isConnectedOrConnecting(): " + isConnectedOrConnecting);
        return isConnectedOrConnecting;
    }
    
    @Override
    public boolean setWifiEnabled()
    {
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();

        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean setWifiEnabled = mWifiManager.setWifiEnabled(true);
        log.info(Thread.currentThread().toString() + "##setWifiEnabled(): " + setWifiEnabled);
        return setWifiEnabled;
    }
    
    @Override
    public boolean enableConnected(String ssid)
    {
        if (isWifiConnected(ssid))
        {
            log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid
                + "]): is connected already,return true");
            return true;
        }
        boolean enableConnected = false;
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        // only use the exist wifi Configuration
        if (wifiConfiguration == null)
        {
            log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid
                + "]): wifi info don't exist in System,return false");
            return false;
        }
        else
        {
            Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
            WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            enableConnected = mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
            log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "]): " + enableConnected);
            return enableConnected;
        }
    }
    
    private final ArrayList<Thread> threads = new ArrayList<Thread>(4);
    
    private boolean isConnectSuc(String ssid)
        throws InterruptedException
    {
        synchronized (threads)
        {
            for (Thread thread : threads)
            {
                if (thread != Thread.currentThread())
                {
                    thread.interrupt();
                }
            }
            threads.clear();
            threads.trimToSize();
            threads.add(Thread.currentThread());
            log.debug(Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid + "]): Thread.interrupted()");
        }
        // the max timeout is 20 seconds
        long start = System.currentTimeMillis();
        long runtime;
        do
        {
            Thread.sleep(CONNECT_CHECK_INTERVAL);
            
            if (isWifiConnected(ssid))
            {
                log.info(Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid
                    + "]): isWifiConnected(ssid)=true, return true");
                return true;
            }
            runtime = System.currentTimeMillis() - start;
        } while (runtime < CONNECT_TIMEOUT);
        log.warn(Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid + "]): is timeout, return false");
        return false;
    }
    
    @Override
    public boolean connect(String ssid)
        throws InterruptedException
    {
        if (isWifiConnected(ssid))
        {
            log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid
                + "]): is connected already,return true");
            return true;
        }
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        // only use the exist wifi Configuration
        if (wifiConfiguration == null)
        {
            log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid
                + "]): wifi info don't exist in System,return false");
            return false;
        }
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        mWifiManager.enableNetwork(netId, true);
        boolean connect = isConnectSuc(ssid);
        log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "]): " + connect);
        return connect;
    }
    
    @Override
    public boolean enableConnected(String ssid, WifiCipherType type, String... password)
    {
        return enableConnected(ssid, type, false, password);
    }
    
    @Override
    public boolean connect(String ssid, WifiCipherType type, String... password)
        throws InterruptedException
    {
        return connect(ssid, type, false, password);
    }
    
    private void addScanResult(List<ScanResult> scanResultList, ScanResult scanResult)
    {
        String scanResultBssid = scanResult.BSSID;
        boolean isExist = false;
        for (ScanResult scanResultInList : scanResultList)
        {
            if (scanResultInList.BSSID.equals(scanResultBssid))
            {
                isExist = true;
                break;
            }
        }
        if (!isExist)
        {
            scanResultList.add(scanResult);
        }
    }
        
    @Override
    public List<ScanResult> scan()
    {
        Context context = IOTApplication.getContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
        List<ScanResult> wifiList = null;
        long start = System.currentTimeMillis();
        long runtime;
        do
        {
            try
            {
                Thread.sleep(SCAN_CHECK_INTERVAL);
            }
            catch (InterruptedException e)
            {
                log.warn(Thread.currentThread().toString() + "##scan(): is interrupted, return empty list");
                return Collections.emptyList();
            }
            
            wifiList = mWifiManager.getScanResults();
            if (wifiList != null)
            {
                List<ScanResult> result = new ArrayList<ScanResult>();
                for (ScanResult scan : wifiList)
                {
                    if (!TextUtils.isEmpty(scan.SSID))
                    {
                        addScanResult(result, scan);
                    }
                }
                log.info(Thread.currentThread().toString() + "##scan(): " + result);
                return result;
            }
            
            runtime = System.currentTimeMillis() - start;
        } while (runtime < SCAN_TIMEOUT);
        
        log.warn(Thread.currentThread().toString() + "##scan(): fail, return empty list");
        // if fail, don't return null
        return Collections.emptyList();
    }
    
    @Override
    public String getWifiConnectedSsid()
    {
        WifiInfo mWifiInfo = getConnectionInfo();
        String ssid = null;
        if (mWifiInfo != null && mWifiInfo.getSSID() != null && isWifiConnected())
        {
            int len = mWifiInfo.getSSID().length();
            // mWifiInfo.getBSSID() = "\"" + ssid + "\"";
            if (mWifiInfo.getSSID().startsWith("\"") && mWifiInfo.getSSID().endsWith("\""))
            {
                ssid = mWifiInfo.getSSID().substring(1, len - 1);
            }
            else
            {
                ssid = mWifiInfo.getSSID();
            }
            
        }
        log.info(Thread.currentThread().toString() + "##getWifiConnectedSsid(): " + ssid);
        return ssid;
    }
    
    @Override
    public boolean enableConnected(String ssid, WifiCipherType type, boolean isSsidHidden, String... password)
    {
        if (isWifiConnected(ssid))
        {
            log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): is connected already,return true");
            return true;
        }
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        // only use the paraters,and remove the exist wifi Configuration
        if (wifiConfiguration != null)
        {
            log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): removeNetwork already in Android System");
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
        }
        int len = password.length;
        if (len > 0 && type != WifiCipherType.WIFICIPHER_OPEN)
        {
            wifiConfiguration = createWifiConfirutaion(ssid, type, isSsidHidden, password[0]);
        }
        else
        {
            wifiConfiguration = createWifiConfirutaion(ssid, type, isSsidHidden, null);
        }
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        boolean enableConnected = mWifiManager.enableNetwork(netId, true);
        log.info(Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
            + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0]) + "]): "
            + enableConnected);
        return enableConnected;
    }
    
    @Override
    public boolean connect(String ssid, WifiCipherType type, boolean isSsidHidden, String... password)
        throws InterruptedException
    {
        if (isWifiConnected(ssid))
        {
            log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): is connected already,return true");
            return true;
        }
        // delete wifi Configuration when the WifiCipherType changed
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        if (wifiConfiguration != null && getWifiCipherType(wifiConfiguration) != type)
        {
            Context context = IOTApplication.sharedInstance().getBaseContext().getApplicationContext();
            WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
            log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): delete the wifiConfiguration changed cipher type");
        }
        // try the exist wifi Configuration firstly
        if (connect(ssid))
        {
            log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): connect(ssid)=true,return true");
            return true;
        }
        boolean enableConnected = enableConnected(ssid, type, isSsidHidden, password);
        if (!enableConnected)
        {
            log.warn(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                + "]): enableConnected(ssid, type, password)=false,return false");
            return false;
        }
        boolean connect = isConnectSuc(ssid);
        log.info(Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type + "],isSsidHidden=["
            + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0]) + "]): " + connect);
        return connect;
    }

    public String getMacAddress(){
        String marshmallowMacAddress = "02:00:00:00:00:00";
        Context context = IOTApplication.getContext().getApplicationContext();
            WifiManager wifiMan = (WifiManager)context.getSystemService(Context.WIFI_SERVICE) ;
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            if(wifiInf !=null && marshmallowMacAddress.equals(wifiInf.getMacAddress())){
                String result = null;
                try {
                    result= getAdressMacByInterface();
                    if (result != null){
                        return result;
                    } else {
                        result = getAddressMacByFile(wifiMan);
                        return result;
                    }
                } catch (IOException e) {
                    Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
                } catch (Exception e) {
                    Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
                }
            } else{
                if (wifiInf != null && wifiInf.getMacAddress() != null) {
                    return wifiInf.getMacAddress();
                } else {
                    return "";
                }
            }
            return marshmallowMacAddress;
        }




    private static String getAdressMacByInterface(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:",b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }


    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {

         String fileAddressMac = "/sys/class/net/wlan0/address";
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }


}
