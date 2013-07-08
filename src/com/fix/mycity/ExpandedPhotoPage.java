package com.fix.mycity;

import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.loopj.android.image.SmartImageView;

public class ExpandedPhotoPage extends Activity {

	int position = 0;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.expandedimagepage);
		position = this.getIntent().getIntExtra("position", 0);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setCustomView(R.layout.expanded_actionbar);

		View actionBarView = actionBar.getCustomView();

		ImageButton check_button = (ImageButton) actionBarView
				.findViewById(R.id.check_button);
		check_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(74,
						200, 153)));

				Intent intent = new Intent(ExpandedPhotoPage.this,
						InfographicPage.class);
				startActivity(intent);

			}

		});

		final TextView description = (TextView) findViewById(R.id.pg_description);
		final TextView hashtags = (TextView) findViewById(R.id.pg_tags);

		Firebase f_description = new Firebase(
				"https://fixmycity.firebaseio.com/image" + position
						+ "/description");
		Firebase f_hashtags = new Firebase(
				"https://fixmycity.firebaseio.com/image" + position + "/tags");

		f_description.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				description.setText(arg0.getValue().toString());

			}

		});
		f_hashtags.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				hashtags.setText(arg0.getValue().toString());

			}

		});

		SmartImageView image_view = (SmartImageView) this
				.findViewById(R.id.smartimage);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor edit = prefs.edit();
		String url_string = prefs.getString("list_of_urls", "");
		List<String> urls = Arrays.asList(url_string.split("\\s*;\\s*"));
		image_view.setImageUrl(urls.get(position));

	}
}
