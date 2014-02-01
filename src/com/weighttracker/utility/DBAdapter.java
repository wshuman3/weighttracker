package com.weighttracker.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	public static final String KEY_ROWID = "_id";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_DATE = "date";   
    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "weighttracker";
    private static final String DATABASE_TABLE = "weights";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
        "create table weights (" +
        "_id integer primary key autoincrement, " +
        "weight double not null, " +
        "date date not null unique);";
        
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS weights");
            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a title into the database---
    public long insertWeight(BigDecimal weight, String date) 
    {
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WEIGHT, weight.doubleValue());
        initialValues.put(KEY_DATE, date);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteWeight(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllWeights(int maxRows, String order) 
    {
    	String limit = Integer.toString(maxRows);
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_WEIGHT,
        		KEY_DATE}, 
                null, 
                null, 
                null, 
                null, 
                KEY_ROWID + " " + order,
                limit);
    }

    //---retrieves a particular title---
    public Cursor getWeight(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_WEIGHT, 
                		KEY_DATE
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
  //---retrieves a particular title---
    public BigDecimal getWeightForDate(String date) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_WEIGHT, 
                		KEY_DATE
                		}, 
                		KEY_DATE + "='" + date + "'", 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            if(mCursor.moveToFirst()){
            	BigDecimal weight = new BigDecimal(mCursor.getDouble(mCursor.getColumnIndex(KEY_WEIGHT)));
            	weight = weight.setScale(2, RoundingMode.HALF_DOWN);
                return weight;
            }
        }
        return null;
    }

    //---updates a title---
    public boolean updateWeight(long rowId, double weight, String date) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_WEIGHT, weight);
        args.put(KEY_DATE, date);
        return db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }	
	
}