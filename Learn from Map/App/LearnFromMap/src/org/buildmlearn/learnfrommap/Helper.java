package org.buildmlearn.learnfrommap;

import org.buildmlearn.learnfrommap.databasehelper.Database;
import org.buildmlearn.learnfrommap.maphelper.CustomTileProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.TileOverlayOptions;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class Helper extends ActionBarActivity {

	public GoogleMap mapView;
	public Location myLocation;
	private SupportMapFragment mapFragment;
	private Handler handler;
	private static final String TABLE_NAME = "main";
	//////////////////
	//Override these function 
	
	public void onMapReady()
	{
		mapView.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				Toast.makeText(getApplicationContext(), "Zoom: " + cameraPosition.zoom , Toast.LENGTH_SHORT).show();
				if(cameraPosition.zoom > (float)4.0)
				{
					mapView.animateCamera(CameraUpdateFactory.zoomTo((float) 4.0));
				}
				
			}
		});

	}

	public boolean isMapReady() 
	{
		return mapView != null;
	}

	public void showErrorMessage(String msg)
	{

	}

	public void onDatabaseLoad(String msg)
	{
		
	}
	
	public void onDatabaseLoadError(String msg)
	{
		
	}
	//////////////////Public Function //////////////////
	
	public void getMapView(SupportMapFragment mapFragment)
	{
		this.handler = new Handler();
		this.mapFragment = mapFragment;
		CheckMap();		
	}

	public void queryDatabase(String where, String[] whereArgs, String orderBy, String limit)
	{
		class QueryDatabase implements Runnable
		{
			String where;
			String[] whereArgs;
			String orderBy;
			String limit;
			public QueryDatabase(String where, String[] whereArgs,
					String orderBy, String limit) {
				super();
				this.where = where;
				this.whereArgs = whereArgs;
				this.orderBy = orderBy;
				this.limit = limit;
			}
			@Override
			public void run() {
				Database main_db = new Database(getApplicationContext());
				SQLiteDatabase db = main_db.getReadableDatabase();
				Cursor cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, orderBy, limit);
				if (getApplicationContext() != null) 
				{
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							Toast.makeText(getApplicationContext()
									, "Successfull", Toast.LENGTH_LONG).show();
						}
					});
				}

			}

		}

		Thread t = new Thread(new QueryDatabase(where, whereArgs, orderBy, limit));
		t.start();
	}

	public void loadDatabase()
	{
		new Thread(new Runnable() {
			
			String msg;
			
			@Override
			public void run() {
				try
				{
					Database main_db = new Database(getApplicationContext());
					SQLiteDatabase db = main_db.getReadableDatabase();
					db.close();
					main_db.close();
					msg = "Successfull";
					Thread.sleep(500);
					if (getApplicationContext() != null) 
					{
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								onDatabaseLoad(msg);
							}
						});
					}
				}
				catch(Exception e)
				{
					msg = "Error loading database\nError: " + e.getMessage();
					if (getApplicationContext() != null) 
					{
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								onDatabaseLoadError(msg);
							}
						});
					}

				}


			}
		}).start();
		
}

	//////////////////Private Function //////////////////
	
	private void setUpMap()
	{
		handler.post(new Runnable() {

			@Override
			public void run() {
				mapView = mapFragment.getMap();
				if(mapView == null)
				{
					handler.postDelayed(this, 1000);
				}
				else
				{
					mapView.setMapType(GoogleMap.MAP_TYPE_NONE);
					mapView.addTileOverlay(new TileOverlayOptions().tileProvider(new CustomTileProvider(getResources().getAssets(), mapView)));
					if(getApplicationContext() != null)
					{
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								onMapReady();

							}
						});
					}
				}

			}
		});

	}

	private void CheckMap() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int playServiceStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplication());

				if (playServiceStatus == ConnectionResult.SUCCESS) 
				{
					if (getVersionFromPackageManager(getApplicationContext()) >= 2) 
					{
						setUpMap();
						if (getApplicationContext() != null) 
						{
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									showErrorMessage("Google Maps  present");
								}
							});
						}
					} 
					else 
					{
						// OpenGL version is not supported
						if (getApplicationContext() != null) 
						{
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									showErrorMessage("Maps cannot be loaded:  OpenGL version is less than 2");
								}
							});
						}
					}
				} 
				else if (GooglePlayServicesUtil.isUserRecoverableError(playServiceStatus)) 
				{
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							showErrorMessage("Google Play services not present");
						}
					});
				} else 
				{
					if (getApplicationContext() != null) 
					{
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								showErrorMessage("Unknown Error");
							}
						});
					}
				}

			}

		}).start();

	}

	private int getVersionFromPackageManager(Context context) 
	{
		PackageManager packageManager = context.getPackageManager();
		FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
		if (featureInfos != null && featureInfos.length > 0) {
			for (FeatureInfo featureInfo : featureInfos) 
			{
				// Null feature name means this feature is the open
				// GLes version feature.
				if (featureInfo.name == null) 
				{
					if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED)
					{
						return getMajorVersion(featureInfo.reqGlEsVersion);
					} else 
					{
						return 1;
					}
				}
			}
		}
		return 1;
	}

	private int getMajorVersion(int glEsVersion) {
		return ((glEsVersion & 0xffff0000) >> 16);
	}



	


}
