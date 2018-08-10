package org.assistindia.convene;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.assistindia.convene.BeenClass.parentChild.SurveyDetail;
import org.assistindia.convene.adapter.SurveysListAdapter;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.utils.Constants;

import java.util.List;

public class DataCollectionListingActivity extends BaseActivity implements View.OnClickListener {

    private ListView surveyListview;
    private ExternalDbOpenHelper externalDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection_listing);
        initializeVariables();
        methodToExpandableAdapter();
    }


    private void methodToExpandableAdapter() {
        List<SurveyDetail> surveysList = externalDbOpenHelper.getUpdatedSurveyList("");
        SurveysListAdapter surveysListAdapter = new SurveysListAdapter(this, surveysList);
        surveyListview.setAdapter(surveysListAdapter);
    }

    private void initializeVariables() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DataCollectionListingActivity.this);
        externalDbOpenHelper = ExternalDbOpenHelper.getInstance(DataCollectionListingActivity.this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
        surveyListview = findViewById(R.id.listview);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        LinearLayout backPressLinearLayout = findViewById(R.id.backPress);
        backPressLinearLayout.setOnClickListener(this);
        toolbarTitle.setText("Data collection forms");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backPress) {
            onBackPressed();
        }
    }
}
