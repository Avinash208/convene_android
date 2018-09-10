package org.fwwb.convene.convenecode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.SurveysBean;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.fragments.DataFormFragment;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class Beneficiarylinkages extends AppCompatActivity {

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
    private String isBeneficiaryTypeLinkage = "";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;
    private List<SurveysBean> surveyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiarylinkages);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        ImageView imageMenu = findViewById(R.id.imageMenu);
        LinearLayout backPress = findViewById(R.id.backPress);

        imageMenu.setVisibility(View.GONE);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        Tabthree = (TabItem) findViewById(R.id.tabItem3);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        isBeneficiaryTypeLinkage = isBeneficiaryTypeLinkage();
        surveyList = dbOpenHelper.getTypeBasedSurvey(String.valueOf(prefs.getInt("survey_id", 0)));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        getPreviousFromIntent(toolbarTitle);
        setOnClick(backPress);

    }

    private void getPreviousFromIntent(TextView toolbarTitle) {
        Intent intent = getIntent();
        String headerName = intent.getStringExtra(Constants.HEADER_NAME);
        Logger.logD("Listing heading Name", headerName);

        if (headerName != null && !headerName.isEmpty()) {
            toolbarTitle.setText(headerName);
        } else {
            toolbarTitle.setText("Beneficiary linkage");
        }

    }

    private void setOnClick(LinearLayout backPress) {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private String isBeneficiaryTypeLinkage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        isBeneficiaryTypeLinkage = dbOpenHelper.getGroupIds(prefs.getInt("survey_id", 0), dbOpenHelper);
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

        private String beneficiaryWithLinkage[] = {getResources().getString(R.string.tab_text_2), getResources().getString(R.string.tab_text_1), getResources().getString(R.string.tab_text_3)};
        private String beneficiaryWithoutActivity[] = { getResources().getString(R.string.tab_text_1), getResources().getString(R.string.tab_text_3)};
        private String beneficiaryWithOUTLinkage[] = {getResources().getString(R.string.tab_text_2), getResources().getString(R.string.tab_text_1)};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (surveyList.isEmpty())
                return beneficiaryWithoutActivity[position];
            if (isBeneficiaryTypeLinkage.equals(""))
                return beneficiaryWithOUTLinkage[position];
            else
                return beneficiaryWithLinkage[position];
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a BeneficiaryLinkageDetails (defined as a static inner class below).

            if (surveyList.isEmpty())
            {
                if (position == 0) {
                    return BeneficiaryLinkageDetails.newInstance(position + 1);
                } else if (position == 1) {
                    return BeneficiaryLinkageActivityFragment.newInstance(position + 1);

                }
            }
            if (position == 0) {
                Bundle bundle = new Bundle();
                DataFormFragment dataFormFragment = new DataFormFragment();
                dataFormFragment.setArguments(bundle);
                return dataFormFragment;
            } else if (position == 1) {
                return BeneficiaryLinkageDetails.newInstance(position + 1);

            } else if (position == 2)
                return BeneficiaryLinkageActivityFragment.newInstance(position + 1);
            else
                return BeneficiaryLinkageDetails.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (surveyList.isEmpty())
                return beneficiaryWithoutActivity.length;
            if (isBeneficiaryTypeLinkage.equals(""))
                return beneficiaryWithOUTLinkage.length;
            else
                return beneficiaryWithLinkage.length;

        }
    }
}
