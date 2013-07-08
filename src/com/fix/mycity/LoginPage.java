package com.fix.mycity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginPage extends Activity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.loginpage);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(LoginPage.this);
		final SharedPreferences.Editor edit = prefs.edit();

		if (!prefs.getString("user_name", "").equals("")) {
			Intent intent = new Intent(LoginPage.this, FeedActivity.class);
			startActivity(intent);
		}

		final TextView full_name = (TextView) findViewById(R.id.fullname);
		final TextView _password = (TextView) findViewById(R.id.password);

		Button login = (Button) findViewById(R.id.loginbutton);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = full_name.getText().toString();
				String password = _password.getText().toString();

				edit.putString("user_name", name);
				edit.putString("password", password);
				edit.commit();

				Intent intent = new Intent(LoginPage.this, FeedActivity.class);
				startActivity(intent);
			}

		});

	}

}
