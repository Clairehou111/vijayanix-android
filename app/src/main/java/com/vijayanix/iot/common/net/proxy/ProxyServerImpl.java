package com.vijayanix.iot.common.net.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProxyServerImpl implements ProxyServer
{
    private static final boolean DEBUG = true;
    private static final boolean USE_LOG4J = true;
    private static final Class<?> CLASS = ProxyServerImpl.class;
    private static final int PROXY_SERVER_PORT_MIN = 10000;
    private static final int PROXY_SERVER_PORT_MAX = 65535;
    
    private volatile boolean mIsStarted = false;
    private volatile int mLocalPort = -1;
    
    private volatile ServerSocket mServerSocket = null;
    
    private final List<ProxyTask> mNewTaskArrayList;
    
    private volatile OfferTaskThread mOfferTaskThread;
    
    private volatile AcceptTaskThread mAcceptTaskThread;
    
    /*
     * Singleton lazy initialization start
     */
    private ProxyServerImpl()
    {
        mNewTaskArrayList = new ArrayList<ProxyTask>();
    }
    
    private static class InstanceHolder
    {
        static ProxyServerImpl instance = new ProxyServerImpl();
    }
    
    public static ProxyServerImpl getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    private void openServer()
    {
        Random random = new Random();
        while (true)
        {
            try
            {
                int port = random.nextInt(PROXY_SERVER_PORT_MAX - PROXY_SERVER_PORT_MIN) + PROXY_SERVER_PORT_MIN;
                mServerSocket = new ServerSocket(port);
                mLocalPort = mServerSocket.getLocalPort();
                break;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void closeServer()
    {
        try
        {
            mServerSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public synchronized void start()
    {
        MeshLog.d(DEBUG, USE_LOG4J, CLASS, "ProxyServerImpl start() entrance");
        
        if (mIsStarted)
        {
            stop();
        }
        
        // Start EspMeshHelper
        MeshSocketManager.getInstance().start();
        
        // Open server
        openServer();
        
        // Start accept task thread
        mAcceptTaskThread = new AcceptTaskThread();
        mAcceptTaskThread.startThread();
        
        // Start offer proxy task thread
        mOfferTaskThread = new OfferTaskThread();
        mOfferTaskThread.startThread();
        
        mIsStarted = true;
    }
    
    @Override
    public synchronized void stop()
    {
        MeshLog.d(DEBUG, USE_LOG4J, CLASS, "ProxyServerImpl stop() entrance");
        
        mIsStarted = false;
        
        // Close offer proxy task thread
        if (mOfferTaskThread != null)
        {
            mOfferTaskThread.stopThread();
            mOfferTaskThread = null;
        }
        mNewTaskArrayList.clear();
        
        // Close server
        closeServer();
        // Close accept task thread
        if (mAcceptTaskThread != null)
        {
            mAcceptTaskThread.stopThread();
            mAcceptTaskThread = null;
        }
        
        // Stop EspMeshHelper
        MeshSocketManager.getInstance().stop();
    }
    
    @Override
    public synchronized int getEspProxyServerPort()
    {
        if (!mIsStarted)
        {
            throw new IllegalStateException("getEspProxyServerPort() should be called after start()");
        }
        return mLocalPort;
    }
    
    private class AcceptTaskThread extends BlockingFinishThread
    {
        private final Class<?> CLASS = AcceptTaskThread.class;
        
        private ProxyTask accept(VijSocket socket)
        {
            return ProxyTaskFactory.createProxyTask(socket);
        }
        
        @Override
        void startThreadsInit()
        {
        }

        @Override
        void endThreadsDestroy()
        {
        }

        @Override
        public void execute()
        {
            while (mIsStart)
            {
                try
                {
                    final Socket socket = mServerSocket.accept();
                    final VijSocket vijSocket = VijSocket.createEspSocket(socket);
                    final ProxyTask proxyTask = accept(vijSocket);
                    synchronized (mNewTaskArrayList)
                    {
                        mNewTaskArrayList.add(proxyTask);
                        mNewTaskArrayList.notify();
                    }
                }
                catch (IOException e)
                {
                    MeshLog.e(DEBUG, USE_LOG4J, CLASS, "execute() mServerSocket.accept() IOException, break");
                    break;
                }
            }
        }
        
    }
    
    private class OfferTaskThread extends BlockingFinishThread
    {
        private final Class<?> CLASS = OfferTaskThread.class;
        
        private List<ProxyTask> mAddedTaskList;
        
        private boolean mIsInterrupted = false;
        
        OfferTaskThread()
        {
            mAddedTaskList = new ArrayList<ProxyTask>();
        }
        
        @Override
        void startThreadsInit()
        {
        }
        
        @Override
        void endThreadsDestroy()
        {
            mAddedTaskList.clear();
            mIsInterrupted = true;
            interrupt();
        }

        private void offer(ProxyTask task)
        {
            MeshSocketManager.getInstance().accept(task);
        }
        
        @Override
        public void execute()
        {
            while (mIsStart)
            {
                synchronized (mNewTaskArrayList)
                {
                    for (int i = 0; i < mNewTaskArrayList.size() && mIsStart; i++)
                    {
                        ProxyTask newTask = mNewTaskArrayList.get(i);
                        if (!isTargetBssidUsing(newTask.getTargetBssid()))
                        {
                            mNewTaskArrayList.remove(i--);
                            offer(newTask);
                            mAddedTaskList.add(newTask);
                        }
                    }
                }
                
                if (!mIsStart)
                {
                    MeshLog.d(DEBUG, USE_LOG4J, CLASS, "OfferTaskThread mRun is false");
                    break;
                }
                
                try
                {
                    boolean blockingAwaked = false;
                    synchronized (mNewTaskArrayList)
                    {
                        if (mNewTaskArrayList.isEmpty())
                        {
                            blockingAwaked = true;
                            MeshLog.d(DEBUG, USE_LOG4J, CLASS, "NewTaskList is empty, wait new task add");
                            mNewTaskArrayList.wait();
                        }
                    }
                    if (!blockingAwaked)
                    {
                        MeshLog.i(DEBUG,
                            USE_LOG4J,
                            CLASS,
                            "NewTaskList is not empty, sleep 100 millisecond and run again");
                        Thread.sleep(100);
                    }
                }
                catch (InterruptedException e)
                {
                    if (mIsInterrupted)
                    {
                        MeshLog.i(DEBUG, USE_LOG4J, CLASS, "OfferTaskThread execute() is interrupted");
                        break;
                    }
                    else
                    {
                        e.printStackTrace();
                    }
                }
            } // while end
        }
        
        private boolean isTargetBssidUsing(String bssid)
        {
            boolean result = false;
            
            for (int i = 0; i < mAddedTaskList.size(); i++)
            {
                ProxyTask task = mAddedTaskList.get(i);
                if (task.isFinished())
                {
                    MeshLog.d(DEBUG, USE_LOG4J, CLASS, task.getTargetBssid() + " is finished, remove it");
                    mAddedTaskList.remove(i--);
                    continue;
                }
                if (task.isExpired())
                {
                    MeshLog.d(DEBUG, USE_LOG4J, CLASS, task.getTargetBssid() + " is expired, remove it");
                    mAddedTaskList.remove(i--);
                    continue;
                }
                if (task.getTargetBssid().equals(bssid))
                {
                    result = true;
                }
            }
            
            MeshLog.d(DEBUG, USE_LOG4J, CLASS, bssid + " checkTargetBssidIsUsing " + result);
            return result;
        }
    }
}
