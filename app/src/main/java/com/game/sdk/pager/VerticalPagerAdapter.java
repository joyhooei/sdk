package com.game.sdk.pager;

import java.util.ArrayList;
import java.util.List;

import com.game.sdk.util.Logger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class VerticalPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;
	

	public VerticalPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	
	public VerticalPagerAdapter(FragmentManager fm,List<Fragment> oneListFragments){
		super(fm);
		this.fragments=oneListFragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		Logger.msg("-------------"+arg0);
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		Logger.msg("--------2222-----"+object);
		return super.getItemPosition(object);
	}

}
