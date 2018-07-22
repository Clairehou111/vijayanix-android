package com.vijayanix.iot.model.user.builder;


import com.vijayanix.iot.model.user.User;
import com.vijayanix.iot.model.user.IBUser;
import com.vijayanix.iot.model.user.IUser;

public class BUser implements IBUser
{
    private static final long GUEST_USER_ID = -1;
    
    private static final String GUEST_USER_KEY = "guest";
    
    private static final String GUEST_USER_EMAIL = "guest";
    
    private static final String GUEST_USER_NAME = "guest";

    private static final long FIXED_USER_ID = Integer.MAX_VALUE;

    private static final String FIXED_USER_KEY = "bravo";

    private static final String FIXED_USER_EMAIL = "bravo@gamil.com";

    private static final String FIXED_USER_NAME = "bravo";

    /*
     * Singleton lazy initialization start
     */
    private IUser instanceSingleton = null;
    
    private BUser()
    {
        instanceSingleton = new User();
    }
    
    private static class InstanceHolder
    {
        static BUser instanceBuilder = new BUser();
    }
    
    public static BUser getBuilder()
    {
        return InstanceHolder.instanceBuilder;
    }
    
    @Override
    public IUser getInstance()
    {
        return instanceSingleton;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    @Override
    public IUser loadUser()
    {   //claire comment this
      /*  IUserDB userDB = UserDBManager.getInstance().load();
        if (userDB != null)
        {
            instanceSingleton.setUserId(userDB.getId());
            instanceSingleton.setUserKey(userDB.getKey());
            instanceSingleton.setUserEmail(userDB.getEmail());
            instanceSingleton.setUserName(userDB.getName());
        }
        else
        {
            instanceSingleton.setUserId(GUEST_USER_ID);
            instanceSingleton.setUserKey(GUEST_USER_KEY);
            instanceSingleton.setUserEmail(GUEST_USER_EMAIL);
            instanceSingleton.setUserName(GUEST_USER_NAME);
        }*/


	    instanceSingleton.setUserId(FIXED_USER_ID);
	    instanceSingleton.setUserKey(FIXED_USER_KEY);
	    instanceSingleton.setUserEmail(FIXED_USER_EMAIL);
	    instanceSingleton.setUserName(FIXED_USER_NAME);
        return instanceSingleton;
    }
}
