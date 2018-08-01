package com.alfanthariq.broadcastpromofirebase.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.alfanthariq.broadcastpromofirebase.fragment.FragmentActivePromo;
import com.alfanthariq.broadcastpromofirebase.fragment.FragmentCoomingPromo;

import java.util.ArrayList;

/**
 * Created by alfanthariq on 29/07/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        mNumOfTabs = fragments.size();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                //FragmentActivePromo frag = (FragmentActivePromo) fragments.get(0);
                return fragments.get(0);
            case 1:
                //FragmentCoomingPromo frag1 = (FragmentCoomingPromo) fragments.get(0);
                return fragments.get(1);
            case 2:
                //FragmentCoomingPromo frag1 = (FragmentCoomingPromo) fragments.get(0);
                return fragments.get(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public float getPageWidth(int position) {
        // TODO Auto-generated method stub
        if (position == 0) {
            return .85f;
        }
        return 1f;
    }
}
