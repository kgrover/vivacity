package com.fix.mycity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.loopj.android.image.SmartImageView;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;

public class MainFeedListAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	int count;
	Context c;
	List<String> urls;
	Activity act;

	public MainFeedListAdapter(Context context, int count, Activity act) {
		c = context;
		inflater = LayoutInflater.from(context);
		this.count = count;
		this.act = act;
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		final SharedPreferences.Editor edit = prefs.edit();
		String url_string = prefs.getString("list_of_urls", "");
		urls = Arrays.asList(url_string.split("\\s*;\\s*"));

	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		System.out.println("URL ARRAY" + urls);
		// if (convertView == null) {
		convertView = inflater.inflate(R.layout.item_layout, null);
		ImageButton donateButton = (ImageButton) convertView
				.findViewById(R.id.donatebutton);

		ImageButton like_button = (ImageButton) convertView
				.findViewById(R.id.likebutton);
		like_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Firebase get_likes = new Firebase(
						"https://fixmycity.firebaseio.com/image" + position
								+ "/upvotes");
				get_likes.addValueEventListener(new ValueEventListener() {

					@Override
					public void onCancelled() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDataChange(DataSnapshot arg0) {
						int current_donation = Integer.parseInt(arg0.getValue()
								.toString());
						get_likes.setValue(current_donation += 1);
						get_likes.removeEventListener(this);
					}

				});

			}

		});
		final TextView num_likes = (TextView) convertView
				.findViewById(R.id.num_likes);

		final TextView money_donated = (TextView) convertView
				.findViewById(R.id.money_donated);

		Firebase likes_updater = new Firebase(
				"https://fixmycity.firebaseio.com/image" + position
						+ "/upvotes");
		likes_updater.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				num_likes.setText(arg0.getValue().toString());
			}

		});

		Firebase money_donated_updater = new Firebase(
				"https://fixmycity.firebaseio.com/image" + position
						+ "/money_donated");
		money_donated_updater.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				money_donated.setText(arg0.getValue().toString());
			}

		});

		donateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Firebase change_val = new Firebase(
						"https://fixmycity.firebaseio.com/image" + position
								+ "/money_donated");
				change_val.addValueEventListener(new ValueEventListener() {

					@Override
					public void onCancelled() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDataChange(DataSnapshot arg0) {

						int current_donation = Integer.parseInt(arg0.getValue()
								.toString());
						change_val.setValue(current_donation += 5);
						change_val.removeEventListener(this);

					}

				});

				PayPalPayment payment = new PayPalPayment(new BigDecimal(
						"10.00"), "USD", "hipster jeans");

				Intent intent = new Intent(act, PaymentActivity.class);

				// // comment this line out for live or set to
				// // PaymentActivity.ENVIRONMENT_SANDBOX for sandbox
				intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
						PaymentActivity.ENVIRONMENT_SANDBOX);

				// it's important to repeat the clientId here so that the SDK
				// has it if Android restarts your
				// app midway through the payment UI flow.
				intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID,
						"AcuduhCsj00a7ZkbLoM3mYIM0rsQeIiQuMpPr60pep2USldG9yuNV_9kD7CP");

				intent.putExtra(PaymentActivity.EXTRA_PAYER_ID,
						"k.grover123@gmail.com");

				intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
						"jain.shubhankar@gmail.com");
				intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

				act.startActivityForResult(intent, 0);

			}

		});

		SmartImageView image = (SmartImageView) convertView
				.findViewById(R.id.smartimage);
		image.setImageUrl(urls.get(position));

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(act, ExpandedPhotoPage.class);
				intent.putExtra("position", position);
				act.startActivity(intent);
			}

		});

		return convertView;
	}

	static class SampleViewHolder {
		TextView num_likes, money_donated;
		ImageButton donateButton, like_button;

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}