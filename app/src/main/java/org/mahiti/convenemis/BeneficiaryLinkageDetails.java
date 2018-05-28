package org.mahiti.convenemis;

/**
 * Created by mahiti on 28/05/18.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
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
        createDynamicQuestionSet(getContext(),surveyPrimaryKeyId,dbOpenHelper,surveyPreferences,surveysId);
        return rootView;
    }

    /**
     * method to read the views.
     * @param dialog
     */
    private  void initVariables(View dialog) {
        surveyPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbOpenHelper = ConveneDatabaseHelper.getInstance(getActivity(), surveyPreferences.getString(Constants.CONVENE_DB, ""), surveyPreferences.getString("UID", ""));
        parentLayout = dialog.findViewById(R.id.dynamic_question_set);
        backToSurvey = dialog.findViewById(R.id.back_survey);
        submitSurvey = dialog.findViewById(R.id.submit_survey);
        Intent surveyPrimaryKeyIntent = getActivity().getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));
        }
    }
    /**
     * method to create dynamic layout on fetching Question and answer .
     * @param context Activity context
     * @param surveyPrimaryKeyId   surveytable pri-key
     * @param dbOpenHelper  database
     * @param preferences
     * @param surveysId
     */
    private  void createDynamicQuestionSet(Context context, String surveyPrimaryKeyId, ConveneDatabaseHelper dbOpenHelper, SharedPreferences preferences, int surveysId) {
        DBHandler dbHelper= new DBHandler(context);
        List<PreviewQuestionAnswerSet> getAttendedQuestion= dbHelper.getAttendedQuestion(surveyPrimaryKeyId,dbOpenHelper,preferences.getInt(Constants.SELECTEDLANGUAGE,0),surveysId);
        if (!getAttendedQuestion.isEmpty()){
            for (int i=0;i<getAttendedQuestion.size();i++){
              /*
                questionParamLayout layout is for setting the answer from the  surveyDatabase.
                 Question and options are getting from convene database .with reference of dbhandler database .
                */
                LinearLayout.LayoutParams  questionParamLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView questionLabel = new TextView(context);
                questionLabel.setLayoutParams(questionParamLayout);
                questionParamLayout.setMargins(4,4,4,0);
                questionLabel.setPadding(10,0,0,10);
                questionLabel.setTextSize(15);
                questionLabel.setTypeface(Typeface.DEFAULT_BOLD);
                questionLabel.setText(getAttendedQuestion.get(i).getQuestion());
                parentLayout.addView(questionLabel);
              /*
                paramAnswer layout is for setting the answe#r from the  surveyDatabase.
                */
                LinearLayout.LayoutParams  paramAnswer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView answerLabel = new TextView(context);
                answerLabel.setLayoutParams(paramAnswer);
                paramAnswer.setMargins(4,0,4,4);
                answerLabel.setPadding(10,0,0,10);
                answerLabel.setText(String.format("Ans: %s", getAttendedQuestion.get(i).getAnswer()));
                answerLabel.setKeyListener(null);
                answerLabel.setCursorVisible(false);
                answerLabel.setPressed(false);
                answerLabel.setFocusable(false);
                parentLayout.addView(answerLabel);
            }

        }
    }

}
