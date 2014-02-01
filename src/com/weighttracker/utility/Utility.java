package com.weighttracker.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.text.format.DateFormat;

import com.weighttracker.MyApplication;

public class Utility {
	
	/**
	 * This Method will import the backedup weight DB from the SD card.
	 * It will place it in the user data DB location.
	 * It will return true for success and false for failure.
	 * 
	 * @return boolean
	 */
	public static boolean importWeightDatafromSD() {
		
    	try {

    		File sd = Environment.getExternalStorageDirectory();
    		File data = Environment.getDataDirectory();

    		String applicationDBPath = "/data/com.weighttracker/databases/weighttracker";
    		String backupDBPath = "com.weighttracker/weighttracker";

    		File currentDB = new File(data, applicationDBPath);
    		File backupDB = new File(sd, backupDBPath);

    		if (backupDB.exists()) {
	    		FileInputStream fileInputStream = new FileInputStream(backupDB);
				FileChannel src = fileInputStream.getChannel();
	    		FileOutputStream fileOutputStream = new FileOutputStream(currentDB);
				FileChannel dst = fileOutputStream.getChannel();
	    		dst.transferFrom(src, 0, src.size());
	    		src.close();
	    		dst.close();
	    		fileInputStream.close();
	    		fileOutputStream.close();
	    		return true;
    		} else {
    			//no DB to import from
    			return false;
    		}

    	} catch (Exception e) {
    		//could not backup data
    		return false;
    	}
	}

	/**
	 * This method will backup the weight DB to the SD card.
	 * It will return true is succesfull or false if there is a failure
	 * 	
	 * @return boolean
	 */
	public static boolean saveWeightDatatoSD() {
		
    	try {

    		File sd = Environment.getExternalStorageDirectory();
    		File data = Environment.getDataDirectory();

    		if (sd.canWrite()) {

	    		String currentDBPath = "/data/com.weighttracker/databases/weighttracker";
	    		
	
	    		File currentDB = new File(data, currentDBPath);
	    		File backupDBPath = new File(sd, "com.weighttracker");
	    		
	    		if (!backupDBPath.exists()) {
	    			backupDBPath.mkdirs();
	    		}
	    		
	    		File backupDB = new File(backupDBPath, "weighttracker");
	    		
	    		if(backupDB.exists()) {
	    			backupDB.delete();
	    		}
	
	    		if (currentDB.exists()) {
		    		FileInputStream fileInputStream = new FileInputStream(currentDB);
					FileChannel src = fileInputStream.getChannel();
		    		FileOutputStream fileOutputStream = new FileOutputStream(backupDB);
					FileChannel dst = fileOutputStream.getChannel();
		    		dst.transferFrom(src, 0, src.size());
		    		src.close();
		    		dst.close();
		    		fileInputStream.close();
		    		fileOutputStream.close();
		    		return true;
	    		} else {
	    			//current DB does not exists
	    			return false;
	    		}
	    		
    		} else {
    			//can't write to SD Card
    			return false;
    		}
	
    	} catch (Exception e) {
    		//could not backup data
    		return false;
    	}
		
	}

	/**
	 * This method will create a CSV file that contains the weight and date
	 * data from the active DB.  It will return true if it was successful
	 * and false if there is a failure
	 * 
	 * @return boolean
	 */
	public static boolean saveWeightFiletoSD() {
		
    	ArrayList<Date> dates = new ArrayList<Date>();
    	ArrayList<BigDecimal> weights = new ArrayList<BigDecimal>();
    	
    	DBAdapter db = new DBAdapter(MyApplication.getAppContext());
        db.open();        
        Cursor weightsCursor = db.getAllWeights(356, "desc");
        while(weightsCursor.moveToNext()){
			try {
	        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	        	Date date = sdf.parse(weightsCursor.getString(weightsCursor.getColumnIndex("date")));
	        	BigDecimal weight =  new BigDecimal(weightsCursor.getDouble(weightsCursor.getColumnIndex("weight")));
	        	weight = weight.setScale(2, RoundingMode.HALF_DOWN);
	        	dates.add(date);
	        	weights.add(weight);

	        	
			} catch (ParseException e) {
				//couldn't parse date fom DB
				return false;
			}
        }
        
    	Collections.reverse(dates);
    	Collections.reverse(weights);
        db.close();
    	
    	File sd = Environment.getExternalStorageDirectory();
		
		if (sd.canWrite()) {

    		File backupFilePath = new File(sd, "com.weighttracker");
    		
    		if (!backupFilePath.exists()) {
    			backupFilePath.mkdirs();
    		}
    		
    		File backupFile = new File(backupFilePath, "weighttracker.csv");
    		FileOutputStream out   =   null;
    		
    		if(backupFile.exists()) {
    			backupFile.delete();
    		}
    		
    		String columnString = "Date,Weight\n";
    		String dataString = "";
    		
    		String dateFormat = "MM/dd/yyyy";
    		
    		for(int i = 0; i < weights.size(); i++) {
    			dataString += DateFormat.format(dateFormat, dates.get(i)).toString() + "," + weights.get(i).toString() + "\n";
    		}
    		
    		String combinedString = columnString + dataString;
    		
    		try {
    			
    	        out = new FileOutputStream(backupFile);
    	        out.write(combinedString.getBytes());
    	        out.close();
    	        return true;
    	        
    		} catch (Exception e) {
    			//failed to backup data to file
    			return false;
    		}
    		
		} else {
			//can't write to SD
			return false;
		}
		
		
	}

	/**
	 * This method displays an alert dialog with the icon picture.
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void displayAlertDialog(Context context, String title, String text) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(text);
		alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK",new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
			   dialog.dismiss();
		   }
		});
		alertDialog.show();
	}
}
