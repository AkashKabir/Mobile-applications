package com.buildmlearn.labeldiagram;

import com.example.labelthediagram.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class DiagramMenu extends FragmentActivity{

	
	FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_menu);
        ViewPager vpPager = (ViewPager) findViewById(R.id.diagrampager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setClipToPadding(false);
        vpPager.setPageMargin(20);
    }
    
    
    
	
	public static class MyPagerAdapter extends FragmentPagerAdapter {
		//pass number of fragments here
	    private static int NUM_ITEMS = 5;

	        public MyPagerAdapter(FragmentManager fragmentManager) {
	            super(fragmentManager);
	        }

	        // Returns total number of pages
	        @Override
	        public int getCount() {
	            return NUM_ITEMS;
	        }

	        // Returns the fragment to display for that page
	        @Override
	        public Fragment getItem(int position) {
	            switch (position) {
	            case 0:
	                return DiagramFragment.getInstance(1);
	            case 1:
	                return DiagramFragment.getInstance(2);
	            case 2:
	                return DiagramFragment.getInstance(3);
	            case 3:
	                return DiagramFragment.getInstance(4);
	            case 4:
	                return DiagramFragment.getInstance(5);
	       
	            default:
	                return null;
	            }
	        }

	        @Override
	        public CharSequence getPageTitle(int position) {
	            return "Page " + position;
	        }
	        
	        @Override
	        public float getPageWidth (int position) {
	            return 0.90f;
	        }   

	    }

	
}
