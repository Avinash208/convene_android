package org.yale.convene.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.yale.convene.fragments.DataFormFragment;
import org.yale.convene.fragments.UserDetailsFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private Bundle bundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,  Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.bundle=bundle;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DataFormFragment dataFormFragment=new DataFormFragment();
                dataFormFragment.setArguments(bundle);
                return dataFormFragment;
            case 1:
                UserDetailsFragment userDetailsFragment=new UserDetailsFragment();
                userDetailsFragment.setArguments(bundle);
                return userDetailsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
