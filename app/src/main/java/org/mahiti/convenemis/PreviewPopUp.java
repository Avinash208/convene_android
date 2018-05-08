package org.mahiti.convenemis;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.network.SurveyGridInlineInterface.surveyQuestionPreviewInterface;
import org.mahiti.convenemis.utils.Constants;

import java.util.List;

public class PreviewPopUp {

    private static final String PREVIEWTITLE = "Preview ";
    private  LinearLayout parentLayout;
    private Button backToSurvey;
    private Button submitSurvey;

    /**
     * this constructer for create the preview the Question and answer .
     * @param activity
     * @param surveyPrimaryKeyId
     * @param dbOpenHelper
     * @param nextB
     * @param previousButton
     * @param surveysId
     */
    public  void showPreviewPopUp(final SurveyQuestionActivity activity, String surveyPrimaryKeyId, ConveneDatabaseHelper dbOpenHelper, final Button nextB, final Button previousButton, int surveysId) {

        surveyQuestionPreviewInterface surveyQuestionPreviewInterface = null;
        surveyQuestionPreviewInterface=activity;
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.preview_popup);
        dialog.setCancelable(false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.90);
        if (dialog.getWindow()!=null)
            dialog.getWindow().setLayout(width, height);
        dialog.setTitle(PREVIEWTITLE);
        initVariables(dialog);
        createDynamicQuestionSet(activity,surveyPrimaryKeyId,dbOpenHelper,preferences,surveysId);
        dialog.show();
        backToSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                nextB.setVisibility(View.VISIBLE);
                previousButton.setVisibility(View.VISIBLE);
            }
        });
        final org.mahiti.convenemis.network.SurveyGridInlineInterface.surveyQuestionPreviewInterface finalSurveyQuestionPreviewInterface = surveyQuestionPreviewInterface;
        submitSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                finalSurveyQuestionPreviewInterface.OnSuccessfulPreviewSubmit(true);
            }
        });
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

    /**
     * method to read the views.
     * @param dialog
     */
    private  void initVariables(Dialog dialog) {
        parentLayout = dialog.findViewById(R.id.dynamic_question_set);
        backToSurvey = dialog.findViewById(R.id.back_survey);
        submitSurvey = dialog.findViewById(R.id.submit_survey);
    }
}