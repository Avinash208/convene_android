package org.mahiti.convenemis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.List;


/**
 * Activity
 *
 */
public class ShowSurveyPreview extends AppCompatActivity implements View.OnClickListener{

    private  LinearLayout parentLayout;
    private String getSurveyPrimaryID;
    private String surveyId="";
    SharedPreferences showSurveyPreviewPreferences;
    public static final String isEditableKey = "isEditable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_survey_preview);
        parentLayout = findViewById(R.id.dynamic_question_set);
        TextView editTextView = findViewById(R.id.edit);
        TextView toolBarTitle= findViewById(R.id.toolbarTitle);
        LinearLayout backPressLinearLayout= findViewById(R.id.backPress);
        backPressLinearLayout.setOnClickListener(this);
        toolBarTitle.setText("Preview Response");
        Intent i= getIntent();
        getSurveyPrimaryID = i.getStringExtra("surveyPrimaryKey");
        surveyId=i.getStringExtra("survey_id");
        showSurveyPreviewPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ConveneDatabaseHelper dbOpenHelper = ConveneDatabaseHelper.getInstance(this, showSurveyPreviewPreferences.getString(Constants.CONVENE_DB, ""), showSurveyPreviewPreferences.getString("UID", ""));
        Logger.logV("", "Record is getting created + dbOpenHelper");
        createDynamicQuestionSet(ShowSurveyPreview.this,getSurveyPrimaryID, dbOpenHelper,surveyId);
        if (!("").equals(showSurveyPreviewPreferences.getString("recentPreviewRecord",""))){
            editTextView.setVisibility(View.VISIBLE);
        }else{
            editTextView.setVisibility(View.GONE);
        }
        SharedPreferences.Editor recentPreview= showSurveyPreviewPreferences.edit();
        recentPreview.putString("recentPreviewRecord","");
        recentPreview.apply();
        if (!showSurveyPreviewPreferences.getBoolean(isEditableKey,true))
            editTextView.setVisibility(View.INVISIBLE);
        else
            editTextView.setVisibility(View.VISIBLE);

        editTextView.setOnClickListener(view -> {
            Intent intent = new Intent(ShowSurveyPreview.this, SurveyQuestionActivity.class);
            intent.putExtra("SurveyId", getSurveyPrimaryID);
            Logger.logD("survey_id survey_id-->","-->"+surveyId);
            intent.putExtra("survey_id",String.valueOf(surveyId));
            startActivity(intent);
            finish();
        });
    }

    /**
     * method to create dynamic layout on fetching Question and answer .
     * @param context Activity context
     * @param surveyPrimaryKeyId   surveytable pri-key
     * @param dbOpenHelper  database
     * @param surveyId
     */
    private  void createDynamicQuestionSet(Context context, String surveyPrimaryKeyId, ConveneDatabaseHelper dbOpenHelper, String surveyId) {
        DBHandler dbHelper= new DBHandler(context);
        List<PreviewQuestionAnswerSet> getAttendedQuestion= dbHelper.getAttendedQuestion(surveyPrimaryKeyId,dbOpenHelper,showSurveyPreviewPreferences.getInt(Constants.SELECTEDLANGUAGE,0), Integer.parseInt(surveyId));
        if (!getAttendedQuestion.isEmpty()){
            for (int i=0;i<getAttendedQuestion.size();i++){
              /*
                questionParamLayout layout is for setting the answer from the  surveyDatabase.
                 Question and options are getting from convene database .with reference of dbhandler database .
                */
                LinearLayout.LayoutParams  surveyQuestionParamLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView surveyQuestionLabel = new TextView(context);
                surveyQuestionLabel.setLayoutParams(surveyQuestionParamLayout);
                surveyQuestionParamLayout.setMargins(4,4,4,0);
                surveyQuestionLabel.setPadding(10,0,0,10);
                surveyQuestionLabel.setTextSize(15);
                surveyQuestionLabel.setTypeface(Typeface.DEFAULT_BOLD);
                surveyQuestionLabel.setText(getAttendedQuestion.get(i).getQuestion());
                parentLayout.addView(surveyQuestionLabel);
              /*
                paramAnswer layout is for setting the answer from the  surveyDatabase.
                */
                LinearLayout.LayoutParams  paramSurveyAnswer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView answerSurveyLabel = new TextView(context);
                answerSurveyLabel.setLayoutParams(paramSurveyAnswer);
                paramSurveyAnswer.setMargins(4,0,4,4);
                answerSurveyLabel.setPadding(10,0,0,10);
                answerSurveyLabel.setText(String.format("Ans: %s", getAttendedQuestion.get(i).getAnswer()));
                answerSurveyLabel.setKeyListener(null);
                answerSurveyLabel.setCursorVisible(false);
                answerSurveyLabel.setPressed(false);
                answerSurveyLabel.setFocusable(false);
                parentLayout.addView(answerSurveyLabel);
            }

        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.backPress){
            onBackPressed();
        }
    }
}
