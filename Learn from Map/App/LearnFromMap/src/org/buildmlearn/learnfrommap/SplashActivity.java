package org.buildmlearn.learnfrommap;

import org.buildmlearn.learnfrommap.databasehelper.Database;
import org.buildmlearn.learnfrommap.databasehelper.DatabaseHelper;
import org.buildmlearn.learnfrommap.maphelper.CustomReverseGeocoder;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends DatabaseHelper {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		loadDatabase();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.id.explore_world_map, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
	}

	@Override
	public void onDatabaseLoad(String msg) {
		super.onDatabaseLoad(msg);
		ProgressBar pb = (ProgressBar)findViewById(R.id.splash_loading);
		pb.setVisibility(View.GONE);
		ConnectivityManager cm =
		        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		if (isConnected)
		{
			CustomReverseGeocoder geocoder = new CustomReverseGeocoder(this);
			geocoder.getState();
			Intent intent= new Intent(getApplicationContext(), ExploreMode.class);
			startActivity(intent);
			finish();
		}
		else 
		{
			TextView tvMsg = (TextView)findViewById(R.id.splash_msg);
			tvMsg.setText("Error: Your device is not connected to internet");
			tvMsg.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDatabaseLoadError(String msg) {
		super.onDatabaseLoadError(msg);
		TextView tvMsg = (TextView)findViewById(R.id.splash_msg);
		ProgressBar pb = (ProgressBar)findViewById(R.id.splash_loading);
		tvMsg.setText(msg);
		tvMsg.setVisibility(View.VISIBLE);
		pb.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}	
		return super.onOptionsItemSelected(item);
	}


	//	@SuppressLint("NewApi")
	//	@Override
	//	protected void onResume()
	//	{
	//	    super.onResume();
	//
	//	    if (Build.VERSION.SDK_INT < 16)
	//	    {
	//	        // Hide the status bar
	//	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	//	        // Hide the action bar
	//	        getSupportActionBar().hide();
	//	    }
	//	    else
	//	    {
	//	        // Hide the status bar
	//	        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
	//	        // Hide the action bar
	//	        getActionBar().hide();
	//	    }
	//}

	public class DatabaseProcess extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Intent intent= new Intent(getApplicationContext(), ExploreMode.class);
			startActivity(intent);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Database db = new Database(getApplicationContext());
			db.close();
			return null;
		}

	}

}
