package com.weighttracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WTFragmentPagerAdapter extends FragmentPagerAdapter {
	
	final int PAGE_COUNT = 3;
	 
    /** Constructor of the class */
    public WTFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int arg0) {
        switch(arg0){
            case 0:
                WeighInFragment weighInFragment = new WeighInFragment();
                return weighInFragment;
            case 1:
                GraphFragment graphFragment = new GraphFragment();
                return graphFragment;
            case 2:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                return statisticsFragment;
        }
        return null;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
