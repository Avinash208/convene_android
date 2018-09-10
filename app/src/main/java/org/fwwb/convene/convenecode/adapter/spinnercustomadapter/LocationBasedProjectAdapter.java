package org.fwwb.convene.convenecode.adapter.spinnercustomadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fwwb.convene.convenecode.BeenClass.parentChild.SurveyDetail;
import org.fwwb.convene.convenecode.ListingActivity;
import org.fwwb.convene.convenecode.LocationBasedActivity;
import org.fwwb.convene.convenecode.ProjectSelectionActivity;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.List;

import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;

/**
 * Created by mahiti on 16/01/18.
 */


public class LocationBasedProjectAdapter extends RecyclerView.Adapter<LocationBasedProjectAdapter.ViewHolder> {



   private Activity activity;
    private static final String MyPREFERENCES = "MyPrefs" ;
   private List<SurveyDetail> locationBasedProjectActivity;
    public LocationBasedProjectAdapter(ProjectSelectionActivity projectSelectionActivity, List<SurveyDetail> locationBasedProjectActivity) {
        this.activity=projectSelectionActivity;
        this.locationBasedProjectActivity=locationBasedProjectActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.project_list_row, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(LocationBasedProjectAdapter.ViewHolder viewHolder, final int position) {

        if (!locationBasedProjectActivity.get(position).getSurveyName().isEmpty()){
            viewHolder.activityname.setText(locationBasedProjectActivity.get(position).getSurveyName());
        }
        if (!locationBasedProjectActivity.get(position).getPiriodicityFlag().isEmpty() ){
           // viewHolder.periodicityname.setText(new StringBuilder().append(locationBasedProjectActivity.get(position).getBeneficiaryType()).append(" - ").append(locationBasedProjectActivity.get(position).getPiriodicityFlag()).toString());
            viewHolder.periodicityname.setText(new StringBuilder().append("Periodicity : ").append(locationBasedProjectActivity.get(position).getPiriodicityFlag()).toString());
        }
        if (!locationBasedProjectActivity.get(position).getBeneficiaryType().isEmpty()){
            viewHolder.projectname.setVisibility(View.VISIBLE);
            viewHolder.projectname.setText(new StringBuilder().append("Beneficiary type : ").append(locationBasedProjectActivity.get(position).getBeneficiaryType()));
        }else{
            viewHolder.projectname.setVisibility(View.GONE);
        }
        setOnClickLisiner(viewHolder,locationBasedProjectActivity.get(position));
        setOrHideBottomView(viewHolder,position);

    }

    private void setOrHideBottomView(ViewHolder viewHolder, int position) {
        if (position==locationBasedProjectActivity.size()-1){
            viewHolder.viewLine.setVisibility(View.GONE);
        }
    }

    private void setOnClickLisiner(ViewHolder viewHolder, SurveyDetail surveyDetail) {
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSharedPreferences(surveyDetail) ;
            }
        });
    }


    @Override
    public int getItemCount() {
        return locationBasedProjectActivity.size();
    }


    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView projectname;
        private TextView periodicityname;
        private TextView activityname;
        private LinearLayout layout;
        private View viewLine;
        public ViewHolder(View view) {
            super(view);
            projectname  = (TextView) view.findViewById(R.id.projectname);
            periodicityname = (TextView) view.findViewById(R.id.periodicityname);
            activityname = (TextView) view.findViewById(R.id.activityname);
            layout = (LinearLayout) view.findViewById(R.id.complete_row);
            viewLine = (View) view.findViewById(R.id.viewline);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    private void setSharedPreferences(SurveyDetail surveyDetailBean) {

        SharedPreferences sharedpreferences = activity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Logger.logD("-->start time","checking time line");
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SURVEY_NAMe, surveyDetailBean.getSurveyName());
        editor.putInt(Constants.FEATURE, 0);
        editor.putInt(Constants.LIMIT, 1);
        editor.putInt(Constants.PERIODICITY, 1);
        editor.putString(Constants.LABEL, "");
        editor.putString(Constants.VERSION, "");
        editor.putInt(Constants.CONFIG, 0);
        editor.putInt(Constants.RD, 0);
        editor.putString(Constants.constraints, "");

        editor.putString(Constants.O_LEAVEL, surveyDetailBean.getOrderLevels());
        editor.putString(Constants.O_LABLES, surveyDetailBean.getLabels());
        editor.putString(Constants.CODE, surveyDetailBean.getPcode());
        editor.putString(Constants.PROJECTFLOW, "1");

        if (!surveyDetailBean.getBeneficiaryIds().isEmpty())
            editor.putInt(SURVEY_ID, Integer.parseInt(surveyDetailBean.getBeneficiaryIds()));
        else
            editor.putInt(SURVEY_ID, surveyDetailBean.getSurveyId());

        editor.putString(Constants.BENEFICIARY_TYPE,surveyDetailBean.getBeneficiaryType());
        editor.putString(Constants.BENEFICIARY_IDS,surveyDetailBean.getBeneficiaryIds());
        editor.putString(Constants.FACILITY_IDS,surveyDetailBean.getFacilityIds());
        editor.putInt(Constants.SELECTEDPROJECTID,surveyDetailBean.getSurveyId());
        getAllLevels(editor,surveyDetailBean);
        editor.putString("Survey_tittle",surveyDetailBean.getSurveyName());
        Logger.logV("the survye id is","the survey id is"+surveyDetailBean.getSurveyId());
        editor.putInt(Constants.Q_CONFIGS, 0);
        editor.apply();
        Logger.logD("-->start time","checking time line");
        if (!surveyDetailBean.getBeneficiaryIds().isEmpty()) {
            Intent survrySummaryReport = new Intent(activity, ListingActivity.class);
            survrySummaryReport.putExtra(SURVEY_ID, String.valueOf(sharedpreferences.getInt(SURVEY_ID,0)));
            survrySummaryReport.putExtra(Constants.HEADER_NAME, surveyDetailBean.getBeneficiaryType());
            activity.startActivity(survrySummaryReport);
        }else{
            Intent intent1=new Intent(activity, LocationBasedActivity.class);
            intent1.putExtra(Constants.PERIODICITY,surveyDetailBean.getPiriodicityFlag());
            intent1.putExtra(Constants.P_LIMIT,1);
            intent1.putExtra("periodicity_count",1);
            intent1.putExtra(SURVEY_ID,surveyDetailBean.getSurveyId());
            intent1.putExtra("survey_name",surveyDetailBean.getSurveyName());
            intent1.putExtra("benId","");
            activity.startActivity(intent1);
        }
    }

    private void getAllLevels(SharedPreferences.Editor editor, SurveyDetail surveyDetailBean) {
        editor.putString(Constants.LEVEL1_ID,surveyDetailBean.getLevel1());
        editor.putString(Constants.LEVEL2_ID,surveyDetailBean.getLevel2());
        editor.putString(Constants.LEVEL3_ID,surveyDetailBean.getLevel3());
        editor.putString(Constants.LEVEL4_ID,surveyDetailBean.getLevel4());
        editor.putString(Constants.LEVEL5_ID,surveyDetailBean.getLevel5());
        editor.putString(Constants.LEVEL6_ID,surveyDetailBean.getLevel6());
        editor.putString(Constants.LEVEL7_ID,surveyDetailBean.getLevel7());
    }

}
