package org.mahiti.convenemis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.adapter.ExpandableListAdapterDataCollection;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.presenter.HomePresenter;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.view.HomeViewInterface;

import java.util.HashMap;
import java.util.List;


public class HomeActivityNew extends BaseActivity implements View.OnClickListener, HomeViewInterface {

    private static final String TAG = "HomeActivityNew";
    Intent intent;
    ExpandableListAdapterDataCollection listAdapter;
    ExpandableListView expListView;
    ExternalDbOpenHelper dbhelper;
    ImageView imageMenu;
    HashMap<String, List<Datum>> listDataChild;
    TextView helpTexts;
    SharedPreferences syncSurveyPreferences;
    TextView logOut;
    TextView userNameLabel;
    private TextView dueCountLable;
    Context context;
    private LinearLayout pressBack;
    private LinearLayout dataformsLinear;
    HomePresenter homePresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        this.context = HomeActivityNew.this;
        homePresenter = new HomePresenter(this);
        initVariables();
        Logger.logD("Current page is homescreen", "curent page is homescreen");
        DBHandler dbHandler = new DBHandler(this);
        dbhelper = ExternalDbOpenHelper.getInstance(HomeActivityNew.this, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("inv_id", ""));
        listDataChild = new HashMap<>();
        homePresenter.doExpandableListHeadingFunctionality(dbhelper);
        pressBack.setOnClickListener(this);
        helpTexts.setOnClickListener(this);
        logOut.setOnClickListener(this);
        logOut.setOnClickListener(this);
        dataformsLinear.setOnClickListener(this);
        dueCountLable.setOnClickListener(this);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem) {
                    expListView.collapseGroup(previousItem);
                }
                previousItem = groupPosition;
            }
        });
    }

    /**
     * initializing all the field values
     */
    private void initVariables() {
        imageMenu =  findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);
        TextView toolbarTitle =  findViewById(R.id.toolbarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        toolbarTitle.setTypeface(customFont);
        toolbarTitle.setText(getString(R.string.Home));
        syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivityNew.this);
        pressBack =  findViewById(R.id.backPress);
        pressBack.setVisibility(View.GONE);
        userNameLabel =  findViewById(R.id.userNameLabel);
        dueCountLable =  findViewById(R.id.duecountLable);
        String userName = defaultPreferences.getString("user_name", "");
        userNameLabel.setText(String.format("Logged in as: %s", userName));
        userNameLabel.setTypeface(customFont);
        helpTexts = findViewById(R.id.helptext);
        logOut =  findViewById(R.id.logout);
        dataformsLinear =  findViewById(R.id.dataformsLinear);
        expListView =  findViewById(R.id.expandableListview);
        TextView contentUpdateView =  findViewById(R.id.update_content);
        contentUpdateView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;

    }
    @Override
    public void onClick(View view) {
        Logger.logD(TAG, "view");
    }
    @Override
    public void getExpandableListHeading(List<String> listDataHeader, HashMap<String, List<Datum>> listDataChild) {
        Logger.logD("MVP WORKING", listDataHeader.size() + "");
        listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);
    }
}