package org.mahiti.convenemis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.AssesmentBean;
import org.mahiti.convenemis.BeenClass.Page;
import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.DataBaseMapperClass;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * Activity
 */
public class ShowSurveyPreview extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout parentLayout;
    private String getSurveyPrimaryID;
    private int surveyId = -1;
    SharedPreferences showSurveyPreviewPreferences;
    private ConveneDatabaseHelper conveneDatabaseHelper;
    private Context context;
    DBHandler dbHelper;
    Map<String, String> responses = null;
    Map<Integer, String> options = null;
    private Boolean isVisible;
    private static final String SAVE_TO_DRAFT_FLAG_KEY = "SaveDraftButtonFlag";
    private static final String SURVEY_ID_KEY = "survey_id";
    private static final String ISLOCATIONBASED = "isLocationBased";
    private static final String NOTLOCATIONBASED = "isNotLocationBased";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        net.sqlcipher.database.SQLiteDatabase.loadLibs(getApplicationContext());
        setContentView(R.layout.activity_show_survey_preview);
        parentLayout = findViewById(R.id.dynamic_question_set);
        TextView editTextView = findViewById(R.id.edit);
        TextView toolBarTitle = findViewById(R.id.toolbarTitle);
        LinearLayout backPressLinearLayout = findViewById(R.id.backPress);
        backPressLinearLayout.setOnClickListener(this);
        toolBarTitle.setText("Preview Response");
        Intent i = getIntent();

        context = ShowSurveyPreview.this;
        getSurveyPrimaryID = i.getStringExtra("surveyPrimaryKey");
        surveyId = i.getIntExtra("survey_id", -1);
        isVisible = i.getBooleanExtra("visibility", false);
        showSurveyPreviewPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setButtonClickListener();
        dbHelper = new DBHandler(context);
        net.sqlcipher.database.SQLiteDatabase.loadLibs(getApplicationContext());
        conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(this, showSurveyPreviewPreferences.getString(Constants.CONVENE_DB, ""), showSurveyPreviewPreferences.getString("UID", ""));
        Logger.logV("", "Record is getting created + dbOpenHelper");
        createDynamicQuestionSet(ShowSurveyPreview.this, getSurveyPrimaryID, surveyId);
        if (!("").equals(showSurveyPreviewPreferences.getString("recentPreviewRecord", ""))) {
            editTextView.setVisibility(View.VISIBLE);
        } else {
            editTextView.setVisibility(View.GONE);
        }
        SharedPreferences.Editor recentPreview = showSurveyPreviewPreferences.edit();
        recentPreview.putString("recentPreviewRecord", "");
        recentPreview.apply();
        editTextView.setOnClickListener(view -> {
            Utilities.setSurveyStatus(showSurveyPreviewPreferences, "edit");
            SharedPreferences.Editor editorSaveDraft = showSurveyPreviewPreferences.edit();
            editorSaveDraft.putBoolean(SAVE_TO_DRAFT_FLAG_KEY, true);
            editorSaveDraft.putBoolean(ISLOCATIONBASED, false);
            editorSaveDraft.putBoolean(NOTLOCATIONBASED, false);
            editorSaveDraft.apply();
            Intent intent = new Intent(this, SurveyQuestionActivity.class);
            intent.putExtra("SurveyId", getSurveyPrimaryID);
            intent.putExtra(SURVEY_ID_KEY, String.valueOf(surveyId));
            startActivity(intent);
            finish();


        });
    }

    private void setButtonClickListener() {
        if (!isVisible) {
            findViewById(R.id.btnSubmit).setVisibility(View.GONE);
            findViewById(R.id.btnCancel).setVisibility(View.GONE);
            return;
        }
        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        });
        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });
    }

    /**
     * method to create dynamic layout on fetching Question and answer .
     *
     * @param context            Activity context
     * @param surveyPrimaryKeyId surveytable pri-key
     * @param surveyId           param
     */
    private void createDynamicQuestionSet(Context context, String surveyPrimaryKeyId, int surveyId) {
        DBHandler dbHelper = new DBHandler(context);
        responses = dbHelper.getAttendedGridQuestion(surveyPrimaryKeyId, conveneDatabaseHelper, true);
        options = conveneDatabaseHelper.getAllOptions();
        List<PreviewQuestionAnswerSet> getAttendedQuestion = conveneDatabaseHelper.getAllQuestions(surveyId);
        if (!getAttendedQuestion.isEmpty()) {
            for (int i = 0; i < getAttendedQuestion.size(); i++) {
              /*
                questionParamLayout layout is for setting the answer from the  surveyDatabase.
                 Question and options are getting from convene database .with reference of dbhandler database .
                */
                LinearLayout.LayoutParams questionParamLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView questionLabel = new TextView(context);
                questionLabel.setLayoutParams(questionParamLayout);
                questionParamLayout.setMargins(0, 4, 4, 0);
                questionLabel.setPadding(8, 0, 0, 10);
                questionLabel.setTextSize(18);
                questionLabel.setTypeface(Typeface.DEFAULT_BOLD);
                questionLabel.setText(getAttendedQuestion.get(i).getQuestion());
                final View viewLine = getLayoutInflater().inflate(R.layout.spinnerline, parentLayout, false);
                parentLayout.addView(questionLabel);
                parentLayout.addView(viewLine);

                setAnswers(getAttendedQuestion.get(i), parentLayout, questionLabel, responses.get(String.valueOf(getAttendedQuestion.get(i).getQuestionID())));
            }

        }
    }


    private void setAnswers(PreviewQuestionAnswerSet previewQuestionAnswerSet, LinearLayout parentLayout, TextView questionLabel, String answer) {
        switch (Integer.parseInt(previewQuestionAnswerSet.getQuestionType())) {
            /*
             * 8 represent Image
             */
            case 8:
                setImageToIv(answer, parentLayout, questionLabel);
                break;

            case 14:
                setGridAnswers(previewQuestionAnswerSet, parentLayout);
                break;
            case 16:
                setInlineAnswers(previewQuestionAnswerSet, parentLayout);
                break;
            case 4:
                setAnswerToTv(answer, parentLayout, questionLabel);
                break;
            case 2:
                if (answer != null) {
                    String[] ansSet = answer.replace("[", "").replace("]", "").split(",");
                    answer = "";
                    for (int i=0;i<ansSet.length;i++){
                        if (i==0)
                            answer = answer + options.get(Integer.parseInt(ansSet[i].trim()));
                        else
                            answer = answer + "," + options.get(Integer.parseInt(ansSet[i].trim()));
                    }

                }
                setAnswerToTv(answer, parentLayout, questionLabel);
                break;
            default:
                setAnswerToTv(answer, parentLayout, questionLabel);
                break;

        }
    }


    private void setInlineAnswers(PreviewQuestionAnswerSet previewQuestionAnswerSet, LinearLayout parentLayout) {
        final List<AssesmentBean> mAssesmant = DataBaseMapperClass.getAssesements(previewQuestionAnswerSet.getQuestionID(), conveneDatabaseHelper.openDataBase(), 1);
        final View childCustomGrid = getLayoutInflater().inflate(R.layout.grid_custom, parentLayout, false);
        LinearLayout tableLayout = childCustomGrid.findViewById(R.id.custom_grid_table_layout);
        LinearLayout tableLayoutAnswerCard = childCustomGrid.findViewById(R.id.custom_grid_table_answer_layout);

        createInlineGridHeading(tableLayout, mAssesmant);
        createINlineAnswerList(tableLayoutAnswerCard,previewQuestionAnswerSet,mAssesmant);
        parentLayout.addView(childCustomGrid);
    }

    private void createINlineAnswerList(LinearLayout tableLayout, PreviewQuestionAnswerSet previewQuestionAnswerSet, List<AssesmentBean> mAssesmant) {
        List<Integer> getinlineRowCount = DataBaseMapperClass.getRowCount(previewQuestionAnswerSet.getQuestionID(), dbHelper.getdatabaseinstance(), getSurveyPrimaryID);
        for (int i=0;i<getinlineRowCount.size();i++){
           View inLineGridView = getLayoutInflater().inflate(R.layout.row_custom, tableLayout, false);
            LinearLayout linearLayout = (LinearLayout) inLineGridView.findViewById(R.id.linearLayout);
           TextView textView= new TextView(this);
            textView.setTextColor(getResources().getColor(R.color.black));
            for (AssesmentBean assesmentBean : mAssesmant) {
                String id = (i+1) + "@" + assesmentBean.getQid();
                TextView assessment = new TextView(context);
                assessment.setGravity(Gravity.LEFT);
                assessment.setTextSize(18);
                assessment.setText(responses.get(id));
                assessment.setTextColor(getResources().getColor(R.color.black));
                if ("C".equalsIgnoreCase(assesmentBean.getQtype()) && responses.get(id) != null) {
                    String[] ansSet = responses.get(id).replace("[", "").replace("]", "").split(",");
                    String answer = "";
                    for (String option : ansSet) {
                        option = option.trim();
                        answer = answer + "," + options.get(Integer.parseInt(option));
                    }
                    answer = answer.substring(1, answer.length());
                    assessment.setText(answer);

                }

                linearLayout.addView(assessment);

            }

           tableLayout.addView(inLineGridView);

        }
    }



    private void createInlineGridHeading(LinearLayout tableLayout, List<AssesmentBean> mAssesmant) {
       String getTempHeading="";
       TextView textView= new TextView(this);
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.grid_heading));
        for (int i = 0; i < mAssesmant.size(); i++) {
            if (i != 0)
               getTempHeading=getTempHeading + new StringBuilder().append(", ").append(mAssesmant.get(i).getAssessment()).toString();
            else
                getTempHeading= getTempHeading + mAssesmant.get(i).getAssessment();
        }
        textView.setText(getTempHeading);
        tableLayout.addView(textView);
    }





    private void setGridAnswers(PreviewQuestionAnswerSet previewQuestionAnswerSet, LinearLayout parentLayout) {

        final List<AssesmentBean> mAssesmant = DataBaseMapperClass.getAssesements(previewQuestionAnswerSet.getQuestionID(), conveneDatabaseHelper.openDataBase(), 1);
        final View childCustomGrid = getLayoutInflater().inflate(R.layout.grid_custom, parentLayout, false);
        final List<Page> mSubQuestions = DataBaseMapperClass.getSubquestionNew(previewQuestionAnswerSet.getQuestionID(), conveneDatabaseHelper.openDataBase(), -1);

        LinearLayout tableLayout = childCustomGrid.findViewById(R.id.custom_grid_table_layout);
        tableLayout.removeAllViews();
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout assessmentHeadersRow = new LinearLayout(context);
        assessmentHeadersRow.removeAllViews();
        for (int i = 0; i < mAssesmant.size() + 1; i++) {
            TextView assessment = new TextView(context);
            assessment.setLayoutParams(param2);
            if (i == 0) {
                assessment.setText("");
                assessment.setLayoutParams(new LinearLayout.LayoutParams(250, 80));
                assessmentHeadersRow.addView(assessment);
            } else {
                assessment.setText(mAssesmant.get(i - 1).getAssessment());
                assessment.setGravity(Gravity.CENTER);
                assessment.setSingleLine(true);
                assessment.setLayoutParams(new LinearLayout.LayoutParams(250, 80));
                assessmentHeadersRow.addView(assessment);


            }
        }
        tableLayout.addView(assessmentHeadersRow);

        for (int i = 0; i < mSubQuestions.size(); i++) {
            LinearLayout subQuestionRow = new LinearLayout(context);
            subQuestionRow.setGravity(Gravity.START);

            TextView subQuestionTextView = new TextView(context);
            subQuestionRow.setLayoutParams(param1);
            subQuestionTextView.setLayoutParams(new LinearLayout.LayoutParams(250, 80));
            subQuestionTextView.setText(mSubQuestions.get(i).getSubQuestion());
            subQuestionTextView.setGravity(Gravity.CENTER);
            subQuestionTextView.setTextColor(Color.WHITE);
            subQuestionTextView.setBackgroundColor(getResources().getColor(R.color.orange));
            subQuestionRow.addView(subQuestionTextView);
            for (int j = 0; j < mAssesmant.size(); j++) {

                TextView answerLabelGrid = new TextView(context);
                answerLabelGrid.setLayoutParams(new LinearLayout.LayoutParams(250, 80));

                answerLabelGrid.setKeyListener(null);
                answerLabelGrid.setCursorVisible(false);
                answerLabelGrid.setPressed(false);
                answerLabelGrid.setFocusable(false);
                answerLabelGrid.setGravity(Gravity.CENTER);
                answerLabelGrid.setBackgroundResource(R.drawable.textfieldbg);
                try {
                    /**int id1 = previewQuestionAnswerSet.getQuestionID();*/
                    int id3 = mAssesmant.get(j).getQid();
                    int id2 = mSubQuestions.get(i).getQuestionId();
                    String id = id2 + "@" + id3;
                    answerLabelGrid.setText(responses.get(id));
                    if ("C".equalsIgnoreCase(mAssesmant.get(j).getQtype()) && responses.get(id) != null) {
                        String[] ansSet = responses.get(id).replace("[", "").replace("]", "").split(",");
                        String answer = "";
                        for (String option : ansSet) {
                            answer = answer + "," + options.get(Integer.parseInt(option));
                        }

                        answerLabelGrid.setText(answer.substring(1, answer.length()));
                    }


                } catch (Exception e) {
                    Logger.logE("setGridAnswers", e.getMessage(), e);
                }
                subQuestionRow.addView(answerLabelGrid);
            }
            tableLayout.addView(subQuestionRow);
        }
        horizontalScrollView.setLayoutParams(param1);
        horizontalScrollView.addView(childCustomGrid);
        parentLayout.addView(horizontalScrollView);

    }

    private void setImageToIv(String answer, LinearLayout parentLayout, TextView questionLabel) {
        if (answer == null || answer.isEmpty()) {
            parentLayout.removeView(questionLabel);
            return;
        }
        ImageView answerLabel = new ImageView(context);
        int width = 200;
        int height = 200;
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
        parms.setMarginStart(20);
        answerLabel.setLayoutParams(parms);
        File imageFile = new File(answer);
        Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (null != image) {
            answerLabel.setImageBitmap(image);
        }
        parentLayout.addView(answerLabel);

    }

    private void setAnswerToTv(String answer, LinearLayout parentLayout, TextView questionLabel) {
        if (answer == null || answer.isEmpty()) {
            parentLayout.removeView(questionLabel);
            return;
        }
        LinearLayout.LayoutParams paramAnswer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView answerLabel = new TextView(context);
        answerLabel.setLayoutParams(paramAnswer);
        paramAnswer.setMargins(4, 0, 4, 4);
        answerLabel.setPadding(10, 0, 0, 10);
        answerLabel.setText(String.format("Ans: %s", answer));
        answerLabel.setKeyListener(null);
        answerLabel.setCursorVisible(false);
        answerLabel.setPressed(false);
        answerLabel.setFocusable(false);
        parentLayout.addView(answerLabel);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backPress) {
            onBackPressed();
        }
    }
}
