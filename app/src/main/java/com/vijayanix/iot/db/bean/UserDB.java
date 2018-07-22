package com.vijayanix.iot.db.bean;

import com.vijayanix.iot.db.interfa.IUserDB;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import com.vijayanix.iot.db.greendao.gen.DaoSession;
import com.vijayanix.iot.db.greendao.gen.DeviceDBDao;
import com.vijayanix.iot.db.greendao.gen.UserDBDao;

@Entity
public class UserDB implements IUserDB{
    @Id
    private long id;
    @NotNull
    private String email;
    @NotNull
    private String key;
    @NotNull
    private String name;
    private boolean isLastLogin;

	@ToMany(referencedJoinProperty = "userId")
    private List<DeviceDB> devices;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1450670558)
    private transient UserDBDao myDao;

    @Generated(hash = 608947284)
    public UserDB(long id, @NotNull String email, @NotNull String key,
            @NotNull String name, boolean isLastLogin) {
        this.id = id;
        this.email = email;
        this.key = key;
        this.name = name;
        this.isLastLogin = isLastLogin;
    }
    @Generated(hash = 1312299826)
    public UserDB() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getIsLastLogin() {
        return this.isLastLogin;
    }
    public void setIsLastLogin(boolean isLastLogin) {
        this.isLastLogin = isLastLogin;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1875627107)
    public List<DeviceDB> getDevices() {
        if (devices == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DeviceDBDao targetDao = daoSession.getDeviceDBDao();
            List<DeviceDB> devicesNew = targetDao._queryUserDB_Devices(id);
            synchronized (this) {
                if (devices == null) {
                    devices = devicesNew;
                }
            }
        }
        return devices;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1428662284)
    public synchronized void resetDevices() {
        devices = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1638678461)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDBDao() : null;
    }


}
