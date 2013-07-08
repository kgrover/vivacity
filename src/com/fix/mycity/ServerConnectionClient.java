package com.fix.mycity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerConnectionClient {
	private static final String BASE_URL = "https://api.imgur.com/3/image";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.addHeader("Authorization", "Client-ID " + "860efcc62125409");
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	public static void put(RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.put(BASE_URL, params, responseHandler);
	}

}