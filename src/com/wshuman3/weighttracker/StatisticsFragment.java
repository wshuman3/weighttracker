package com.wshuman3.weighttracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.wshuman3.weighttracker.utility.DBAdapter;

public class StatisticsFragment extends Fragment {
	
  
	private LinearLayout view;
	List<Date> dates = new ArrayList<Date>();
	List<BigDecimal> weights = new ArrayList<BigDecimal>();
	Map<String, BigDecimal> dateWeightMap = new HashMap<String, BigDecimal>();
	List<String> monthlyChanges = new ArrayList<String>();
	List<String> weightChanges = new ArrayList<String>();
	String currentView;
	GestureLibrary gestureLibrary;
	ViewFlipper viewFlipper;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) 
        	return null;
        view = (LinearLayout) inflater.inflate(R.layout.statistics, container, false);
        refreshData();
        return view;
    }
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			refreshData();
		}
	}
	
    private synchronized void refreshData() {
    	
    	this.dates = new ArrayList<Date>();
    	this.weights = new ArrayList<BigDecimal>();
    	this.dateWeightMap = new HashMap<String, BigDecimal>();
    	this.weightChanges = new ArrayList<String>();
		this.monthlyChanges = new ArrayList<String>();
	    	
    	new Thread() {
        	public void run() {
        		weightChanges = getWeightStatistics();
        		monthlyChanges = getMonthStatistics();
        		refreshHandler.sendEmptyMessage(0);
        	}
        }.start();
		
	}
    
    private Handler refreshHandler = new Handler() {

    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    			updateUIWeightStatistics();
    	}
    };

    private synchronized void updateUIWeightStatistics() {
    	if (!weightChanges.isEmpty()) {
    		((TextView)view.findViewById(R.id.DailyChangeResult)).setText(weightChanges.get(0));
            ((TextView)view.findViewById(R.id.WeekChangeResult)).setText(weightChanges.get(1));
            ((TextView)view.findViewById(R.id.TwoWeekChangeResult)).setText(weightChanges.get(2));
            ((TextView)view.findViewById(R.id.MonthChangeResult)).setText(weightChanges.get(3));
            ((TextView)view.findViewById(R.id.TwoMonthChangeResult)).setText(weightChanges.get(4));
            ((TextView)view.findViewById(R.id.ThreeMonthChangeResult)).setText(weightChanges.get(5));
            ((TextView)view.findViewById(R.id.SixMonthChangeResult)).setText(weightChanges.get(6));
            ((TextView)view.findViewById(R.id.TotalChangeResult)).setText(weightChanges.get(7));
            ((TextView)view.findViewById(R.id.JanuaryChange)).setText(monthlyChanges.get(0));
    		((TextView)view.findViewById(R.id.FebruaryChange)).setText(monthlyChanges.get(1));
    		((TextView)view.findViewById(R.id.MarchChange)).setText(monthlyChanges.get(2));
    		((TextView)view.findViewById(R.id.AprilChange)).setText(monthlyChanges.get(3));
    		((TextView)view.findViewById(R.id.MayChange)).setText(monthlyChanges.get(4));
    		((TextView)view.findViewById(R.id.JuneChange)).setText(monthlyChanges.get(5));
    		((TextView)view.findViewById(R.id.JulyChange)).setText(monthlyChanges.get(6));
    		((TextView)view.findViewById(R.id.AugustChange)).setText(monthlyChanges.get(7));
    		((TextView)view.findViewById(R.id.SeptemberChange)).setText(monthlyChanges.get(8));
    		((TextView)view.findViewById(R.id.OctoberChange)).setText(monthlyChanges.get(9));
    		((TextView)view.findViewById(R.id.NovemberChange)).setText(monthlyChanges.get(10));
    		((TextView)view.findViewById(R.id.DecemberChange)).setText(monthlyChanges.get(11));
    	}
    }    

	private List<String> getWeightStatistics() {
		
		List<String> weightChangesList = new ArrayList<String>();
		
		for(int i = 0; i < 8; i++) {
			weightChangesList.add("");
		}
		
		if(this.dateWeightMap == null || this.dateWeightMap.isEmpty()) {
			getDateWeightMap();
		}

       Calendar calcCalendar = Calendar.getInstance();
        
        Date today = Calendar.getInstance().getTime();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        
        BigDecimal todaysWeight = dateWeightMap.get(new SimpleDateFormat("MM/dd/yyyy").format(today));
        
        //get dates for calculation
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.DAY_OF_YEAR, -1);
        Date minusOneDay = calcCalendar.getTime();
        BigDecimal minusOneDayWeight = getClosestWeight(minusOneDay, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        Date minusOneWeek = calcCalendar.getTime();
        BigDecimal minusOneWeekWeight = getClosestWeight(minusOneWeek, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.WEEK_OF_YEAR, -2);
        Date minusTwoWeeks = calcCalendar.getTime();
        BigDecimal minusTwoWeeksWeight = getClosestWeight(minusTwoWeeks, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.MONTH, -1);
        Date minusOneMonth = calcCalendar.getTime();
        BigDecimal minusOneMonthWeight = getClosestWeight(minusOneMonth, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.MONTH, -2);
        Date minusTwoMonths = calcCalendar.getTime();
        BigDecimal minusTwoMonthsWeight = getClosestWeight(minusTwoMonths, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.MONTH, -3);
        Date minusThreeMonths = calcCalendar.getTime();
        BigDecimal minusThreeMonthsWeight = getClosestWeight(minusThreeMonths, today);
        
        calcCalendar.setTime(today);
        calcCalendar.add(Calendar.MONTH, -6);
        Date minusSixMonths = calcCalendar.getTime();
        BigDecimal minusSixMonthsWeight = getClosestWeight(minusSixMonths, today);
                             
        if(minusOneDayWeight != null && todaysWeight != null){
        	BigDecimal dailyChangeResult = todaysWeight.subtract(minusOneDayWeight);
        	weightChangesList.set(0, dailyChangeResult.toString());
        }
        if(minusOneWeekWeight != null && todaysWeight != null){
        	BigDecimal weekChangeResult = todaysWeight.subtract(minusOneWeekWeight);
        	weightChangesList.set(1, weekChangeResult.toString());
        }
        if(minusTwoWeeksWeight != null && todaysWeight != null){
        	BigDecimal twoWeekChangeResult = todaysWeight.subtract(minusTwoWeeksWeight);
        	weightChangesList.set(2, twoWeekChangeResult.toString());
        }
        if(minusOneMonthWeight != null && todaysWeight != null){
        	BigDecimal monthChangeResult = todaysWeight.subtract(minusOneMonthWeight);
        	weightChangesList.set(3, monthChangeResult.toString());
        }
        if(minusTwoMonthsWeight != null && todaysWeight != null){
        	BigDecimal twoMonthChangeResult = todaysWeight.subtract(minusTwoMonthsWeight);
        	weightChangesList.set(4, twoMonthChangeResult.toString());
        }
        if(minusThreeMonthsWeight != null && todaysWeight != null){
        	BigDecimal threeMonthChangeResult = todaysWeight.subtract(minusThreeMonthsWeight);
        	weightChangesList.set(5, threeMonthChangeResult.toString());
        }
        if(minusSixMonthsWeight != null && todaysWeight != null){
        	BigDecimal sixMonthChangeResult = todaysWeight.subtract(minusSixMonthsWeight);
        	weightChangesList.set(6, sixMonthChangeResult.toString());
        }       
        if(!weights.isEmpty()) {
        	BigDecimal totalChangeResult = weights.get(0).subtract(weights.get(weights.size()-1));
        	weightChangesList.set(7, totalChangeResult.toString());
        }  
        
        return weightChangesList;
	}
    
	private void getDateWeightMap() {
		DBAdapter db = new DBAdapter(getActivity());
		db.open();        
		Cursor weightsCursor = db.getAllWeights(1000, "desc");
		
		while(weightsCursor.moveToNext()){
			
			try {
				
	        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	        	Date date = sdf.parse(weightsCursor.getString(weightsCursor.getColumnIndex("date")));
	        	date.setHours(0);
	        	date.setMinutes(0);
	        	date.setSeconds(0);
	        	BigDecimal weight =  new BigDecimal(weightsCursor.getDouble(weightsCursor.getColumnIndex("weight")));
	        	weight = weight.setScale(2, RoundingMode.HALF_DOWN);
	        	this.dates.add(date);
	        	this.weights.add(weight);
		        	
			} catch (ParseException e) {
				//could not parse the date from the DB
				e.printStackTrace();	
			}
        } 
        
		db.close();
	        
        for(int i=0; i < dates.size(); i++) {
        	this.dateWeightMap.put(new SimpleDateFormat("MM/dd/yyyy").format(dates.get(i)), weights.get(i));
        }	
	}

	private List<String> getMonthStatistics() {
		
		List<String> monthlyChangesList = new ArrayList<String>();
		 
		for (int i = 0; i < 12; i++) {
			monthlyChangesList.add("");
		}
		 
		if(this.dateWeightMap == null || this.dateWeightMap.isEmpty()) {
			getDateWeightMap();
		}
		 
		for(int month = 0; month < 12; month++) {
			 
			BigDecimal lastWeightofMonth = null;
			BigDecimal firstWeightofMonth = null;
			 
			Calendar calcCalendar = Calendar.getInstance();
			int currentYear = calcCalendar.get(Calendar.YEAR);
			calcCalendar.set(Calendar.YEAR, currentYear);
			calcCalendar.set(Calendar.MONTH, month);
			int maxDayofMonth = calcCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			 
			for(int days = 1; days <= maxDayofMonth; days++) {
				
				calcCalendar.set(Calendar.DAY_OF_MONTH, days);
				String currentCheckDate = new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime());
				
				if(dateWeightMap.get(currentCheckDate) != null){
					
					firstWeightofMonth = dateWeightMap.get(new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime()));
					break;
					
				}
				
			}
			 
			for(int days = maxDayofMonth; days > 0; days--){
				
				calcCalendar.set(Calendar.DAY_OF_MONTH, days);
				String currentCheckDate = new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime());
 
				if(dateWeightMap.get(currentCheckDate) != null){
	 
					lastWeightofMonth = dateWeightMap.get(new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime()));
					break;
				}
			}
			 
			if(lastWeightofMonth != null && firstWeightofMonth != null) {
				 
				BigDecimal monthlyDifference = lastWeightofMonth.subtract(firstWeightofMonth);
				 
				if(month == 0) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 1) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 2) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 3) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 4) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 5) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 6) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 7) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 8) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 9) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 10) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				} else if(month == 11) {
					monthlyChangesList.set(month, monthlyDifference.toString());
				}
			} 
		}
		 
		return monthlyChangesList;
	 
	}
	 
	private BigDecimal getClosestWeight(Date calculationDate, Date today) {
		
		Calendar calcCalendar = Calendar.getInstance();
		BigDecimal returnWeight = null;
		 
		while(!calculationDate.equals(today)) {
			
			calcCalendar.setTime(calculationDate);
			String currentCheckDate = new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime());
			
			if(dateWeightMap.get(currentCheckDate) != null){
				return returnWeight = dateWeightMap.get(new SimpleDateFormat("MM/dd/yyyy").format(calcCalendar.getTime()));
			}
			
			calcCalendar.add(Calendar.DAY_OF_YEAR, 1);
			calculationDate = calcCalendar.getTime();
		}
		 
		return returnWeight;
	 }

	 
}