package com.weighttracker;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.weighttracker.utility.DBAdapter;
import com.weighttracker.R;

public class StatisticsFragment extends Fragment {
	
	private ScrollView view;
	private List<Date> dates = new ArrayList<Date>();
	private List<BigDecimal> weights = new ArrayList<BigDecimal>();
	private Map<String, BigDecimal> dateWeightMap = new HashMap<String, BigDecimal>();
	private	List<BigDecimal> monthlyChanges = new ArrayList<BigDecimal>();
	private List<BigDecimal> weightChanges = new ArrayList<BigDecimal>();
	private BigDecimal zero = new BigDecimal(0.00);
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) 
        	return null;
        view = (ScrollView) inflater.inflate(R.layout.statistics, container, false);
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
    	this.weightChanges = new ArrayList<BigDecimal>();
		this.monthlyChanges = new ArrayList<BigDecimal>();
	    	
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
    		// basic
    		((TextView)view.findViewById(R.id.DailyChangeResult)).setText(getString(weightChanges.get(0)));
    		if (weightChanges.get(0).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.DailyChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(0).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.DailyChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.WeekChangeResult)).setText(getString(weightChanges.get(1)));
    		if (weightChanges.get(1).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.WeekChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(1).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.WeekChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.TwoWeekChangeResult)).setText(getString(weightChanges.get(2)));
    		if (weightChanges.get(2).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.TwoWeekChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(2).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.TwoWeekChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.MonthChangeResult)).setText(getString(weightChanges.get(3)));
    		if (weightChanges.get(3).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.MonthChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(3).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.MonthChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.TwoMonthChangeResult)).setText(getString(weightChanges.get(4)));
    		if (weightChanges.get(4).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.TwoMonthChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(4).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.TwoMonthChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.ThreeMonthChangeResult)).setText(getString(weightChanges.get(5)));
    		if (weightChanges.get(5).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.ThreeMonthChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(5).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.ThreeMonthChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.SixMonthChangeResult)).setText(getString(weightChanges.get(6)));
    		if (weightChanges.get(6).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.SixMonthChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(6).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.SixMonthChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.TotalChangeResult)).setText(getString(weightChanges.get(7)));
    		if (weightChanges.get(7).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.TotalChangeResult)).setTextColor(getResources().getColor(R.color.red));
    		else if (weightChanges.get(7).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.TotalChangeResult)).setTextColor(getResources().getColor(R.color.green));
    		
    		// months
    		 ((TextView)view.findViewById(R.id.JanuaryChange)).setText(getString(monthlyChanges.get(0)));
    		if (monthlyChanges.get(0).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.JanuaryChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(0).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.JanuaryChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.FebruaryChange)).setText(getString(monthlyChanges.get(1)));
    		if (monthlyChanges.get(1).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.FebruaryChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(1).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.FebruaryChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.MarchChange)).setText(getString(monthlyChanges.get(2)));
    		if (monthlyChanges.get(2).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.MarchChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(2).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.MarchChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.AprilChange)).setText(getString(monthlyChanges.get(3)));
    		if (monthlyChanges.get(3).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.AprilChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(3).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.AprilChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.MayChange)).setText(getString(monthlyChanges.get(4)));
    		if (monthlyChanges.get(4).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.MayChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(4).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.MayChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.JuneChange)).setText(getString(monthlyChanges.get(5)));
    		if (monthlyChanges.get(5).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.JuneChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(5).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.JuneChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.JulyChange)).setText(getString(monthlyChanges.get(6)));
    		if (monthlyChanges.get(6).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.JulyChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(6).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.JulyChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.AugustChange)).setText(getString(monthlyChanges.get(7)));
    		if (monthlyChanges.get(7).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.AugustChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(7).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.AugustChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.SeptemberChange)).setText(getString(monthlyChanges.get(8)));
    		if (monthlyChanges.get(8).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.SeptemberChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(8).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.SeptemberChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.OctoberChange)).setText(getString(monthlyChanges.get(9)));
    		if (monthlyChanges.get(9).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.OctoberChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(9).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.OctoberChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.NovemberChange)).setText(getString(monthlyChanges.get(10)));
    		if (monthlyChanges.get(10).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.NovemberChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(10).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.NovemberChange)).setTextColor(getResources().getColor(R.color.green));
    		
    		((TextView)view.findViewById(R.id.DecemberChange)).setText(getString(monthlyChanges.get(11)));
    		if (monthlyChanges.get(11).compareTo(zero) < 0)
    			((TextView)view.findViewById(R.id.DecemberChange)).setTextColor(getResources().getColor(R.color.red));
    		else if (monthlyChanges.get(11).compareTo(zero) > 0)
    			((TextView)view.findViewById(R.id.DecemberChange)).setTextColor(getResources().getColor(R.color.green));
    	}
    } 
    
    private String getString(BigDecimal decimal) {
    	return (decimal == null) ? "" : decimal.toString(); 
    }

	private List<BigDecimal> getWeightStatistics() {
		
		List<BigDecimal> weightChangesList = new ArrayList<BigDecimal>();
		
		for(int i = 0; i < 8; i++) {
			weightChangesList.add(zero);
		}
		
		if(this.dateWeightMap == null || this.dateWeightMap.isEmpty()) {
			getDateWeightMap();
		}

		Calendar calcCalendar = Calendar.getInstance();
        
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(Calendar.HOUR_OF_DAY, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.SECOND, 0);
		Date today = todayCal.getTime();
        
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
        	weightChangesList.set(0, dailyChangeResult);
        }
        if(minusOneWeekWeight != null && todaysWeight != null){
        	BigDecimal weekChangeResult = todaysWeight.subtract(minusOneWeekWeight);
        	weightChangesList.set(1, weekChangeResult);
        }
        if(minusTwoWeeksWeight != null && todaysWeight != null){
        	BigDecimal twoWeekChangeResult = todaysWeight.subtract(minusTwoWeeksWeight);
        	weightChangesList.set(2, twoWeekChangeResult);
        }
        if(minusOneMonthWeight != null && todaysWeight != null){
        	BigDecimal monthChangeResult = todaysWeight.subtract(minusOneMonthWeight);
        	weightChangesList.set(3, monthChangeResult);
        }
        if(minusTwoMonthsWeight != null && todaysWeight != null){
        	BigDecimal twoMonthChangeResult = todaysWeight.subtract(minusTwoMonthsWeight);
        	weightChangesList.set(4, twoMonthChangeResult);
        }
        if(minusThreeMonthsWeight != null && todaysWeight != null){
        	BigDecimal threeMonthChangeResult = todaysWeight.subtract(minusThreeMonthsWeight);
        	weightChangesList.set(5, threeMonthChangeResult);
        }
        if(minusSixMonthsWeight != null && todaysWeight != null){
        	BigDecimal sixMonthChangeResult = todaysWeight.subtract(minusSixMonthsWeight);
        	weightChangesList.set(6, sixMonthChangeResult);
        }       
        if(!weights.isEmpty()) {
        	BigDecimal totalChangeResult = weights.get(0).subtract(weights.get(weights.size()-1));
        	weightChangesList.set(7, totalChangeResult);
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
	        	Calendar dateCal = Calendar.getInstance();
	        	dateCal.setTime(date);
	        	dateCal.set(Calendar.HOUR_OF_DAY, 0);
	        	dateCal.set(Calendar.MINUTE, 0);
	        	dateCal.set(Calendar.SECOND, 0);
	    		date = dateCal.getTime();
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

	private List<BigDecimal> getMonthStatistics() {
		
		List<BigDecimal> monthlyChangesList = new ArrayList<BigDecimal>();
		 
		for (int i = 0; i < 12; i++) {
			monthlyChangesList.add(zero);
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
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 1) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 2) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 3) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 4) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 5) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 6) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 7) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 8) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 9) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 10) {
					monthlyChangesList.set(month, monthlyDifference);
				} else if(month == 11) {
					monthlyChangesList.set(month, monthlyDifference);
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