package com.wizcodegroup.easytransport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wizcodegroup.easytransport.extra.AlertMessage;
import com.wizcodegroup.easytransport.extra.AllConstants;
import com.wizcodegroup.easytransport.extra.AllURL;
import com.wizcodegroup.easytransport.extra.CacheImageDownloader;
import com.wizcodegroup.easytransport.extra.PrintLog;
import com.wizcodegroup.easytransport.extra.SharedPreferencesHelper;
import com.wizcodegroup.easytransport.holder.AllCityMenu;
import com.wizcodegroup.easytransport.model.CityMenuList;
import com.wizcodegroup.easytransport.parser.CityMenuParser;

public class ListActivity extends Activity {
	/** Called when the activity is first created. */

	private ListView list;
	private Context con;
	private Bitmap defaultBit;
	private RestaurantAdapter adapter;
	private ProgressDialog pDialog;
	private CacheImageDownloader downloader;
	
	

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
		con = this;
		initUI();
	 }
	private void initUI() {
		// TODO Auto-generated method stub

        final TextView toptitle = (TextView)findViewById(R.id.toptitle);
        toptitle.setText(AllConstants.topTitle.toString().trim());

        list = (ListView) findViewById(R.id.menuListView);
		downloader = new CacheImageDownloader();
//		defaultBit = BitmapFactory.decodeResource(getResources(),
//				R.drawable.img);

		// parseQuery(AllConstants.query);
		parseQuery();

		PrintLog.myLog("Query in activity : ", AllConstants.query);

	}

	private void parseQuery() {
		// TODO Auto-generated method stub
		if (!SharedPreferencesHelper.isOnline(con)) {
			AlertMessage.showMessage(con, "Error", "No internet connection");
			return;
		}

		pDialog = ProgressDialog.show(this, "", "Loading..", false, false);

		final Thread d = new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					
//					if (CityMenuParser.connect(con, AllURL.nearByURL(
//							AllConstants.UPlat, AllConstants.UPlng,
//							AllConstants.query, AllConstants.apiKey, AllConstants.radius)))
					
					if (CityMenuParser.connect(con, AllURL.nearByURL(
							AllConstants.UPlat, AllConstants.UPlng,
							AllConstants.query, AllConstants.apiKey))) {

						PrintLog.myLog("Size of City : ", AllCityMenu
								.getAllCityMenu().size()
								+ "");

					}

				} catch (final Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						if (pDialog != null) {
							pDialog.cancel();
						}
						if (AllCityMenu.getAllCityMenu().size() == 0) {

						} else {

							adapter = new RestaurantAdapter(con);
							list.setAdapter(adapter);
							adapter.notifyDataSetChanged();
						}

					}
				});

			}
		});
		d.start();
	}

	class RestaurantAdapter extends ArrayAdapter<CityMenuList> {
		private final Context con;

		public RestaurantAdapter(Context context) {
			super(context, R.layout.rowlist, AllCityMenu.getAllCityMenu());
			con = context;
			// TODO Auto-generated constructor stub

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				final LayoutInflater vi = (LayoutInflater) con
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.rowlist, null);
			}

			if (position < AllCityMenu.getAllCityMenu().size()) {
				final CityMenuList CM = AllCityMenu.getCityMenuList(position);

				Typeface title = Typeface.createFromAsset(getAssets(),
                        "fonts/ROBOTO-REGULAR.TTF");
				Typeface add = Typeface.createFromAsset(getAssets(),
                        "fonts/ROBOTO-LIGHT.TTF");

				// ----Address----

				final TextView address = (TextView) v
						.findViewById(R.id.rowAddress);

				try {
					address
							.setText(CM.getVicinity().toString()
									.trim());
					address.setTypeface(add);
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {

					AllConstants.photoReferrence = CM.getPhotoReference()
							.toString().trim();
					PrintLog
							.myLog("PPRRRef", AllConstants.photoReferrence + "");
				} catch (Exception e) {
					// TODO: handle exception
				}

				// ---Image---

				final ImageView icon = (ImageView) v
						.findViewById(R.id.rowImageView);
				
				try {

					AllConstants.iconUrl = CM.getIcon()
							.toString().trim();
					PrintLog
							.myLog("iconURL:", AllConstants.iconUrl + "");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				try {

					String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=190&photoreference="
							+ AllConstants.photoReferrence
							+ "&sensor=true&key=" + AllConstants.apiKey;

					if (AllConstants.photoReferrence.length() != 0) {

//						downloader.download(AllConstants.iconUrl.trim(), icon);
						downloader.download(imgUrl.trim(), icon);

						AllConstants.cPhotoLink = imgUrl.replaceAll(" ", "%20");
					}

					else {
						downloader.download(AllConstants.iconUrl.trim(), icon);
						AllConstants.cPhotoLink = AllConstants.iconUrl.replaceAll(" ", "%20");
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				// ------Rating ---
//				final RatingBar listRatings = (RatingBar) v
//						.findViewById(R.id.ratingBarL);

				String rating = CM.getRating();

				try {

					Float count = Float.parseFloat(rating);

//					listRatings.setRating(count);

				} catch (Exception e) {

				}

				// ----Name----

				final TextView name = (TextView) v.findViewById(R.id.rowName);
				try {
					name.setText(CM.getName().toString().trim());
					name.setTypeface(title);
				} catch (Exception e) {
					// TODO: handle exception
				}
				v.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						AllConstants.referrence = CM.getReference().toString()
								.trim();
						try {
							AllConstants.photoReferrence = CM
									.getPhotoReference().toString().trim();
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							AllConstants.detailsiconUrl = CM
									.getIcon().toString().trim();
						} catch (Exception e) {
							// TODO: handle exception
						}
						final Intent iii = new Intent(con,
								ListDetailsActivity.class);
						iii.putExtra("POSITION", position);
						iii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(iii);

						// }

					}
				});

			}

			// TODO Auto-generated method stub
			return v;
		}

	}

	public void btnHome(View v) {

		Intent next = new Intent(con, CityGuideActivity.class);
		next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(next);

	}

	public void btnBack(View v) {
		finish();

	}
}
