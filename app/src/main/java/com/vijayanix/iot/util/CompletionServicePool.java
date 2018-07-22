package com.vijayanix.iot.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hxhoua on 2018/6/29.
 */

public class CompletionServicePool {

	static final int THREADS_CORE_COUNT = 8;
	static final int THREADS_MAX_COUNT = 16;
	static final long THREADS_KEEP_ALIVE_TIME = 60L;
	static final TimeUnit THREADS_KEEP_ALIVE_UNIT = TimeUnit.SECONDS;

	private static final int POOL_SIZE = 10;
	private  static  CompletionService cService;
	private static ExecutorService mExecutorService;


	private CompletionServicePool(){
		// 创建线程池
		mExecutorService =
				new ThreadPoolExecutor(THREADS_CORE_COUNT, THREADS_MAX_COUNT, THREADS_KEEP_ALIVE_TIME,
						THREADS_KEEP_ALIVE_UNIT, new LinkedBlockingQueue<Runnable>());
		cService = new ExecutorCompletionService(mExecutorService);
	}

	private static class InstanceHolder
	{
		static CompletionServicePool instance = new CompletionServicePool();
	}

	public static CompletionServicePool getInstance()
	{
		return CompletionServicePool.InstanceHolder.instance;
	}


	public <T> Future<T> submit(final Callable<T> task){
		return (Future<T>) cService.submit(task);
	}


	public <T> Future<T> take(){

		Future<T> future = null;
		try {
			future =  cService.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return future;
	}


	public void shutdown() {
		mExecutorService.shutdown();
	}

	public void shutdownNow() {
		mExecutorService.shutdownNow();
	}
}
