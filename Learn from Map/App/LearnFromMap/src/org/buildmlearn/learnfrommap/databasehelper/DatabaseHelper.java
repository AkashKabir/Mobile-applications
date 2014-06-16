package org.buildmlearn.learnfrommap.databasehelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static String DB_PATH;
	private static final String TABLE_NAME = "main";
	public SQLiteDatabase database;
	public Context context;
	private SQLiteDatabase db;
	private static final String DB_NAME = "pls10.db";

	public SQLiteDatabase getDb() {
		return database;
	}


	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		Log.d("jj","ji");
		this.context = context;
		String packageName = context.getPackageName();
		DB_PATH = String.format("//data//data//%s//databases//", packageName);
		openDataBase();
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {

				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			Log.d("Here checkDataBase", "Before Error");
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		} 
		catch (SQLException e) {
			Log.d(this.getClass().toString(), "Error while checking db");
		}
		catch (Exception e)
		{
			Log.d("Exception", e.getMessage());
		}
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {
		//Open a stream for reading from our ready-made database
		//The stream source is located in the assets
		InputStream externalDbStream = context.getAssets().open(DB_NAME);

		//Path to the created empty database on your Android device
		String outFileName = DB_PATH + DB_NAME;

		//Now create a stream for writing the database byte by byte
		OutputStream localDbStream = new FileOutputStream(outFileName);

		//Copying the database
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		//Don�t forget to close the streams
		localDbStream.close();
		externalDbStream.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		if (database == null) {
			createDataBase();
			database = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase db) {}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public Cursor select(String where, String[] whereArgs, String orderBy, String limit)
	{
		db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, where, whereArgs, null, null, orderBy, limit);
		return cursor;
	}

	public void insert(String rawQuery)
	{
		//Insert logic here
	}
}

