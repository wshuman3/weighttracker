package com.wshuman3.weighttracker;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.wshuman3.weighttracker.utility.Utility;


public class WeightTrackerActivity extends ActionBarActivity  {
	
	private ViewPager pager;
	ActionBar actionBar;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	actionBar = getSupportActionBar();
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	pager = (ViewPager) findViewById(R.id.pager);
    	FragmentManager fm = getSupportFragmentManager();
    	
    	ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        };
        
        pager.setOnPageChangeListener(pageChangeListener);
        WTFragmentPagerAdapter fragmentPagerAdapter = new WTFragmentPagerAdapter(fm);
        pager.setAdapter(fragmentPagerAdapter);
        actionBar.setDisplayShowTitleEnabled(true);
 
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };
 
        Tab tab = actionBar.newTab()
            .setText(R.string.weighin_name)
            .setTabListener(tabListener);
        actionBar.addTab(tab);
 
        tab = actionBar.newTab()
            .setText(R.string.graph_name)
            .setTabListener(tabListener);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
            .setText(R.string.statistics_name)
            .setTabListener(tabListener);
        actionBar.addTab(tab);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	    	case R.id.file_backup_item:
	    		if(!Utility.saveWeightFiletoSD()){
	    			Utility.displayAlertDialog(this, "Backup Unsuccessful","Could not create your weight data file.");
	    		}
	    		return true;
	    	case R.id.backup_item:
	    		if(!Utility.saveWeightDatatoSD()) {
	    			Utility.displayAlertDialog(this, "Backup Unsuccessful","Could not back up your weight data.");
	    		}
	    		return true;
	    	case R.id.import_item:
	    		if(!Utility.importWeightDatafromSD()) {
	    			Utility.displayAlertDialog(this, "Import Unsuccessful","Could not import your weight data.");
	    		}
	    		return true;
        }
        return false;
    }
    

  

}