package org.fwwb.convene.convenecode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.StatusBean;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PendingSurveyActivity extends AppCompatActivity implements View.OnClickListener {
    ExternalDbOpenHelper dbhelper;
    SharedPreferences defaultPreferences;
    Activity activity;
    private DBHandler dbHelper;
    private LinearLayout pressBack;
    private int getTotalNumberOfSurveys = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_survey);
        initVariables();
    }

    private void initVariables() {
        activity = PendingSurveyActivity.this;
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        dbhelper = ExternalDbOpenHelper.getInstance(activity, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("inv_id", ""));
        dbHelper = new DBHandler(activity);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        toolbarTitle.setTypeface(customFont);
        toolbarTitle.setText(getString(R.string.pending));
        pressBack = findViewById(R.id.backPress);
        pressBack.setOnClickListener(this);
        pressBack.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        new createCompletedSurveyListInBackGround().execute();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backPress:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private class createCompletedSurveyListInBackGround extends AsyncTask {

        List<Datum> getAllActivityList = new ArrayList<>();
        Map<String, List<StatusBean>> getAllResponse = new HashMap<>();
        @Override
        protected Object doInBackground(Object[] objects) {
            getTotalNumberOfSurveys=dbHelper.getTotalSurveyCount(dbHelper);
            getAllActivityList = dbhelper.getAllActivityList(dbhelper);
            for (int i = 0; i < getAllActivityList.size(); i++) {
                getAllResponse.put(getAllActivityList.get(i).getName(), dbHelper.getCollectedSurveyList(dbhelper, getAllActivityList.get(i).getId()));
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (!getAllResponse.isEmpty()) {
                updateUI(getAllResponse);
            } else {
                ToastUtils.displayToast("Records seems to be Empty", activity);
            }
        }
    }

    private void updateUI(Map<String, List<StatusBean>> getAllResponse) {
        try {
            LinearLayout dynamiccompletedlist = (LinearLayout) findViewById(R.id.dynamic_completed_list);
            dynamiccompletedlist.removeAllViews();
            Set<String> getHashMapKeys = getAllResponse.keySet();
            for (String getSurveyname : getHashMapKeys) {
                View inflatParentView = getLayoutInflater().inflate(R.layout.completed_row_update, dynamiccompletedlist, false);
                TextView activity= (TextView) inflatParentView.findViewById(R.id.activity_name);
                TextView updateCompletedCount= (TextView) inflatParentView.findViewById(R.id.update_count);
                List<StatusBean> getResponse= getAllResponse.get(getSurveyname);
                activity.setText(getSurveyname);
                int calCulatePending= getTotalNumberOfSurveys-getResponse.size();
                updateCompletedCount.setTextColor(Color.RED);
                updateCompletedCount.setText(String.valueOf(calCulatePending));
                dynamiccompletedlist.addView(inflatParentView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.displayToast("Some thing went wrong Please contact Admin",activity);
        }
    }
}
