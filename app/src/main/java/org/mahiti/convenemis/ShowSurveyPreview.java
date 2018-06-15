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

        editTextView.setVisibility(View.INVISIBLE);
        editTextView.setOnClickListener(view -> {
            Intent intent = new Intent(ShowSurveyPreview.this, SurveyQuestionActivity.class);
            intent.putExtra("SurveyId", getSurveyPrimaryID);
            Logger.logD("survey_id survey_id-->", "-->" + surveyId);
            intent.putExtra("survey_id", String.valueOf(surveyId));
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
                questionParamLayout.setMargins(4, 4, 4, 0);
                questionLabel.setPadding(10, 0, 0, 10);
                questionLabel.setTextSize(15);
                questionLabel.setTypeface(Typeface.DEFAULT_BOLD);
                questionLabel.setText(getAttendedQuestion.get(i).getQuestion());
                parentLayout.addView(questionLabel);

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
            /*case 16:
                setInlineAnswers(previewQuestionAnswerSet, parentLayout);
                break;*/
            case 4:
                  setAnswerToTv(answer, parentLayout, questionLabel);
                break;
            case 2:
                if(answer != null)
                {
                    String[] ansSet = answer.replace("[", "").replace("]", "").split(",");
                    answer = "";
                    for (String option : ansSet) {
                        answer = answer + "," + options.get(Integer.parseInt(option.trim()));
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
        tableLayout.setOrientation(LinearLayout.HORIZONTAL);
        createQuestionsForInline(tableLayout, mAssesmant);
        createAnswersForInline(tableLayout, previewQuestionAnswerSet, mAssesmant);
        parentLayout.addView(childCustomGrid);
    }

    private void createAnswersForInline(LinearLayout tableLayout, PreviewQuestionAnswerSet previewQuestionAnswerSet, List<AssesmentBean> mAssesmant) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout assessmentAnswersRow = new LinearLayout(context);
        assessmentAnswersRow.removeAllViews();
        assessmentAnswersRow.setOrientation(LinearLayout.VERTICAL);

        for (AssesmentBean assesmentBean : mAssesmant) {

            int id1 = previewQuestionAnswerSet.getQuestionID();
            String id = id1 + "@" + assesmentBean.getQid();
            TextView assessment = new TextView(context);
            assessment.setLayoutParams(layoutParams);
            assessment.setText(responses.get(id));
            assessment.setGravity(Gravity.CENTER);
            assessment.setLayoutParams(new LinearLayout.LayoutParams(150, 70));
            assessmentAnswersRow.addView(assessment);

        }
        tableLayout.addView(assessmentAnswersRow);

    }

    private void createQuestionsForInline(LinearLayout tableLayout, List<AssesmentBean> mAssesmant) {
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout assessmentHeadersRow = new LinearLayout(context);
        assessmentHeadersRow.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        assessmentHeadersRow.removeAllViews();
        assessmentHeadersRow.setOrientation(LinearLayout.VERTICAL);
        for (AssesmentBean assesmentBean : mAssesmant) {

            TextView assessment = new TextView(context);
            assessment.setLayoutParams(param2);
            assessment.setText(assesmentBean.getAssessment());
            assessment.setGravity(Gravity.CENTER);
            assessment.setBackgroundColor(context.getResources().getColor(R.color.yellow));

            assessment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 70));
            assessmentHeadersRow.addView(assessment);

        }
        tableLayout.addView(assessmentHeadersRow);

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
                assessment.setLayoutParams(new LinearLayout.LayoutParams(400, 100));
                assessmentHeadersRow.addView(assessment);
            } else {
                assessment.setText(mAssesmant.get(i - 1).getAssessment());
                assessment.setGravity(Gravity.CENTER);
                assessment.setSingleLine(true);
                assessment.setLayoutParams(new LinearLayout.LayoutParams(400, 100));
                assessmentHeadersRow.addView(assessment);


            }
        }
        tableLayout.addView(assessmentHeadersRow);

        for (int i = 0; i < mSubQuestions.size(); i++) {
            LinearLayout subQuestionRow = new LinearLayout(context);
            subQuestionRow.setGravity(Gravity.START);

            TextView subQuestionTextView = new TextView(context);
            subQuestionRow.setLayoutParams(param1);
            subQuestionTextView.setLayoutParams(new LinearLayout.LayoutParams(400, 100));
            subQuestionTextView.setText(mSubQuestions.get(i).getSubQuestion());
            subQuestionTextView.setGravity(Gravity.CENTER);
            subQuestionTextView.setTextColor(Color.BLACK);
            subQuestionTextView.setBackgroundResource(R.drawable.textfieldbg);
            subQuestionRow.addView(subQuestionTextView);
            for (int j = 0; j < mAssesmant.size(); j++) {

                TextView answerLabelGrid = new TextView(context);
                answerLabelGrid.setLayoutParams(new LinearLayout.LayoutParams(400, 100));

                answerLabelGrid.setKeyListener(null);
                answerLabelGrid.setCursorVisible(false);
                answerLabelGrid.setPressed(false);
                answerLabelGrid.setFocusable(false);
                answerLabelGrid.setBackgroundResource(R.drawable.textfieldbg);
                try {
                    /**int id1 = previewQuestionAnswerSet.getQuestionID();*/
                    int id3 = mAssesmant.get(j).getQid();
                    int id2 = mSubQuestions.get(i).getQuestionId();
                    String id = id2 + "@" + id3;

                    if ("C".equalsIgnoreCase(mAssesmant.get(j).getQtype())  && responses.get(id) != null) {
                        String[] ansSet = responses.get(id).replace("[", "").replace("]", "").split(",");
                        String answer = "";
                        for (String option : ansSet) {
                            answer = answer + "," + options.get(Integer.parseInt(option));
                        }

                    }

                    answerLabelGrid.setText(responses.get(id));

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
