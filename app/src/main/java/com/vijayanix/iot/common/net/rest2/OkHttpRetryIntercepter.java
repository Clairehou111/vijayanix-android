package com.vijayanix.iot.common.net.rest2;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hxhoua on 2018/6/25.
 */

public class OkHttpRetryIntercepter implements Interceptor {

	public int maxRetry;//最大重试次数
	private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

	public OkHttpRetryIntercepter(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		System.out.println("retryNum=" + retryNum);
		Response response = chain.proceed(request);
		while (!response.isSuccessful() && retryNum < maxRetry) {
			retryNum++;
			System.out.println("retryNum=" + retryNum);
			response = chain.proceed(request);
		}
		return response;
	}
}
