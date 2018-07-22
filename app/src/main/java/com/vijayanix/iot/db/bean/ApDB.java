package com.vijayanix.iot.db.bean;


import com.vijayanix.iot.db.interfa.IApDB;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ApDB implements IApDB{

	@Id(autoincrement = true)
    private Long id;
    @NotNull
    private String bssid;
	@NotNull
    private String ssid;
	@NotNull
    private String password;
    private boolean isLastSelected;
    private int configuredFailedCount;
	@NotNull
    private String deviceBssids;
    @Generated(hash = 1175164124)
    public ApDB(Long id, @NotNull String bssid, @NotNull String ssid,
            @NotNull String password, boolean isLastSelected,
            int configuredFailedCount, @NotNull String deviceBssids) {
        this.id = id;
        this.bssid = bssid;
        this.ssid = ssid;
        this.password = password;
        this.isLastSelected = isLastSelected;
        this.configuredFailedCount = configuredFailedCount;
        this.deviceBssids = deviceBssids;
    }
    @Generated(hash = 1512250716)
    public ApDB() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBssid() {
        return this.bssid;
    }
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
    public String getSsid() {
        return this.ssid;
    }
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean getIsLastSelected() {
        return this.isLastSelected;
    }
    public void setIsLastSelected(boolean isLastSelected) {
        this.isLastSelected = isLastSelected;
    }
    public int getConfiguredFailedCount() {
        return this.configuredFailedCount;
    }
    public void setConfiguredFailedCount(int configuredFailedCount) {
        this.configuredFailedCount = configuredFailedCount;
    }
    public String getDeviceBssids() {
        return this.deviceBssids;
    }
    public void setDeviceBssids(String deviceBssids) {
        this.deviceBssids = deviceBssids;
    }


}
