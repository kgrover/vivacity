package com.fix.mycity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class FeedActivity extends FragmentActivity implements
		ActionBar.TabListener, ValueEventListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ArrayList<String> image_urls, image_upvotes, image_lats, image_longs;
	static double longitude;

	static double latitude;
	Uri img_uri;
	static int num_images;
	static Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		c = this.getApplicationContext();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_layout);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(255, 136, 0)));
		View actionBarView = actionBar.getCustomView();

		Intent intent = new Intent(this, PayPalService.class);

		// live: don't put any environment extra
		// sandbox: use PaymentActivity.ENVIRONMENT_SANDBOX
		// intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
		// PaymentActivity.ENVIRONMENT_SANDBOX);
		//
		intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID,
				"AcuduhCsj00a7ZkbLoM3mYIM0rsQeIiQuMpPr60pep2USldG9yuNV_9kD7CP");
		intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
				PaymentActivity.ENVIRONMENT_SANDBOX);
		intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
				"jain.shubhankar-facilitator@gmail.com");
		startService(intent);

		ImageButton cameraLaunch = (ImageButton) actionBarView
				.findViewById(R.id.loginbutton);
		cameraLaunch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File dir = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

				File output = new File(dir, "CameraContentDemo.jpeg");
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
				img_uri = Uri.fromFile(output);
				startActivityForResult(i, 2);
			}

		});

		Firebase f = new Firebase("https://fixmycity.firebaseio.com/counter");
		f.addValueEventListener(FeedActivity.this);

		System.out.println("THE NAME IS" + f.toString());

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		this.getLocation();
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}

	public void getLocation() {
		com.fix.mycity.MyLocation.LocationResult locationResult = new com.fix.mycity.MyLocation.LocationResult() {
			@Override
			public void gotLocation(Location location) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();

				final SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(FeedActivity.this);
				final SharedPreferences.Editor edit = prefs.edit();

				edit.putFloat("longitude", (float) longitude);
				edit.putFloat("latitude", (float) latitude);

				edit.commit();
				System.out.println("Location: " + longitude + ", " + latitude);
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this.getApplicationContext(), locationResult);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {
			Bitmap bm = null;
			try {
				bm = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), img_uri);
				bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2,
						bm.getHeight() / 2, false);
			} catch (Exception e) {
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
																// bitmap
																// // object
			byte[] b = baos.toByteArray();
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			// encodedImage = encodedImage.substring(0, encodedImage.length() /
			// 500);
			System.out.println("ENCODED IMAGE" + encodedImage.length());
			// uploadImage("1", "new" + encodedImage);
			try {
				ServerAPI.upload_image_data(encodedImage, FeedActivity.this);

			} catch (Exception e) {
				System.out.println("ERROR:" + e);
			}
		} else if (requestCode == 0) {

			PaymentConfirmation confirm = data
					.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
			if (confirm != null) {
				try {
					Log.i("paymentExample", confirm.toJSONObject().toString(4));

					// TODO: send 'confirm' to your server for verification.
					// see
					// https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
					// for more details.

				} catch (JSONException e) {
					Log.e("paymentExample",
							"an extremely unlikely failure occurred: ", e);
				}
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.i("paymentExample", "The user canceled.");
		} else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {
			Log.i("paymentExample",
					"An invalid payment was submitted. Please see the docs.");
		}

	}

	public void uploadImage(String url_number, String data) {
		Firebase firebase = new Firebase(
				"https://fixmycity.firebaseio.com/image/" + url_number);
		firebase.setValue(data);
	}

	public static class ProfileFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.blankview, container,
					false);

			Button map_button = (Button) rootView.findViewById(R.id.map_button);
			map_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent in = new Intent(ProfileFragment.this.getActivity(),
							MapActivity.class);
					ProfileFragment.this.getActivity().startActivity(in);
				}

			});

			return rootView;
		}

	}

	public static class MapShowingFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.profile_page, container,
					false);

			return rootView;
		}

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			if (position == 1) {
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;
			} else if (position == 0) {
				// Fragment mFragment = getSupportFragmentManager()
				// .findFragmentById(R.id.mapFragment);
				// Fragment fragment = new MySupportMapFragment();
				// return fragment;

				MapShowingFragment fr = new MapShowingFragment();
				return fr;

			} else {
				ProfileFragment fr = new ProfileFragment();
				return fr;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	// public static class MySupportMapFragment extends Fragment {
	// public static final String ARG_SECTION_NUMBER = "section_number";
	//
	// public MySupportMapFragment() {
	// }
	//
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	//
	// View rootView = inflater.inflate(R.layout.mapfragment, container,
	// false);
	// MapView map = (MapView) rootView.findViewById(R.id.map_view_track);
	// System.out.println("MAP!" + map.getMap());
	// return rootView;
	//
	// }
	// }

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		static ListView list;
		static Context c;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_feed_dummy,
					container, false);
			list = (ListView) rootView.findViewById(R.id.listView1);
			list.setAdapter(new MainFeedListAdapter(DummySectionFragment.this
					.getActivity(), 0, DummySectionFragment.this.getActivity()));

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					System.out.println("CLICKED");
					Intent intent = new Intent(DummySectionFragment.this
							.getActivity(), ExpandedPhotoPage.class);
					intent.putExtra("position", arg2);
					startActivity(intent);
				}

			});
			c = this.getActivity().getApplicationContext();
			return rootView;
		}

		public void uploadSampleData() {
			Firebase firebase = new Firebase(
					"https://fixmycity.firebaseio.com/whatever/2");
			firebase.setValue("This should be added to the whatever1 directory.");
		}

		public static void refresh(final Activity a) {
			list.setAdapter(new MainFeedListAdapter(c, num_images, a));
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					System.out.println("CLICKED");
					Intent intent = new Intent(a, ExpandedPhotoPage.class);
					intent.putExtra("position", arg2);
					a.startActivity(intent);
				}

			});
		}
	}

	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataChange(DataSnapshot snap) {
		num_images = Integer.parseInt(String
				.valueOf(snap.getValue().toString()));
		loadImages();
		loadImageLats();
		loadImageLongs();
	}

	public void doListLoading() {
		System.out.println("Loading List...");

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor edit = prefs.edit();

		String list_of_urls = "";

		for (String a : image_urls) {
			list_of_urls += a + ";";
		}

		edit.putString("list_of_urls", list_of_urls);
		edit.commit();

		DummySectionFragment.refresh(this);
	}

	private void loadImages() {
		image_urls = new ArrayList<String>();
		for (int i = 0; i < num_images; i++) {

			Firebase new_image = new Firebase(
					"https://fixmycity.firebaseio.com/image" + i + "/link");
			new_image.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDataChange(DataSnapshot arg0) {
					System.out.println("VALUE" + arg0.getValue());
					image_urls.add((String) arg0.getValue());
					if (image_urls.size() == num_images) {
						doListLoading();
					}

				}

			});
		}

	}

	private void loadImageLats() {
		image_lats = new ArrayList<String>();
		for (int i = 0; i < num_images; i++) {

			Firebase new_image = new Firebase(
					"https://fixmycity.firebaseio.com/image" + i + "/latitude");
			new_image.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDataChange(DataSnapshot arg0) {
					System.out.println("VALUE" + arg0.getValue());
					image_lats.add((String) arg0.getValue());
					if (image_lats.size() == num_images) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(FeedActivity.this);
						final SharedPreferences.Editor edit = prefs.edit();

						String list_of_lats = "";

						for (String a : image_lats) {
							list_of_lats += a + ";";
						}

						edit.putString("list_of_lats", list_of_lats);
						edit.commit();
					}

				}

			});
		}

	}

	private void loadImageLongs() {
		image_longs = new ArrayList<String>();
		for (int i = 0; i < num_images; i++) {

			Firebase new_image = new Firebase(
					"https://fixmycity.firebaseio.com/image" + i + "/longitude");
			new_image.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDataChange(DataSnapshot arg0) {
					System.out.println("VALUE" + arg0.getValue());
					image_longs.add((String) arg0.getValue());
					if (image_longs.size() == num_images) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(FeedActivity.this);
						final SharedPreferences.Editor edit = prefs.edit();

						String list_of_longs = "";

						for (String a : image_longs) {
							list_of_longs += a + ";";
						}

						edit.putString("list_of_longs", list_of_longs);
						edit.commit();
					}

				}

			});
		}

	}

}
