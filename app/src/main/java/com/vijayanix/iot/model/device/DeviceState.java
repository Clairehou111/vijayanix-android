package com.vijayanix.iot.model.device;


public class DeviceState implements IDeviceState, Cloneable
{
    
    /**
     * although these variables are final, but their mState could be changed. but please don't change them. or the bugs
     * will be produced
     */
    public static final IDeviceState NEW = new DeviceState(1 << IDeviceState.Enum.NEW.ordinal());
    
    public static final IDeviceState LOCAL = new DeviceState(1 << IDeviceState.Enum.LOCAL.ordinal());
    
    public static final IDeviceState INTERNET = new DeviceState(1 << IDeviceState.Enum.INTERNET.ordinal());
    
    public static final IDeviceState OFFLINE = new DeviceState(1 << IDeviceState.Enum.OFFLINE.ordinal());
    
    public static final IDeviceState CONFIGURING = new DeviceState(
        1 << IDeviceState.Enum.CONFIGURING.ordinal());
    
    public static final IDeviceState UPGRADING_LOCAL = new DeviceState(
        1 << IDeviceState.Enum.UPGRADING_LOCAL.ordinal());
    
    public static final IDeviceState UPGRADING_INTERNET = new DeviceState(
        1 << IDeviceState.Enum.UPGRADING_INTERNET.ordinal());
    
    public static final IDeviceState ACTIVATING = new DeviceState(1 << IDeviceState.Enum.ACTIVATING.ordinal());
    
    public static final IDeviceState DELETED = new DeviceState(1 << IDeviceState.Enum.DELETED.ordinal());
    
    public static final IDeviceState RENAMED = new DeviceState(1 << IDeviceState.Enum.RENAMED.ordinal());
    
    public static final IDeviceState CLEAR = new DeviceState(0);
    
    
    // check whether the state which should be final is final
    private void __checkvalid()
    {
        if (this == NEW)
        {
            throw new IllegalArgumentException(
                "DeviceState.NEW can't be changed or the statemachine will make supprise");
        }
        else if (this == LOCAL)
        {
            throw new IllegalArgumentException(
                "DeviceState.LOCAL can't be changed or the statemachine will make supprise");
        }
        else if (this == INTERNET)
        {
            throw new IllegalArgumentException(
                "DeviceState.INTERNET can't be changed or the statemachine will make supprise");
        }
        else if (this == OFFLINE)
        {
            throw new IllegalArgumentException(
                "DeviceState.OFFLINE can't be changed or the statemachine will make supprise");
        }
        else if (this == CONFIGURING)
        {
            throw new IllegalArgumentException(
                "DeviceState.CONFIGURING can't be changed or the statemachine will make supprise");
        }
        else if (this == UPGRADING_LOCAL)
        {
            throw new IllegalArgumentException(
                "DeviceState.UPGRADING_LOCAL can't be changed or the statemachine will make supprise");
        }
        else if (this == UPGRADING_INTERNET)
        {
            throw new IllegalArgumentException(
                "DeviceState.UPGRADING_INTERNET can't be changed or the statemachine will make supprise");
        }
        else if (this == ACTIVATING)
        {
            throw new IllegalArgumentException(
                "DeviceState.ACTIVATING can't be changed or the statemachine will make supprise");
        }
        else if (this == DELETED)
        {
            throw new IllegalArgumentException(
                "DeviceState.DELETED can't be changed or the statemachine will make supprise");
        }
        else if (this == RENAMED)
        {
            throw new IllegalArgumentException(
                "DeviceState.RENAMED can't be changed or the statemachine will make supprise");
        }
        else if (this == CLEAR)
        {
            throw new IllegalArgumentException(
                "DeviceState.CLEAR can't be changed or the statemachine will make supprise");
        }
    }
    
    /**
     * 
     * @param state
     * @param permittedStates permitted States, only pure state of {@link #NEW},{@link #LOCAL} ,{@link #INTERNET},
     *            {@link #OFFLINE},{@link #CONFIGURING},{@link #UPGRADING_LOCAL},{@link #UPGRADING_INTERNET},
     *            {@link #ACTIVATING},{@link #DELETED},{@link #RENAMED},{@link #CLEAR}
     * @return whether the state is valid
     */
    public static boolean checkValidWithPermittedStates(IDeviceState state, IDeviceState... permittedStates)
    {
        int len = permittedStates.length;
        if (len == 0)
        {
            throw new NullPointerException("checkValidWithPermittedStates() permittedStates shouldn't be length of 0");
        }
        DeviceState deviceState = (DeviceState)state;
        IDeviceState.Enum[] stateEnums = IDeviceState.Enum.values();
        boolean isPermitted = false;
        // for each all of states
        for (int i = 0; i < stateEnums.length; i++)
        {
            IDeviceState.Enum stateEnum = stateEnums[i];
            // check whether the state is permitted
            if (deviceState.isStateXXX(stateEnum))
            {
                isPermitted = false;
                for (int j = 0; j < len; j++)
                {
                    DeviceState permittedState = (DeviceState)permittedStates[j];
                    if (permittedState.isStateXXX(stateEnum))
                    {
                        isPermitted = true;
                        break;
                    }
                }
                // the state isn't permitted
                if (!isPermitted)
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 
     * @param state
     * @param forbiddenStates forbidden States, only pure state of {@link #NEW},{@link #LOCAL} ,{@link #INTERNET},
     *            {@link #OFFLINE},{@link #CONFIGURING},{@link #UPGRADING_LOCAL},{@link #UPGRADING_INTERNET},
     *            {@link #ACTIVATING},{@link #DELETED},{@link #RENAMED},{@link #CLEAR}
     * @return whether the state is valid
     */
    public static boolean checkValidWithForbiddenStates(IDeviceState state, IDeviceState... forbiddenStates)
    {
        int len = forbiddenStates.length;
        if (len == 0)
        {
            throw new NullPointerException("checkValidWithForbiddenStates() forbiddenStates shouldn't be length of 0");
        }
        DeviceState deviceState = (DeviceState)state;
        // for each forbidden states
        for (int i = 0; i < len; i++)
        {
            DeviceState forbiddenState = (DeviceState)forbiddenStates[i];
            // state is forbidden
            if (deviceState.isStateXXX(forbiddenState.getDeviceState()))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param state
     * @param necessaryStates necessary States, only pure state of {@link #NEW},{@link #LOCAL} ,{@link #INTERNET},
     *            {@link #OFFLINE},{@link #CONFIGURING},{@link #UPGRADING_LOCAL},{@link #UPGRADING_INTERNET},
     *            {@link #ACTIVATING},{@link #DELETED},{@link #RENAMED},{@link #CLEAR}
     * @return whether the state is valid
     */
    public static boolean checkValidWithNecessaryStates(IDeviceState state, IDeviceState... necessaryStates)
    {
        int len = necessaryStates.length;
        if (len == 0)
        {
            throw new NullPointerException("checkValidWithNecessaryStates() necessaryStates shouldn't be length of 0");
        }
        boolean isContained = false;
        DeviceState deviceState = (DeviceState)state;
        // for each necessary states
        for (int i = 0; i < len; i++)
        {
            DeviceState necessaryState = (DeviceState)necessaryStates[i];
            // check whether the state is contained
            isContained = deviceState.isStateXXX(necessaryState.getDeviceState());
            // the state isn't permitted
            if (!isContained)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param state
     * @param specificStates specific States, only pure state of {@link #NEW},{@link #LOCAL} ,{@link #INTERNET},
     *            {@link #OFFLINE},{@link #CONFIGURING},{@link #UPGRADING_LOCAL},{@link #UPGRADING_INTERNET},
     *            {@link #ACTIVATING},{@link #DELETED},{@link #RENAMED},{@link #CLEAR}
     * @return whether the state is valid
     */
    public static boolean checkValidWithSpecificStates(IDeviceState state, IDeviceState... specificStates)
    {
        return checkValidWithNecessaryStates(state, specificStates)
            && checkValidWithPermittedStates(state, specificStates);
    }
    
    private int mState = 0;
    
    public DeviceState()
    {
        
    }
    
    public DeviceState(int state)
    {
        this.mState = state;
    }
    
    private void addStateXXX(IDeviceState.Enum stateEnum)
    {
        __checkvalid();
        this.mState |= (1 << stateEnum.ordinal());
    }
    
    private void clearStateXXX(IDeviceState.Enum stateEnum)
    {
        __checkvalid();
        this.mState &= (~(1 << stateEnum.ordinal()));
    }
    
    private boolean isStateXXX(IDeviceState.Enum stateEnum)
    {
        return (this.mState & (1 << stateEnum.ordinal())) != 0;
    }
    
    @Override
    public int getStateValue()
    {
        return mState;
    }
    
    @Override
    public void setStateValue(int state)
    {
        __checkvalid();
        this.mState = state;
    }
    
    @Override
    public void addStateNew()
    {
        addStateXXX(IDeviceState.Enum.NEW);
    }
    
    @Override
    public void clearStateNew()
    {
        clearStateXXX(IDeviceState.Enum.NEW);
    }
    
    @Override
    public boolean isStateNew()
    {
        return isStateXXX(IDeviceState.Enum.NEW);
    }
    
    @Override
    public void addStateLocal()
    {
        addStateXXX(IDeviceState.Enum.LOCAL);
    }
    
    @Override
    public void clearStateLocal()
    {
        clearStateXXX(IDeviceState.Enum.LOCAL);
    }
    
    @Override
    public boolean isStateLocal()
    {
        return isStateXXX(IDeviceState.Enum.LOCAL);
    }
    
    @Override
    public void addStateInternet()
    {
        addStateXXX(IDeviceState.Enum.INTERNET);
    }
    
    @Override
    public void clearStateInternet()
    {
        clearStateXXX(IDeviceState.Enum.INTERNET);
    }
    
    @Override
    public boolean isStateInternet()
    {
        return isStateXXX(IDeviceState.Enum.INTERNET);
    }
    
    @Override
    public void addStateOffline()
    {
        addStateXXX(IDeviceState.Enum.OFFLINE);
    }
    
    @Override
    public void clearStateOffline()
    {
        clearStateXXX(IDeviceState.Enum.OFFLINE);
    }
    
    @Override
    public boolean isStateOffline()
    {
        return isStateXXX(IDeviceState.Enum.OFFLINE);
    }
    
    @Override
    public void addStateConfiguring()
    {
        addStateXXX(IDeviceState.Enum.CONFIGURING);
    }
    
    @Override
    public void clearStateConfiguring()
    {
        clearStateXXX(IDeviceState.Enum.CONFIGURING);
    }
    
    @Override
    public boolean isStateConfiguring()
    {
        return isStateXXX(IDeviceState.Enum.CONFIGURING);
    }
    
    @Override
    public void addStateActivating()
    {
        addStateXXX(IDeviceState.Enum.ACTIVATING);
    }
    
    @Override
    public void clearStateActivating()
    {
        clearStateXXX(IDeviceState.Enum.ACTIVATING);
    }
    
    @Override
    public boolean isStateActivating()
    {
        return isStateXXX(IDeviceState.Enum.ACTIVATING);
    }
    
    @Override
    public void addStateUpgradingLocal()
    {
        addStateXXX(IDeviceState.Enum.UPGRADING_LOCAL);
    }
    
    @Override
    public void clearStateUpgradingLocal()
    {
        clearStateXXX(IDeviceState.Enum.UPGRADING_LOCAL);
    }
    
    @Override
    public boolean isStateUpgradingLocal()
    {
        return isStateXXX(IDeviceState.Enum.UPGRADING_LOCAL);
    }
    
    @Override
    public void addStateUpgradingInternet()
    {
        addStateXXX(IDeviceState.Enum.UPGRADING_INTERNET);
    }
    
    @Override
    public void clearStateUpgradingInternet()
    {
        clearStateXXX(IDeviceState.Enum.UPGRADING_INTERNET);
    }
    
    @Override
    public boolean isStateUpgradingInternet()
    {
        return isStateXXX(IDeviceState.Enum.UPGRADING_INTERNET);
    }
    
    @Override
    public void addStateDeleted()
    {
        addStateXXX(IDeviceState.Enum.DELETED);
    }
    
    @Override
    public void clearStateDeleted()
    {
        clearStateXXX(IDeviceState.Enum.DELETED);
    }
    
    @Override
    public boolean isStateDeleted()
    {
        return isStateXXX(IDeviceState.Enum.DELETED);
    }
    
    @Override
    public void addStateRenamed()
    {
        addStateXXX(IDeviceState.Enum.RENAMED);
        
    }
    
    @Override
    public void clearStateRenamed()
    {
        clearStateXXX(IDeviceState.Enum.RENAMED);
    }
    
    @Override
    public boolean isStateRenamed()
    {
        return isStateXXX(IDeviceState.Enum.RENAMED);
    }
    
    @Override
    public void clearState()
    {
        this.mState = 0;
    }
    
    @Override
    public boolean isStateClear()
    {
        return this.mState == 0;
    }
    
    @Override
    public IDeviceState.Enum getDeviceState()
    {
        if (this.isStateUpgradingLocal())
        {
            return IDeviceState.Enum.UPGRADING_LOCAL;
        }
        else if (this.isStateUpgradingInternet())
        {
            return IDeviceState.Enum.UPGRADING_INTERNET;
        }
        else if (this.isStateOffline())
        {
            return IDeviceState.Enum.OFFLINE;
        }
        else if (this.isStateNew())
        {
            return IDeviceState.Enum.NEW;
        }
        // LOCAL must be front of INTERNET
        // for the UI display priority to Local
        else if (this.isStateLocal())
        {
            return IDeviceState.Enum.LOCAL;
        }
        else if (this.isStateInternet())
        {
            return IDeviceState.Enum.INTERNET;
        }
        else if (this.isStateDeleted())
        {
            return IDeviceState.Enum.DELETED;
        }
        else if (this.isStateConfiguring())
        {
            return IDeviceState.Enum.CONFIGURING;
        }
        else if (this.isStateActivating())
        {
            return IDeviceState.Enum.ACTIVATING;
        }
        // RENAMED and CLEAR should be in the end
        else if (this.isStateRenamed())
        {
            return IDeviceState.Enum.RENAMED;
        }
        else if (this.isStateClear())
        {
            return IDeviceState.Enum.CLEAR;
        }
        return null;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DeviceState) || o == null)
        {
            return false;
        }
        DeviceState other = (DeviceState)o;
        return this.mState == other.mState;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (this.isStateUpgradingLocal())
        {
            sb.append(IDeviceState.Enum.UPGRADING_LOCAL + ",");
        }
        if (this.isStateUpgradingInternet())
        {
            sb.append(IDeviceState.Enum.UPGRADING_INTERNET + ",");
        }
        if (this.isStateOffline())
        {
            sb.append(IDeviceState.Enum.OFFLINE + ",");
        }
        if (this.isStateRenamed())
        {
            sb.append(IDeviceState.Enum.RENAMED + ",");
        }
        if (this.isStateNew())
        {
            sb.append(IDeviceState.Enum.NEW + ",");
        }
        if (this.isStateLocal())
        {
            sb.append(IDeviceState.Enum.LOCAL + ",");
        }
        if (this.isStateInternet())
        {
            sb.append(IDeviceState.Enum.INTERNET + ",");
        }
        if (this.isStateDeleted())
        {
            sb.append(IDeviceState.Enum.DELETED + ",");
        }
        if (this.isStateConfiguring())
        {
            sb.append(IDeviceState.Enum.CONFIGURING + ",");
        }
        if (this.isStateClear())
        {
            sb.append(IDeviceState.Enum.CLEAR + ",");
        }
        if (this.isStateActivating())
        {
            sb.append(IDeviceState.Enum.ACTIVATING + ",");
        }
        return "EspStateDeviceState=[" + sb.substring(0, sb.length() - 1) + "]";
    }
    
    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
    
}
