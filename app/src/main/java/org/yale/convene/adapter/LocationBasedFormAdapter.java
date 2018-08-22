package org.yale.convene.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.yale.convene.BeenClass.SurveysBean;
import org.yale.convene.BeenClass.parentChild.LocationSurveyBeen;
import org.yale.convene.R;
import org.yale.convene.ShowSurveyPreview;
import org.yale.convene.SupportClass;
import org.yale.convene.SurveyQuestionActivity;
import org.yale.convene.api.BeneficiaryApis.ResponseUpdateAPI;
import org.yale.convene.database.ConveneDatabaseHelper;
import org.yale.convene.location.GPSTracker;
import org.yale.convene.utils.AddBeneficiaryUtils;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.PeriodicityUtils;
import org.yale.convene.utils.StartSurvey;
import org.yale.convene.utils.ToastUtils;
import org.yale.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Adapter class
 *
 */
public class LocationBasedFormAdapter extends BaseAdapter {
    private  SharedPreferences preferences;
    private  ConveneDatabaseHelper databaseHelper;
    private Context mcontext;
    private List<LocationSurveyBeen> hamletLevelBeenList = new ArrayList<>();
    private String periodicity = "";
    private String surveyId = "";
    private Activity activity;
//    private int getPausedSurveyID = 0;
    private static final String CHECK_CONNECTIVITY = "No internet connection";
    private static final String SAVEDRAFTBUTTON_FLAG = "SaveDraftButtonFlag";
    private static final String ISLOCATIONBASED = "isLocationBased";
    private static final String ISNOTLOCATIONBASED = "isNotLocationBased";
    private AddBeneficiaryUtils addBeneficiaryUtils;
    private Bundle bundle;
    private String surveyName="";
    private GPSTracker gpsTracker;
    private String[] orderLabels;
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;


    /**
     * @param context
     * @param levelBeens
     * @param orderLabels
     * @param orderLeves
     */
    public LocationBasedFormAdapter(Context context, Activity activity, List<LocationSurveyBeen> levelBeens, Bundle bundle, String[] orderLabels, String[] orderLeves) {
        try{
            mcontext = context;
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            this.hamletLevelBeenList = levelBeens;
            this.activity = activity;
            databaseHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB, ""), preferences.getString("UID", ""));
            addBeneficiaryUtils = new AddBeneficiaryUtils(this.activity);
            this.bundle=bundle;
            periodicity = bundle.getString(Constants.PERIODICITY);
            this.orderLabels=orderLabels;
        }catch (Exception e){
          Logger.logE("","",e);
        }


    }


    @Override
    public int getCount() {
        return hamletLevelBeenList.size();
    }


    /**
     * @param beneficiaryPosition
     * @return
     */
    @Override
    public Object getItem(int beneficiaryPosition) {
        return hamletLevelBeenList.get(beneficiaryPosition);
    }


    /**
     * @param beneficiaryPosition
     * @return
     */
    @Override
    public long getItemId(int beneficiaryPosition) {
        return beneficiaryPosition;
    }


    /**
     * @param beneficiaryPosition
     * @param convertView
     * @param viewGroup
     * @return
     */

    @Override
    public View getView(final int beneficiaryPosition, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Viewholder viewholder;
        View layoutView = convertView;
        if (layoutView == null) {
            viewholder = new Viewholder();
            layoutView = inflater.inflate(R.layout.beneficiaryinstitution_detail_row, viewGroup, false);
            initializeVariables(viewholder, layoutView);
            layoutView.setTag(viewholder);
        } else {
            layoutView = convertView;
            viewholder = (Viewholder) layoutView.getTag();
        }
        setToString(bundle);
        viewholder.periodicityTextview.setText(periodicity);
        viewholder.hamletTextView.setText(hamletLevelBeenList.get(beneficiaryPosition).getLocationName());
        setSurveyStatus(hamletLevelBeenList.get(beneficiaryPosition),viewholder.pendingTextview);
        if (hamletLevelBeenList.get(beneficiaryPosition).getIsOnline() == 2 )
            SupportClass.setOffline(viewholder.hamletTextView,hamletLevelBeenList.get(beneficiaryPosition).getLocationName());
        if (hamletLevelBeenList.get(beneficiaryPosition).getIsOnline() == 1 )
            viewholder.hamletTextView.setText(hamletLevelBeenList.get(beneficiaryPosition).getLocationName());
        viewholder.pendingTextview.setOnClickListener(view -> {
            TextView btn = (TextView) view;
            callNewSurvey(btn, hamletLevelBeenList.get(beneficiaryPosition));
        });
        Logger.logD("TimeTest","@getView after query");
        return layoutView;
    }

    private void setSurveyStatus(LocationSurveyBeen locationSurveyBeen, TextView pendingTextview) {
        if(locationSurveyBeen.getIsContinue() == 1)
        {
            pendingTextview.setText("Continue");
            return;
        }

        if (locationSurveyBeen.getSurveyStatusFlag() == 1)
        {
            pendingTextview.setText("Pending");
            return;
        }
        if (locationSurveyBeen.getSurveyStatusFlag() == -1 || locationSurveyBeen.getSurveyStatusFlag() == 2)
        {
            SupportClass.setRedStar(pendingTextview,mcontext.getString(R.string.pending));
            return;
        }
        if (locationSurveyBeen.getIsEditable() == 1)
        {
            pendingTextview.setText("Edit/View");
            return;
        }
        if(locationSurveyBeen.getIsEditable() == 2)
        {
            pendingTextview.setText("View");
            return;
        }



    }
    /**
     *
     * @param bundle
     *
     */
    private void setToString(Bundle bundle) {
        try {

            if(bundle!=null){
                surveyId = String.valueOf(bundle.getInt("survey_id"));
                periodicity =bundle.getString(Constants.PERIODICITY);
                surveyName=bundle.getString("survey_name");
            }else{
                surveyId = String.valueOf(preferences.getInt(Constants.SURVEY_ID,0));
                periodicity =preferences.getString(Constants.PERIODICITY,"");
                surveyName=preferences.getString("survey_name","");
            }
        }catch (Exception e){
            Logger.logE("","",e);
        }

    }
//
    private void callNewSurvey(TextView btn, LocationSurveyBeen surveyBeen) {
        int boundaryId = surveyBeen.getClusterId();
        int boundarylevel = Integer.parseInt(surveyBeen.getLocationLevel().replace("level",""));
        SharedPreferences.Editor editor11 = preferences.edit();
        Intent intent;
        switch(btn.getText().toString()){
            case "Pending":
                SurveysBean surveysBean= new SurveysBean();
                addBeneficiaryUtils.setToPreferences(mcontext, surveyName, boundaryId, boundarylevel, surveyBeen.getLocationName(), "", preferences, surveysBean);
                editor11.putBoolean(ISLOCATIONBASED, true);
                editor11.putBoolean(ISNOTLOCATIONBASED, false);
                editor11.putBoolean(SAVEDRAFTBUTTON_FLAG, true);
                editor11.apply();
                calligGPS();
                checkGPSConnection(surveyBeen, false);
                break;
            case "Pending *":
                SurveysBean surveysB= new SurveysBean();
                addBeneficiaryUtils.setToPreferences(mcontext, surveyName, boundaryId, boundarylevel, surveyBeen.getLocationName(), "", preferences, surveysB);

                editor11.putBoolean(ISLOCATIONBASED, true);
                editor11.putBoolean(ISNOTLOCATIONBASED, false);
                editor11.putBoolean(SAVEDRAFTBUTTON_FLAG, true);
                editor11.apply();
                calligGPS();
                checkGPSConnection(surveyBeen,true);
                break;
            case "Continue":
                editor11.putBoolean(SAVEDRAFTBUTTON_FLAG, true);
                editor11.putBoolean(ISLOCATIONBASED, true);
                editor11.putBoolean(ISNOTLOCATIONBASED, false);
                editor11.apply();
                intent = new Intent(mcontext, SurveyQuestionActivity.class);
                intent.putExtra("SurveyId", String.valueOf(surveyBeen.getResponseId()));
                intent.putExtra(Constants.SURVEY_ID, String.valueOf(surveyId));
                mcontext.startActivity(intent);
                break;
            case "Edit/View":
                if(surveyBeen.getIsOnline() == 1) {
                    if (Utils.haveNetworkConnection(mcontext)) {
                        SurveysBean surveys= new SurveysBean();
                        addBeneficiaryUtils.setToPreferences(mcontext, surveyName, boundaryId, boundarylevel, surveyBeen.getLocationName(), "", preferences, surveys);
                        SharedPreferences.Editor editorSaveDraft = preferences.edit();
                        editorSaveDraft.putBoolean(SAVEDRAFTBUTTON_FLAG, true);
                        editorSaveDraft.putString("recentPreviewRecord", String.valueOf(surveyBeen.getResponseId()));
                        editorSaveDraft.putBoolean(ISLOCATIONBASED, true);
                        editorSaveDraft.putBoolean(ISNOTLOCATIONBASED, false);
                        editorSaveDraft.apply();
                        methodToCallEdittedSurveyResponse(surveyId, surveyBeen);
                    } else {
                        ToastUtils.displayToast(CHECK_CONNECTIVITY, mcontext);
                    }
                }
                else{
                    editor11.putBoolean(ISLOCATIONBASED, true);
                    editor11.putBoolean(ISNOTLOCATIONBASED, false);
                    boolean flag = false;
                    if (surveyBeen.getIsEditable() == 1)
                        flag = true;
                  //  editor11.putBoolean(ShowSurveyPreview.isEditableKey,flag);
                    editor11.apply();
                    SharedPreferences.Editor recentPreview = preferences.edit();
                    recentPreview.putString("recentPreviewRecord", "");
                    recentPreview.apply();
                    SharedPreferences.Editor recentP1 = prefs.edit();
                    recentP1.putString("recentPreviewRecord", "");
                    recentP1.apply();
                    intent = new Intent(mcontext, ShowSurveyPreview.class);
                    intent.putExtra("surveyPrimaryKey", String.valueOf(surveyBeen.getResponseId()));
                    intent.putExtra("survey_id", String.valueOf(surveyId));
                    mcontext.startActivity(intent);
                }
                break;
            case "View":
                if (Utils.haveNetworkConnection(mcontext)) {
                    methodToCallEdittedSurveyResponse(surveyId, surveyBeen);
                    editor11.putBoolean(ISLOCATIONBASED, true);
                    editor11.putBoolean(ISNOTLOCATIONBASED, false);
                    editor11.apply();
                } else {
                    ToastUtils.displayToast(CHECK_CONNECTIVITY, mcontext);
                }
                break;
                default:
                    ToastUtils.displayToast("Periodic limit exceed", mcontext);
                    break;
        }
    }
//
    /**
     * method to call the response details api get the survey answers of particular survey
     * @param surveyId
     * @param surveyBeen
     */
    private void methodToCallEdittedSurveyResponse(String surveyId, LocationSurveyBeen surveyBeen) {
        new ResponseUpdateAPI(activity,mcontext,mcontext.getString(R.string.main_Url)+"api/response-detail/",String.valueOf(surveyBeen.getResponseId()),surveyBeen.getClusterId(),surveyBeen.getLocationName(),"", String.valueOf(surveyBeen.getLocationLevel()),Integer.parseInt(surveyId)).execute();
    }
    /**
     * method to check the gps connection and start the survey
     * @param
     * @param surveyBeen
     * @param isPrev
     */
    private void checkGPSConnection(LocationSurveyBeen surveyBeen, boolean isPrev) {
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }else {
            checkreagionalLanguage(surveyBeen, isPrev);
        }
    }

    /**
     * method
     *
     */
    private void calligGPS() {
        gpsTracker = new GPSTracker(mcontext);
    }
    private void checkreagionalLanguage(LocationSurveyBeen surveyBeen, boolean isPrev) {
        if ((1) == (preferences.getInt(Constants.SELECTEDLANGUAGE, 0))) {
            boolean isLanguageExist = databaseHelper.checkLanguageExistOrNot(Integer.parseInt(surveyId), preferences.getInt(Constants.SELECTEDLANGUAGE, 0));
            Logger.logD("isLanguageExist", "-->" + isLanguageExist);
            if (isLanguageExist) {
                if (isPrev)
                {
                    showPrevSurveyDailog(surveyBeen);
                }
                else
                    new StartSurvey(mcontext, activity, surveyBeen.getClusterId(), orderLabels[orderLabels.length-1].substring(0,1).toUpperCase() + orderLabels[orderLabels.length-1].substring(1), surveyBeen.getLocationName(), surveyId,"").execute();

            } else {
                Utils.showAlertPopUp(activity);
            }
        } else {
            boolean isRegionalLangExist = databaseHelper.checkRegionalLanguageExist(Integer.parseInt(surveyId), preferences.getInt(Constants.SELECTEDLANGUAGE, 0));
            if (isRegionalLangExist) {
                new StartSurvey(mcontext, activity, surveyBeen.getClusterId(), orderLabels[orderLabels.length-1].substring(0,1).toUpperCase() + orderLabels[orderLabels.length-1].substring(1), surveyBeen.getLocationName(), surveyId,"").execute();
            } else {
                Utils.showAlertPopUp(activity);
            }
        }
    }


    /**
     * method
     *
     * @param surveysBean
     */
    private void showPrevSurveyDailog(LocationSurveyBeen surveysBean) {
        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_alert_dialog);
        TextView text = dialog.findViewById(R.id.labelTextview);
        text.setText(R.string.previous_survey);
        Button dialogBtn_okay = dialog.findViewById(R.id.okButton);
        dialogBtn_okay.setOnClickListener(v -> {
            dialog.cancel();
            String previousDate = PeriodicityUtils.getPreviousPeriodLastDate(periodicity);
            new StartSurvey(mcontext, activity, surveysBean.getClusterId(), orderLabels[orderLabels.length-1].substring(0,1).toUpperCase() + orderLabels[orderLabels.length-1].substring(1), surveysBean.getLocationName(), surveyId,previousDate).execute();

        });
        dialog.show();
    }


    /**
     * method
     * @param viewholder
     * @param layoutView
     *
     */
    private void initializeVariables(Viewholder viewholder, View layoutView) {
        viewholder.hamletTextView= layoutView.findViewById(R.id.villageName);
        viewholder.periodicityTextview= layoutView.findViewById(R.id.periodicityTextview);
        viewholder.pendingTextview= layoutView.findViewById(R.id.pending);
    }

    public void updateList(List<LocationSurveyBeen> locationSurveyBeenList)
    {
       hamletLevelBeenList = locationSurveyBeenList;

    }


    /**
     * method
     *
     * @param datum
     */
//    public void add(LevelBeen datum){
//        hamletLevelBeenList.add(datum);
//    }

    /**
     * method
     *
     */
    private class Viewholder {
       TextView hamletTextView;
       TextView periodicityTextview;
       TextView pendingTextview;
    }
}
