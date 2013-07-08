package com.fix.mycity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	SupportMapFragment mapFragment;
	GoogleMap map;
	List<String> lats;
	List<String> longs;

	private static final LatLng MY_LOCATION = new LatLng(37.37726, -121.92312);

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapfragment);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor edit = prefs.edit();
		String lat_string = prefs.getString("list_of_lats", "");
		String long_string = prefs.getString("list_of_longs", "");

		lats = new ArrayList<String>(Arrays.asList(lat_string
				.split("\\s*;\\s*")));
		longs = new ArrayList<String>(Arrays.asList(long_string
				.split("\\s*;\\s*")));

		System.out.println("LATITUDES: " + lats);
		System.out.println("LONGITUDES: " + longs);

		lats.removeAll(Collections.singleton("null"));
		longs.removeAll(Collections.singleton("null"));

		MapFragment a = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map);
		System.out.println("A : " + a);
		map = a.getMap();
		while (map == null) {

		}
		if (map != null) {
			System.out.println("MAP" + map);
		}

		addMarkers();
	}

	public void addMarkers() {

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(MY_LOCATION, 15));

		for (int i = 0; i < lats.size(); i++) {
			if (Float.valueOf(lats.get(i)) != null
					&& Float.valueOf(longs.get(i)) != null) {
				map.addMarker(new MarkerOptions().position(
						new LatLng(Double.parseDouble(lats.get(i)), Double
								.parseDouble(longs.get(i)))).title("Location"));
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupMap();
	}

	private void setupMap() {
		if (map != null) {
			return;
		}
		map = mapFragment.getMap();
		if (map == null) {
			return;
		}

		doZoom();
	}

	private void doZoom() {
		if (map != null) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					45.424900, -75.694968), 17));
		}
	}
}