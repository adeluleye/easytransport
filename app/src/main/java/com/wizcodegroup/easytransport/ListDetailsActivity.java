package com.wizcodegroup.easytransport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wizcodegroup.easytransport.extra.AlertMessage;
import com.wizcodegroup.easytransport.extra.AllConstants;
import com.wizcodegroup.easytransport.extra.AllURL;
import com.wizcodegroup.easytransport.extra.CacheImageDownloader;
import com.wizcodegroup.easytransport.extra.PrintLog;
import com.wizcodegroup.easytransport.extra.SharedPreferencesHelper;
import com.wizcodegroup.easytransport.holder.AllCityDetails;
import com.wizcodegroup.easytransport.holder.AllCityReview;
import com.wizcodegroup.easytransport.model.CityDetailsList;
import com.wizcodegroup.easytransport.model.ReviewList;
import com.wizcodegroup.easytransport.parser.CityDetailsParser;

public class ListDetailsActivity extends Activity {
	/** Called when the activity is first created. */

	private Context con;
	private String pos = "";
	//private TextView cName, cAdd, cCell, cWeb;
    private TextView cName, cAdd;
	private CacheImageDownloader downloader;
	private Bitmap defaultBit;
	private ProgressDialog pDialog;
	private CityDetailsList CD;
	private RatingBar detailsRat;
    private RestaurantAdapter adapter;
    private ListView list;
    private String Demourl = "https://maps.googleapis.com/maps/api/place/details/json?reference=CnRkAAAAj2VWwXQIX-TFfx6XaexF9rN6Kc005BMP8h0V2pKj7IuyLPWUBCt7gnHr8q9RYWeIva06HuChuwhxsio4f7c9s5aLynGzzX19Oatq8Q9Oz8w2Zj54B8PUNgDNcQ6rHKuKmpAPJBXitOcAYugvPZshDBIQsYMRaNz0n5VfpHx6C2GCFRoUsCD2Zx0P_a-rqyxHN-GTC1QZz2U&sensor=true&key=AIzaSyC6zHflVgVCLKEMWBFMFm5qj0Jis-eoR4U";


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detailslayout);
		con = this;

		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub
        list = (ListView) findViewById(R.id.reviewListView);
		cName = (TextView) findViewById(R.id.cName);
		cAdd = (TextView) findViewById(R.id.cAddress);
		//cCell = (TextView) findViewById(R.id.cPhone);
		//cWeb = (TextView) findViewById(R.id.cWeb);
		detailsRat = (RatingBar) findViewById(R.id.detailsRating);
		downloader = new CacheImageDownloader();
		defaultBit = BitmapFactory.decodeResource(getResources(),
                R.drawable.car);

		updateUI();

	}

	/****
	 * 
	 * update wrestler info
	 * 
	 */

	private void updateUI() {
		if (!SharedPreferencesHelper.isOnline(con)) {
			AlertMessage.showMessage(con, "Error", "No Internet Connection");
			return;
		}

		pDialog = ProgressDialog.show(this, "", "Loading..", false, false);

		final Thread d = new Thread(new Runnable() {

			public void run() {
                // TODO Auto-generated method stub

                try {
                    if (CityDetailsParser.connect(con, AllURL
                            .cityGuideDetailsURL(AllConstants.referrence,
                                    AllConstants.apiKey))) {
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
                        Typeface title = Typeface.createFromAsset(getAssets(),
                                "fonts/ROBOTO-REGULAR.TTF");
                        Typeface add = Typeface.createFromAsset(getAssets(),
                                "fonts/ROBOTO-LIGHT.TTF");
                        try {

                            CD = AllCityDetails.getAllCityDetails()
                                    .elementAt(0);

                            cName.setText(CD.getName().trim());
                            try {

                                cName.setText(CD.getName().trim());
                                cName.setTypeface(title);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            try {

                                cAdd.setText(CD.getFormatted_address().trim());
                                cAdd.setTypeface(add);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
							/*try {

								cCell.setText(CD.getFormatted_phone_number()
										.trim());
								cCell.setTypeface(add);
							} catch (Exception e) {
								// TODO: handle exception
							}
							try {

								cWeb.setText(CD.getWebsite().trim());
								cWeb.setTypeface(add);
							} catch (Exception e) {
								// TODO: handle exception
							}*/
                            try {

                                AllConstants.lat = CD.getLat().trim();

                                AllConstants.lng = CD.getLng().trim();
                                PrintLog.myLog("GEO", AllConstants.lat);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                            // ------Rating ---

                            String rating = CD.getRating();

                            try {

                                Float count = Float.parseFloat(rating);
                                // PrintLog.myLog("Rating as float", count +
                                // "");
                                detailsRat.setRating(count);

                            } catch (Exception e) {

                                PrintLog.myLog("error at rating", e.toString());
                            }

                            // ---Photo---
                            try {

                                String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                        + AllConstants.photoReferrence
                                        + "&sensor=true&key="
                                        + AllConstants.apiKey;

                                final ImageView lImage = (ImageView) findViewById(R.id.imageViewL);

                                if (AllConstants.photoReferrence.length() != 0) {

                                    downloader.download(imgUrl.trim(), lImage);

                                } else {
                                    lImage.setImageBitmap(defaultBit);

//									downloader.download(AllConstants.detailsiconUrl.trim(), lImage);
                                }

                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        if (AllCityReview.getAllCityReview().size() == 0) {

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

    class RestaurantAdapter extends ArrayAdapter<ReviewList> {
        private final Context con;

        public RestaurantAdapter(Context context) {
            super(context, R.layout.review, AllCityReview.getAllCityReview());
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
                v = vi.inflate(R.layout.review, null);
            }

            if (position < AllCityReview.getAllCityReview().size()) {
                final ReviewList CM = AllCityReview.getReviewList(position);

                Typeface add = Typeface.createFromAsset(getAssets(),
                        "fonts/ROBOTO-LIGHT.TTF");

                // ----Address----

                final TextView address = (TextView) v
                        .findViewById(R.id.AuthorName);

                try {
                    address.setText(CM.getAuthor_name().toString().trim());
                    address.setTypeface(add);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                // final RatingBar rRating = (RatingBar)
                // v.findViewById(R.id.rRatingBar);
                try {

                    String reviewRating = CM.getAuthor_rating();

                    try {

                        Float rcount = Float.parseFloat(reviewRating);
                        // PrintLog.myLog("Rating as float", count +
                        // "");
                        // rRating.setRating(rcount);

                    } catch (Exception e) {

                        PrintLog.myLog("rRating:", reviewRating);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                // ---Image---

                final TextView name = (TextView) v.findViewById(R.id.reView);
                try {
                    name.setText(CM.getAuthor_text().toString().trim());
                    name.setTypeface(add);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

            // TODO Auto-generated method stub
            return v;
        }

    }

    private void call() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + AllConstants.cCell + ""));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {

        }
    }

    public void cPhone(View v) {
        if (CD.getFormatted_phone_number().length() != 0) {
            try {

                call();

                AllConstants.cCell = CD.getFormatted_phone_number().trim();

                PrintLog.myLog("Tel::", AllConstants.cCell);

            } catch (Exception e) {
                // TODO: handle exception
            }

        } else {

            Toast.makeText(ListDetailsActivity.this, "Sorry!No Phone Number Found.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void webView(View v) {

        if (CD.getWebsite().length() != 0) {
            try {

                AllConstants.webUrl = CD.getWebsite().trim();
                Intent next = new Intent(con, DroidWebViewActivity.class);
                next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(next);
                PrintLog.myLog("Website::", AllConstants.cWeb);
            } catch (Exception e) {
                // TODO: handle exception
            }

        } else {

            Toast.makeText(ListDetailsActivity.this, "Sorry!No URL Found.",
                    Toast.LENGTH_LONG).show();
        }

    }

	public void mapViewBtn(View v) {

		Intent next = new Intent(con, MapViewActivity.class);
		next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(next);

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
