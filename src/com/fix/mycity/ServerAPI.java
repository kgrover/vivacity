package com.fix.mycity;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.client.Firebase;
import com.fix.mycity.JsonData.ImgurResponse;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerAPI {

	public static void upload_image_data(String image_data, final Activity a)
			throws JSONException {

		RequestParams params = new RequestParams();
		params.put("image", image_data);

		ServerConnectionClient.post("", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(a);
				SharedPreferences.Editor edit = prefs.edit();

				int counter = prefs.getInt("counter", 0);

				Gson gson = new Gson();
				String link = gson.fromJson(response, ImgurResponse.class).data.link;
				System.out.println("IMGUR link: " + link);

				Firebase firebase = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/link");
				firebase.setValue(link);

				Firebase longitude = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/longitude");

				longitude.setValue(String.valueOf(prefs.getFloat("longitude",
						(long) 0.0)));

				Firebase latitude = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/latitude");
				latitude.setValue(String.valueOf(prefs.getFloat("latitude",
						(long) 0.0)));

				Firebase upvotes = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/upvotes");
				upvotes.setValue("0");

				Firebase money_donated = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/money_donated");
				money_donated.setValue(0);

				Intent intent = new Intent(a, AddInfo.class);
				a.startActivity(intent);
			}

			@Override
			public void onFinish() {
				System.out.println("finished");
			}

			@Override
			public void onFailure(Throwable error) {
				System.out.println("Imgur Error " + "Request Failed, error"
						+ error.toString());

			}

		});

	}
}
