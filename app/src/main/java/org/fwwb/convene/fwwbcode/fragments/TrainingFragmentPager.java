package org.fwwb.convene.fwwbcode.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fwwb.convene.convenecode.utils.Logger;

import java.util.List;

/**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TrainingFragmentPager extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList;

    public TrainingFragmentPager(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
        this.fragmentList = fragmentList;
        Logger.logV("size",""+ fragmentList.size());
    }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a CurrentTaskListFragment (defined as a static inner class below).
            return fragmentList.get(position);
//            if (position == 0)
//                return CurrentTaskListFragment.newInstance();
//            else return UpcomingTaskListFragment.newInstance();

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragmentList.size();
        }
    }