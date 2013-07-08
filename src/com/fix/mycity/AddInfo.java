package com.fix.mycity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class AddInfo extends Activity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.add_info);

		final TextView description = (TextView) this
				.findViewById(R.id.description);
		final TextView tags = (TextView) this.findViewById(R.id.tags);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor edit = prefs.edit();

		final int counter = prefs.getInt("counter", 0);

		ImageButton upload = (ImageButton) this.findViewById(R.id.upload);
		upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Firebase firebase = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/description");
				firebase.setValue(description.getText().toString());

				Firebase firebase2 = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/tags");
				firebase2.setValue(tags.getText().toString());

				Firebase username = new Firebase(
						"https://fixmycity.firebaseio.com/image" + counter
								+ "/username");

				username.setValue(prefs.getString("user_name", "nouser"));

				Firebase update_counter = new Firebase(
						"https://fixmycity.firebaseio.com/counter");

				int newcounter = counter + 1;

				edit.putInt("counter", newcounter);
				update_counter.setValue(newcounter);

				edit.commit();

				Intent intent = new Intent(AddInfo.this, FeedActivity.class);
				startActivity(intent);

			}

		});
	}

}
