package org.mahiti.convenemis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.fragments.DataFormFragment;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

public class Beneficiarylinkages extends AppCompatActivity {

    private static final String TAG = "Beneficiarylinkages";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabItem Tabthree;
    ExternalDbOpenHelper dbOpenHelper;
    private SharedPreferences sharedPreferences;
    private String isBeneficiaryTypeLinkage="";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;
    private String BeneficiaryUUID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiarylinkages);
        getIntentParameters();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        Tabthree = (TabItem) findViewById(R.id.tabItem3);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        isBeneficiaryTypeLinkage=isBeneficiaryTypeLinkage();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    private void getIntentParameters() {
        try {
            Bundle bundle= getIntent().getExtras();
            if (bundle!=null)
                BeneficiaryUUID=bundle.getString("SurveyId");
        } catch (Exception e) {
           Logger.logE(TAG,"Exception in the getIntentParameters",e);
        }
    }

    private String isBeneficiaryTypeLinkage() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        isBeneficiaryTypeLinkage= dbOpenHelper.getGroupIds(prefs.getInt("survey_id", 0), dbOpenHelper);
        return isBeneficiaryTypeLinkage;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beneficiarylinkages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a BeneficiaryLinkageDetails (defined as a static inner class below).
            if (position == 0)
                return BeneficiaryLinkageDetails.newInstance(position + 1);
            else if (position == 1) {

                Bundle bundle = new Bundle();
                DataFormFragment dataFormFragment = new DataFormFragment();
                dataFormFragment.setArguments(bundle);
                return dataFormFragment;
            } else if (position == 2)
                return BeneficiaryLinkageActivityFragment.newInstance(position + 1);
            else
                return BeneficiaryLinkageDetails.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (isBeneficiaryTypeLinkage.equals(""))
                return 2;
            else
                return 3;

        }
    }
}
