package org.mahiti.convenemis;

/**
 * Created by mahiti on 28/05/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeneficiaryLinkageDetails extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private LinearLayout parentLayout;
    private Button backToSurvey;
    private Button submitSurvey;
    private SharedPreferences surveyPreferences;
    private ConveneDatabaseHelper dbOpenHelper;
    private String surveyPrimaryKeyId="";
    private static final String SURVEYID = "survey_id";
    private int surveysId;
    private String fragmentHeading;
    private RelativeLayout topLayoutContainer;


    public BeneficiaryLinkageDetails() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BeneficiaryLinkageDetails newInstance(int sectionNumber) {
        BeneficiaryLinkageDetails fragment = new BeneficiaryLinkageDetails();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beneficiarylinkages, container, false);
        initVariables(rootView);


        createDynamicQuestionSet(getContext(),surveyPrimaryKeyId,dbOpenHelper,surveyPreferences,surveysId,
                rootView);
        return rootView;
    }

    private void setDynamicImageAccordingly(View fragmentView, String answer) {
        if (fragmentHeading!=null && !fragmentHeading.isEmpty() && fragmentHeading.equals("Households")){
            ImageView imageView= fragmentView.findViewById(R.id.userImage);
            Utilities.setGender(imageView,answer,getActivity());
        }

    }

    /**
     * method to read the views.
     * @param dialog
     */
    private  void initVariables(View dialog) {
        surveyPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbOpenHelper = ConveneDatabaseHelper.getInstance(getActivity(), surveyPreferences.getString(Constants.CONVENE_DB, ""), surveyPreferences.getString("UID", ""));
        parentLayout = dialog.findViewById(R.id.dynamic_question_set);
        topLayoutContainer = dialog.findViewById(R.id.top_layout_container);
        backToSurvey = dialog.findViewById(R.id.back_survey);
        submitSurvey = dialog.findViewById(R.id.submit_survey);
        Intent surveyPrimaryKeyIntent = getActivity().getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            fragmentHeading = surveyPrimaryKeyIntent.getStringExtra(Constants.HEADER_NAME);
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));

        }
        checkTopLayoutVisiablity(fragmentHeading);

    }

    private void checkTopLayoutVisiablity(String fragmentHeading) {
        if (fragmentHeading.equalsIgnoreCase("Households")){
            topLayoutContainer.setVisibility(View.VISIBLE);
        }else{
            topLayoutContainer.setVisibility(View.GONE);
        }
    }

    /**
     * method to create dynamic layout on fetching Question and answer .
     * @param context Activity context
     * @param surveyPrimaryKeyId   surveytable pri-key
     * @param dbOpenHelper  database
     * @param preferences
     * @param surveysId
     * @param rootView
     */
    private  void createDynamicQuestionSet(Context context, String surveyPrimaryKeyId,
                                           ConveneDatabaseHelper dbOpenHelper, SharedPreferences preferences,
                                           int surveysId, View rootView) {
        DBHandler dbHelper= new DBHandler(context);

        List<PreviewQuestionAnswerSet> getAttendedQuestion= dbHelper.getAttendedQuestion(surveyPrimaryKeyId,dbOpenHelper,preferences.getInt(Constants.SELECTEDLANGUAGE,0),surveysId);
        if (!getAttendedQuestion.isEmpty()){
            for (int i=0;i<getAttendedQuestion.size();i++){
              /*
                questionParamLayout layout is for setting the answer from the  surveyDatabase.
                 Question and options are getting from convene database .with reference of dbhandler database .
                */
                if (getAttendedQuestion.get(i).getQuestion().equals(Constants.Gender) ){
                    setDynamicImageAccordingly(rootView,getAttendedQuestion.get(i).getAnswer());
                    getAttendedQuestion.remove(i);
                }
                else if (getAttendedQuestion.get(i).getQuestion().equals(Constants.NAME) ){
                    setDynamicNameAccordingly(rootView,getAttendedQuestion.get(i).getAnswer());
                    getAttendedQuestion.remove(i);
                }
                LinearLayout.LayoutParams  questionParamLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                View child = this.getLayoutInflater().inflate(R.layout.detail_row, parentLayout, false);//child.xml
                TextView locationlabel = (TextView) child.findViewById(R.id.locationlabel);
                TextView namelabel = (TextView) child.findViewById(R.id.namelabel);
                namelabel.setText(getAttendedQuestion.get(i).getQuestion());
                locationlabel.setText(String.format(getAttendedQuestion.get(i).getAnswer()));
                locationlabel.setKeyListener(null);
                locationlabel.setCursorVisible(false);
                locationlabel.setPressed(false);
                locationlabel.setFocusable(false);
                locationlabel.setTextColor(context.getResources().getColor(R.color.divider));
                parentLayout.addView(child);


            }

        }
    }

    private void setDynamicNameAccordingly(View rootView, String answer) {
        TextView textView= rootView.findViewById(R.id.userName);
        if (!answer.isEmpty())
            textView.setText(answer);


    }

}
