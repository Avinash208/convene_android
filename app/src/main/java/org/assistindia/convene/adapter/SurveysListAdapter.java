package org.assistindia.convene.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.assistindia.convene.BeenClass.parentChild.SurveyDetail;
import org.assistindia.convene.LevelsActivityNew;
import org.assistindia.convene.ListingActivity;
import org.assistindia.convene.R;
import org.assistindia.convene.utils.AnimationUtils;
import org.assistindia.convene.utils.Constants;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mahiti on 15/12/17.
 */

public class SurveysListAdapter extends BaseAdapter {

    private Context context;
    private SharedPreferences sharedPreferences;
    private List<SurveyDetail> surveyDetailList;
    private static final String TYPEIDS="typeIds";
    private static final String HEADERNAME=Constants.HEADER_NAME;
    private static final String TYPENAME="typeName";
    private static final String LOCATION_BASED_TITLE="LOCATION_BASED";

    private static final String BENEFICIARY_TITLE="BENEFICIARY";
    private static final String FACILITY_TITLE="FACILITY";
    private static final String PERIODICITY_FLAG="periodicityFlag";
    private static final String PERIODICITY=Constants.PERIODICITY;
    private static final String SURVEY_ID="survey_id";
    private static final String BENEFICIARY_IDS="beneficiaryIds";
    private static final String FACILITY_IDS="facilityIds";
    private static final String SURVEY_NAME="SurveyName";
    private static final String MY_PREFS_NAME = "MyPrefs";

    private SharedPreferences prefs;

    public SurveysListAdapter(Context context, List<SurveyDetail> surveysList){
        this.context=context;
        this.surveyDetailList=surveysList;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }
    @Override
    public int getCount() {
        return surveyDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return surveyDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Viewholder vh;
        View layoutView = view;
        if (layoutView == null) {
            vh = new Viewholder();
            layoutView = inflater.inflate(R.layout.surveyselection_detail_row, viewGroup,false);
            vh.surveyName = layoutView.findViewById(R.id.lblListItem);
            vh.linearItem = layoutView.findViewById(R.id.linearItem);
            vh.imageArraow= layoutView.findViewById(R.id.imageArraow);
            vh.periodicityTextview= layoutView.findViewById(R.id.periodicityTextview);
            vh.typetextTextView= layoutView.findViewById(R.id.typeTextview);
            vh.typeLabelTextView= layoutView.findViewById(R.id.labelTextview);
            layoutView.setTag(vh);
        } else {
            layoutView = view;
            vh = (Viewholder) layoutView.getTag();
        }
        vh.surveyName.setText(surveyDetailList.get(i).getSurveyName());
        vh.periodicityTextview.setText(String.format("Periodicity : %s", surveyDetailList.get(i).getPiriodicityFlag()));
        if(("").equals(surveyDetailList.get(i).getBeneficiaryIds()) && ("").equals(surveyDetailList.get(i).getFacilityIds())){
            vh.typetextTextView.setVisibility(View.GONE);
            vh.typeLabelTextView.setVisibility(View.GONE);
        }else if(!("").equals(surveyDetailList.get(i).getBeneficiaryIds()) && !("").equals(surveyDetailList.get(i).getFacilityIds())){
            vh.typeLabelTextView.setText("Beneficiary :");
            vh.typetextTextView.setText(surveyDetailList.get(i).getBeneficiaryType());
        } else if(!("").equalsIgnoreCase(surveyDetailList.get(i).getFacilityIds())){
            vh.typetextTextView.setText(surveyDetailList.get(i).getFacilityType());
            vh.typeLabelTextView.setText("Facility :");
        }else{
            vh.typetextTextView.setText(surveyDetailList.get(i).getBeneficiaryType());
            vh.typeLabelTextView.setText("Beneficiary :");
        }
        vh.linearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.viewAnimation(view);
                methodToCallNextActivity(surveyDetailList,i,vh.typetextTextView.getText().toString(),vh);
            }
        });
        return layoutView;
    }

    private void methodToCallNextActivity(List<SurveyDetail> surveyDetailList, int position, String getSurveyTypeName, Viewholder vh) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Bundle bundle=new Bundle();
        if(surveyDetailList.get(position).getFacilityIds().isEmpty()&& (surveyDetailList.get(position).getBeneficiaryIds().isEmpty())){
            methodToSetValuesToBundle(bundle,editor,surveyDetailList,position,LOCATION_BASED_TITLE,getSurveyTypeName);
            SharedPreferences.Editor editor1=prefs.edit();
            editor1.putString("Survey_tittle",surveyDetailList.get(position).getSurveyName());
            editor1.putInt(SURVEY_ID,surveyDetailList.get(position).getSurveyId());
            editor1.putString(PERIODICITY,surveyDetailList.get(position).getPiriodicityFlag());
            editor1.putString("periodicity_count",surveyDetailList.get(position).getPiriodicity());
            editor1.putString("survey_name",surveyDetailList.get(position).getSurveyName());
            editor1.apply();
            SharedPreferences.Editor syncSurveyEditor1 = sharedPreferences.edit();
            syncSurveyEditor1.putBoolean("isLocationBased", true);
            syncSurveyEditor1.putBoolean("isNotLocationBased", false);
            editor.putString(BENEFICIARY_IDS,"");
            editor.putString(FACILITY_IDS,"");
            syncSurveyEditor1.putInt(SURVEY_ID,surveyDetailList.get(position).getSurveyId());
            syncSurveyEditor1.apply();
            Intent intent1=new Intent(context, LevelsActivityNew.class);
            intent1.putExtra(Constants.PERIODICITY,surveyDetailList.get(position).getPiriodicityFlag());
            intent1.putExtra(Constants.P_LIMIT,surveyDetailList.get(position).getPLimit());
            intent1.putExtra("periodicity_count",surveyDetailList.get(position).getPiriodicity());
            intent1.putExtra(SURVEY_ID,surveyDetailList.get(position).getSurveyId());
            intent1.putExtra("survey_name",surveyDetailList.get(position).getSurveyName());
            intent1.putExtra("benId",surveyDetailList.get(position).getFacilityIds());
            context.startActivity(intent1);
        }else {
            SharedPreferences.Editor syncSurveyEditor1 = sharedPreferences.edit();
            syncSurveyEditor1.putBoolean("isLocationBased", false);
            syncSurveyEditor1.putBoolean("isNotLocationBased", true);
            syncSurveyEditor1.apply();
            //DeleteChanges
//            Intent intent = new Intent(context, ChooseBeneficiaryActivity.class);
//            setToBundle(position, surveyDetailList, intent, getSurveyTypeName,bundle,editor);
//            context.startActivity(intent);
//            Bundle[{typeValue=Health Centers, beneficiary_type_id=286, headerName=Facilities}]

            Intent intent = new Intent(context, ListingActivity.class);
            intent.putExtra(Constants.HEADER_NAME,"Facilities");
            intent.putExtra("beneficiary_type_id",surveyDetailList.get(position).getFacilityIds());

            if(vh.typeLabelTextView.getText().toString().contains("Benef")){
                intent.putExtra(Constants.HEADER_NAME,"Beneficiaries");
                intent.putExtra("beneficiary_type_id",surveyDetailList.get(position).getBeneficiaryIds());
               
            }
            intent.putExtra(Constants.TYPE_VALUE, surveyDetailList.get(position).getSurveyName());
            intent.putExtra("surveyIdDCF",surveyDetailList.get(position).getSurveyId());
            context.startActivity(intent);
        }
    }

    private void setToBundle(int position, List<SurveyDetail> surveyDetailList, Intent intent, String getSurveyTypeName, Bundle bundle, SharedPreferences.Editor editor) {
        if(!surveyDetailList.get(position).getFacilityIds().isEmpty() &&!surveyDetailList.get(position).getBeneficiaryIds().isEmpty()) {
            methodToSetValuesToBundle(bundle,editor,surveyDetailList,position,BENEFICIARY_TITLE,getSurveyTypeName);
        }else if(surveyDetailList.get(position).getFacilityIds().isEmpty()) {
            methodToSetValuesToBundle(bundle,editor,surveyDetailList,position,BENEFICIARY_TITLE,getSurveyTypeName);
        }else{
            methodToSetValuesToBundle(bundle,editor,surveyDetailList,position,FACILITY_TITLE,getSurveyTypeName);
        }
        bundle.putString(PERIODICITY_FLAG,surveyDetailList.get(position).getPiriodicityFlag());
        editor.putString(PERIODICITY_FLAG,surveyDetailList.get(position).getPiriodicityFlag());
        bundle.putString(PERIODICITY,surveyDetailList.get(position).getPiriodicity());
        editor.putString(PERIODICITY,surveyDetailList.get(position).getPiriodicity());
        editor.putInt(SURVEY_ID, surveyDetailList.get(position).getSurveyId());
        editor.putInt(Constants.P_LIMIT, surveyDetailList.get(position).getPLimit());
        bundle.putString(SURVEY_ID, String.valueOf(surveyDetailList.get(position).getSurveyId()));
        editor.apply();
        intent.putExtras(bundle);
    }

    private void methodToSetValuesToBundle(Bundle bundle, SharedPreferences.Editor editor, List<SurveyDetail> surveyDetailList, int position, String beneficiaryTitle, String getSurveyTypeName) {
        bundle.putString(TYPEIDS, surveyDetailList.get(position).getBeneficiaryIds());
        editor.putString(TYPEIDS,surveyDetailList.get(position).getBeneficiaryIds());
        editor.putString(HEADERNAME, getSurveyTypeName);
        bundle.putString(HEADERNAME, getSurveyTypeName);
        editor.putString(TYPENAME,beneficiaryTitle);
        bundle.putString(TYPENAME,beneficiaryTitle);
        editor.putString(BENEFICIARY_IDS,surveyDetailList.get(position).getBeneficiaryIds());
        bundle.putString(BENEFICIARY_IDS,surveyDetailList.get(position).getBeneficiaryIds());
        editor.putString(FACILITY_IDS,surveyDetailList.get(position).getFacilityIds());
        bundle.putString(FACILITY_IDS,surveyDetailList.get(position).getFacilityIds());
        editor.putString(SURVEY_NAME,surveyDetailList.get(position).getSurveyName());
        bundle.putString(SURVEY_NAME,surveyDetailList.get(position).getSurveyName());
        bundle.putInt(Constants.P_LIMIT,surveyDetailList.get(position).getPLimit());
        editor.putInt(SURVEY_ID, surveyDetailList.get(position).getSurveyId());
        bundle.putString(SURVEY_ID, String.valueOf(surveyDetailList.get(position).getSurveyId()));
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class Viewholder {
        TextView surveyName;
        ImageView imageArraow;
        TextView periodicityTextview;
        TextView typetextTextView;
        TextView typeLabelTextView;
        LinearLayout linearItem;
    }
}
