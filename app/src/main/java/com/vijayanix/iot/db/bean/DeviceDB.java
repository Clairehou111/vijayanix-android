package com.vijayanix.iot.db.bean;


import com.vijayanix.iot.db.interfa.IDeviceDB;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DeviceDB implements IDeviceDB
{
    @Id
    private long id;
    @NotNull
    private String key;
    @NotNull
    private String bssid;
    private int type;
    private int state;
    private boolean isOwner;
    @NotNull
    private String name;
    private String rom_version;
    private String latest_rom_version;
    private long timestamp;
    private long activatedTime;
    private long userId;


    @Generated(hash = 999307566)
    public DeviceDB(long id, @NotNull String key, @NotNull String bssid, int type,
            int state, boolean isOwner, @NotNull String name, String rom_version,
            String latest_rom_version, long timestamp, long activatedTime,
            long userId) {
        this.id = id;
        this.key = key;
        this.bssid = bssid;
        this.type = type;
        this.state = state;
        this.isOwner = isOwner;
        this.name = name;
        this.rom_version = rom_version;
        this.latest_rom_version = latest_rom_version;
        this.timestamp = timestamp;
        this.activatedTime = activatedTime;
        this.userId = userId;
    }
    @Generated(hash = 1363222787)
    public DeviceDB() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getBssid() {
        return this.bssid;
    }
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public boolean getIsOwner() {
        return this.isOwner;
    }
    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRom_version() {
        return this.rom_version;
    }
    public void setRom_version(String rom_version) {
        this.rom_version = rom_version;
    }
    public String getLatest_rom_version() {
        return this.latest_rom_version;
    }
    public void setLatest_rom_version(String latest_rom_version) {
        this.latest_rom_version = latest_rom_version;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getActivatedTime() {
        return this.activatedTime;
    }
    public void setActivatedTime(long activatedTime) {
        this.activatedTime = activatedTime;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
