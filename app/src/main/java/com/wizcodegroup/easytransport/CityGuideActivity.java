package com.wizcodegroup.easytransport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wizcodegroup.easytransport.extra.AllConstants;

public class CityGuideActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
	// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in
	// Milliseconds

	protected LocationManager locationManager;

    private LinearLayout taxi, troto, gasstation, car_repair, car_wash;
	private static Context con;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_nearby);
		con = this;
		iUI();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
	}

	/* AlertMethod */

	protected void alertbox(String title, String mymessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Device GPS is Off").setCancelable(false)
				.setTitle("Gps Status").setPositiveButton("Gps On",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// finish the current activity
								// AlertBoxAdvance.this.finish();
								Intent myIntent = new Intent(
										Settings.ACTION_SECURITY_SETTINGS);
								startActivity(myIntent);
								dialog.cancel();
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel the dialog box
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void showCurrentLocation() {

		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());
			//Toast.makeText(CityGuideActivity.this, message, Toast.LENGTH_LONG)
			//		.show();

			Log.e("GeoData:", message);
			// final TextView myLat = (TextView) findViewById(R.id.myLat);

			double lat = location.getLatitude();

			// myLat.setText(String.valueOf(lat));

			// final TextView myLng = (TextView) findViewById(R.id.myLng);

			double lng = location.getLongitude();
			// myLng.setText(String.valueOf(lng));

			AllConstants.UPlat = String.valueOf(lat);
			AllConstants.UPlng = String.valueOf(lng);
		}

	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());

			// Toast.makeText(SplashScreenActivity.this, message,
			// Toast.LENGTH_LONG).show();
		}

		public void onStatusChanged(String s, int i, Bundle b) {
//			Toast.makeText(CityGuideActivity.this, "Provider status changed",
//					Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
			alertbox("Gps Status!!", "Your GPS is: OFF");

//			Toast.makeText(CityGuideActivity.this,
//					"Provider disabled by the user. GPS turned off",
//					Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String s) {
			Toast.makeText(CityGuideActivity.this,
                    "GPS turned on",
                    Toast.LENGTH_LONG).show();
		}

	}

	/* Initialize User Interface */

	private void iUI() {

        taxi = (LinearLayout) findViewById(R.id.taxi);
        taxi.setOnClickListener(this);

        troto = (LinearLayout) findViewById(R.id.troto);
        troto.setOnClickListener(this);


        car_wash = (LinearLayout) findViewById(R.id.car_wash);
        car_wash.setOnClickListener(this);

        gasstation = (LinearLayout) findViewById(R.id.gasstation);
        gasstation.setOnClickListener(this);

        car_repair = (LinearLayout) findViewById(R.id.car_repair);
        car_repair.setOnClickListener(this);

    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// finish();
		// android.os.Process.killProcess(android.os.Process.myPid());
		
		
		showCurrentLocation();
		
		
		switch (v.getId()) {


            case R.id.taxi:
                AllConstants.topTitle = "Taxi Stations";
                AllConstants.query = "taxi";
                final Intent taxi = new Intent(this, ListActivity.class);
                taxi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(taxi);

                break;

            case R.id.troto:
                AllConstants.topTitle = "Troto Stations";
                AllConstants.query = "bus_station";
                final Intent troto = new Intent(this, ListActivity.class);
                troto.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(troto);

                break;

            case R.id.gasstation:
                AllConstants.topTitle = "Filling Stations";
                AllConstants.query = "gas_station";
                final Intent gasstation = new Intent(this, ListActivity.class);
                gasstation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gasstation);

                break;

            case R.id.car_wash:
                AllConstants.topTitle = "Car Wash";
                AllConstants.query = "car_wash";
                final Intent car_wash = new Intent(this, ListActivity.class);
                car_wash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(car_wash);

                break;

            case R.id.car_repair:
                AllConstants.topTitle = "Car Repair";
                AllConstants.query = "car_repair";
                final Intent car_repair = new Intent(this, ListActivity.class);
                car_repair.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(car_repair);

                break;


        }

	}

    /*public void btnAbout(View v) {
        AllConstants.webUrl = "http://adeluleye.github.io/appstore.html";
        AllConstants.topTitle = "ABOUT VICINITYAPP";
        Intent next = new Intent(con, DroidWebViewActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);
    }

    public void btnFacebook(View v) {
        AllConstants.webUrl = "https://www.facebook.com/pages/Vicinity/380480888768169?ref=bookmarks";
        AllConstants.topTitle = "VICINITYAPP FAN PAGE";
        Intent next = new Intent(con, DroidWebViewActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);
    }

    public void btnTwitter(View v) {
        AllConstants.webUrl = "https://twitter.com/adeluleye";
        AllConstants.topTitle = "VICINITYAPP ON TWITTER";
        Intent next = new Intent(con, DroidWebViewActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);
    }

    public void btnShare(View v) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "VicinityApp");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Hi friend! I have found a good app which helps me in locating and exploring places with ease in my vicinity. " +
                        "Now, i can easily search for good restaurants, churches, nearest ATM points and lots " +
                        "of cool places. Kindly download VicinityApp!");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }*/
}