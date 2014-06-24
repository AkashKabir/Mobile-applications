package org.buildmlearn.learnfrommap.questionmodule;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.buildmlearn.learnfrommap.databasehelper.Database;
import org.buildmlearn.learnfrommap.questionmodule.XmlQuestion.Type;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class BaseQuestion {

	protected String code;
	protected Type type;
	protected String format;
	protected String answer;
	protected boolean location;
	protected String relation;
	protected LocationType locationType;
	protected float lat;
	protected float lng;
	protected String locationKey;
	protected String locationValue;
	protected Context mContext;
	private Database db;
	private XmlQuestion xml;


	public static enum LocationType {None, Coordiates, String}


	public BaseQuestion(Context mContext, XmlQuestion question, float lat, float lng)
	{
		db = new Database(mContext);
		this.mContext = mContext;
		this.code = question.getCode();
		this.type = question.getType();
		this.format = question.getFormat();
		this.answer = question.getAnswer();
		this.location= question.isLocation();
		this.relation = question.getRelation();
		this.lat = lat;
		this.lng = lng;
		this.locationType = LocationType.Coordiates;
		this.xml = question;
	}

	public BaseQuestion(Context mContext, XmlQuestion question, String locationKey, String locationValue)
	{
		db = new Database(mContext);
		this.mContext = mContext;
		this.code = question.getCode();
		this.type = question.getType();
		this.format = question.getFormat();
		this.answer = question.getAnswer();
		this.location = question.isLocation();
		this.relation = question.getRelation();
		this.locationKey = locationKey;
		this.locationValue = locationValue;
		this.locationType = LocationType.String;
		this.xml = question;
	}

	public BaseQuestion(Context mContext, XmlQuestion question)	{
		this.mContext = mContext;
		db = new Database(mContext);
		this.code = question.getCode();
		this.type = question.getType();
		this.format = question.getFormat();
		this.answer = question.getAnswer();
		this.location = question.isLocation();
		this.relation = question.getRelation();
		this.locationType = LocationType.None;
		this.xml = question;
		db.queryDatabase();
	}

	
	//Converts coordinates to country
	public Map<String, String> getAddress(double lat, double lng) {
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		Map<String, String> location = new HashMap<String, String>();
		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			Address obj = addresses.get(0);
			location.put("COUNTRY", obj.getCountryCode());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return location;
	}

	private DbRow selectRowFromDb(String where, String[] whereArgs) throws QuestionModuleException {

		Cursor cursor = db.select(where, whereArgs, null, null);
		DbRow dbRow;
		cursor.moveToFirst();
		if(cursor.moveToFirst())
		{
			Log.e("Cursor", "All Good");
		}
		else
		{
			Log.e("Cursor", "Error");
		}
		if(cursor.getCount() == 0)
		{
			throw new QuestionModuleException("No rows selected for " + where);
		}
		if(cursor.getCount() == 1)
		{
			//0,1,2 represents the order of column in the table
			//like name is the second column so used 1
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			float lat = cursor.getFloat(2);
			float lng = cursor.getFloat(3);
			String code = cursor.getString(4);
			String country_code = cursor.getString(5);
			String capital = cursor.getString(6);
			String country = cursor.getString(7);
			String state = cursor.getString(8);
			String continent = cursor.getString(9);
			int population = cursor.getInt(10);
			int elevation = cursor.getInt(11);
			dbRow = new DbRow(id, lng, lat, name, code, country_code, country, capital, state, continent, population, elevation);
		}
		else
		{
			//Get a random row between 0 to cursor.getcount and check if it has been used before
			dbRow = getRandomRow(cursor);
		}
		return dbRow;
	}

	private DbRow getRandomRow(Cursor cursor) {
		Random random = new Random();
		int pos = random.nextInt(cursor.getCount());
		if(cursor.move(pos))
		{
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			float lat = cursor.getFloat(2);
			float lng = cursor.getFloat(3);
			String code = cursor.getString(4);
			String country_code = cursor.getString(5);
			String capital = cursor.getString(6);
			String country = cursor.getString(7);
			String state = cursor.getString(8);
			String continent = cursor.getString(9);
			int population = cursor.getInt(10);
			int elevation = cursor.getInt(11);
			DbRow dbRow = new DbRow(id, lng, lat, name, code, country_code, country, capital, state, continent, population, elevation);
			return dbRow;
		}
		else
		{
			//throw error 
			Log.d("Error", "Cursor error");
			return null;
		}

	}
	
	
	//Logic for creating question from DbRow and XmlQuestion 
	public GeneratedQuestion makeQuestion() throws QuestionModuleException {

		String where;
		String[] whereArgs;
		DbRow dbRow;
		GeneratedQuestion genQues = null;
		if(locationType == LocationType.Coordiates)
		{
			whereArgs = new String[2];
			Map<String, String> map = getAddress(lat, lng);
			String key = (String) map.keySet().iterator().next();
			where = "code = ? AND " + key + "=?";
			whereArgs[0] = code;
			whereArgs[1] = (String) map.get(key);
			dbRow = selectRowFromDb(where, whereArgs);
		}
		else if(locationType == LocationType.String)
		{
			whereArgs = new String[2];
			where = "code=? AND " + locationKey + "=?";
			whereArgs[0] = code;
			whereArgs[1] = locationValue;
			dbRow = selectRowFromDb(where, whereArgs);
		}
		else if(locationType == LocationType.None)
		{
			whereArgs = new String[1];
			where = "code=?";
			whereArgs[0] = code;
			Log.e("CODE", whereArgs[0]);
			dbRow = selectRowFromDb(where, whereArgs);
		}
		else
		{
			throw new QuestionModuleException("Invalid locationType in BaseQuestion");
		}
		String x = dbRow.getName();
		String y = null;
		String answer = null;
		y = "";
		if(relation.equals("population"))
		{
			y = "population";
			answer = String.valueOf(dbRow.getPopulation());
		}
		else if(relation.equals("country"))
		{
			x = dbRow.getCountry();
		}
		else if(relation.equals("state"))
		{
			//Need to implement
			x = "";
		}
		else if(relation.equals("location"))
		{
			x = String.valueOf(dbRow.getLat() + "," + dbRow.getLng());
			
		}
		else if(relation.equals("elevation"))
		{
			x = String.valueOf(dbRow.getElevation());
			
		}
		else if(relation.equals("population"))
		{
			x = String.valueOf(dbRow.getPopulation());
		}
		
		//Answer
		if(this.answer.equals("country"))
		{
			answer = dbRow.getCountry();
		}
		else if(this.answer.equals("state"))
		{
			answer = dbRow.getName();
		}
		else if(this.answer.equals("name"))
		{
			answer = dbRow.getName();
		}
		else if(this.answer.equals("location"))
		{
			answer = String.valueOf(dbRow.getLat() + "," + dbRow.getLng());
		}
		else if(this.answer.equals("population"))
		{
			answer = String.valueOf(dbRow.getPopulation());
		}
		else if(this.answer.equals("elevation"))
		{
			answer = String.valueOf(dbRow.getElevation());
		}
		String format;
		format = this.format;
		format = format.replace(":X:", x);
		format = format.replace(":relationship:", y);
		Log.d("QUESTION", format);
		Log.d("ANSWER", answer);
		String[] questionAnswer = new String[2];
		questionAnswer[0] = format;
		questionAnswer[1] = answer;
		
		if(type ==  Type.MultipleChoiceQuestion)
		{
			//Make options
			genQues = new GeneratedQuestion(dbRow, xml, format, answer, questionAnswer);
		}
		else if(type ==  Type.FillBlanks)
		{
			genQues = new GeneratedQuestion(dbRow, xml, format, answer, GeneratedQuestion.Type.Fill);
		}
		else if(type == Type.PinOnMap)
		{
			genQues = new GeneratedQuestion(dbRow, xml, format, answer, GeneratedQuestion.Type.Pin);
		}
		return genQues;
	}


}
