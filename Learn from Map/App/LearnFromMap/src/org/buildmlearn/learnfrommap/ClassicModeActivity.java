package org.buildmlearn.learnfrommap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class ClassicModeActivity extends ActionBarActivity {

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	private Intent intent;
	private Spinner spinner;
	private RelativeLayout mLoading;
	private RelativeLayout mMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setHomeButtonEnabled(true);
		setContentView(R.layout.activity_classic_mode);
		spinner = (Spinner) findViewById(R.id.classic_spinner1);
		mMain = (RelativeLayout)findViewById(R.id.classic_main);
		mLoading = (RelativeLayout)findViewById(R.id.classic_load);

		intent = new Intent(getBaseContext(), GameActivity.class);
		intent.putExtra("MODE", "CLASSIC_MODE");
		intent.putExtra("SELECTION", "COUNTRY");

		// Create an ArrayAdapter using the string array and a default spinner layout
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_view,  R.array.countries_array);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.countries_array, R.layout.spinner_text_view);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				
				getCountry(location.getLatitude(), location.getLongitude());
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				Toast.makeText(getApplicationContext(), "onStatusChanged", Toast.LENGTH_LONG).show();
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "onProviderEnabled", Toast.LENGTH_LONG).show();
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "Location Services are disabled", Toast.LENGTH_LONG).show();
				mLoading.setVisibility(View.GONE);
				mMain.setVisibility(View.VISIBLE);
			}
		};

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLoading.setVisibility(View.GONE);
		mMain.setVisibility(View.VISIBLE);
	}


	public void customCountry(View v)
	{
		String country = spinner.getSelectedItem().toString();
		intent.putExtra("VALUE", country);
		intent.putExtra("DISPLAY", "Country: " + country);
		startActivity(intent);

	}

	public void loadLocation(View v)
	{

		mLoading.setVisibility(View.VISIBLE);
		mMain.setVisibility(View.GONE);
		// Register the listener with the Location Manager to receive location updates


		if(location != null)
		{
			getCountry(location.getLatitude(), location.getLongitude());
		}
		else
		{
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}

	}

	private void getCountry(double lat, double lng) {
		String googleurl = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyACYVxd_d-49UnhqibCI6F9f7b5Gw1qTSc&";
		Log.v("HTTP" , "Latitude is: " + lat + "Longitude is:" + lng);
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(googleurl);

		sbuilder.append("latlng=" + lat + "," + lng);
		sbuilder.append("&sensor=true");
		String url = sbuilder.toString();
		Log.v("URL", url);
		StringRequest myReq = new StringRequest(Method.GET, 
				url,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("VOLLEY", response);
				try {
					JSONObject main = new JSONObject(response);
					JSONArray array = main.getJSONArray("results");
					JSONObject obj = array.getJSONObject(0);
					array = obj.getJSONArray("address_components");
					for(int i=0; i<array.length(); i++)
					{
						obj = array.getJSONObject(i);
						Log.d("JSON" + i, obj.toString());	
						JSONArray tempArray = obj.getJSONArray("types");
						if(tempArray.getString(0).equals("country"))
						{
							String country = obj.getString("long_name");
							Log.e("Country", country);
							intent.putExtra("VALUE", country);
							intent.putExtra("DISPLAY", "Country: " + country);
							startActivity(intent);
							locationManager.removeUpdates(locationListener);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "There was some error fetching your location\nError: " + e.getMessage(), Toast.LENGTH_LONG).show();
					mLoading.setVisibility(View.GONE);
					mMain.setVisibility(View.VISIBLE);
					e.printStackTrace();
				}

			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("VOLLEY ERROR", error.getMessage());
				Toast.makeText(getApplicationContext(), "There was some error fetching your location\nError: " + error.getMessage(), Toast.LENGTH_LONG).show();
				mLoading.setVisibility(View.GONE);
				mMain.setVisibility(View.VISIBLE);
			}
		});
		RequestQueue mQueue = Volley.newRequestQueue(this);
		mQueue.add(myReq);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classic_mode, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showCustomDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showCustomDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(ClassicModeActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.about_dialog);   
		dialog.show();
	}



}
