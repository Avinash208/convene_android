package org.fwwb.convene.convenecode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.AnswersPage;
import org.fwwb.convene.convenecode.BeenClass.AssesmentBean;
import org.fwwb.convene.convenecode.BeenClass.Page;
import org.fwwb.convene.convenecode.BeenClass.Response;
import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;
import org.fwwb.convene.convenecode.api.FilterCallBackInterface;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.DataBaseMapperClass;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.network.SurveyGridInlineInterface.surveyQuestionGridInlineInterface;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.PreferenceConstants;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static org.fwwb.convene.convenecode.SurveyQuestionActivity.MY_PREFS_NAME;
import static org.fwwb.convene.convenecode.utils.Constants.GridResponseHashMap;
import static org.fwwb.convene.convenecode.utils.Constants.GridResponseHashMapKeys;
import static org.fwwb.convene.convenecode.utils.Constants.TAG;
import static org.fwwb.convene.convenecode.utils.Constants.assessmentListBasedOnSkip;
import static org.fwwb.convene.convenecode.utils.Constants.buttonDynamicDateGrid;
import static org.fwwb.convene.convenecode.utils.Constants.dateButton;
import static org.fwwb.convene.convenecode.utils.Constants.fillInlineHashMapKey;
import static org.fwwb.convene.convenecode.utils.Constants.fillInlineRow;
import static org.fwwb.convene.convenecode.utils.Constants.gridAssessmentMapDialog;
import static org.fwwb.convene.convenecode.utils.Constants.gridQuestionMapDialog;
import static org.fwwb.convene.convenecode.utils.Constants.listHashMapKey;
import static org.fwwb.convene.convenecode.utils.Constants.mainAcessmentList;
import static org.fwwb.convene.convenecode.utils.Constants.mainGridAssessmentMapDialog;

import static org.fwwb.convene.convenecode.utils.Constants.notTaggedQids;
import static org.fwwb.convene.convenecode.utils.Constants.responselistBasedOnSkip;
import static org.fwwb.convene.convenecode.utils.Constants.rowInflater;

import static org.fwwb.convene.convenecode.utils.Constants.skipPageCount;
import static org.fwwb.convene.convenecode.utils.Constants.skipPageView;


public class SupportClass {

    private static final String LOGGER_TAG = "SupportClass";
    private static List<String> GridlistHashMapKey = new ArrayList<>();
    public static int storeInlineindexPostion=1;
    public  static  String  addOREditFlag="add";
    public  static  int  deleteResponseFlag=0;

    List<Spinner> storeAllDynamicSpinner = new ArrayList<>();
    Activity activity;
    Context context;
    private AlertDialog b;

    /**
     * @param context
     * @param db
     * @param handler
     * @param surveyId
     */
    public void backButtonFunction(final SurveyQuestionActivity context, final net.sqlcipher.database.SQLiteDatabase db, final DBHandler handler, final String surveyId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alertDialogBuilder.setMessage(R.string.exitSurvey);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Log.d(LOGGER_TAG, surveyId + "");
                        RemoveTask removeTask = new RemoveTask(db, handler, surveyId, context);
                        removeTask.execute();
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Logger.logV("", "backButtonFunction");
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void createDialogFOrGrid(Page page, View child, SurveyQuestionActivity surveyQuestionActivity,
                                    SQLiteDatabase database, int getCurrentGridQuestionID, List<Response> getAnsweredResponse,
                                    int survey_ID) {
        skipPageCount = 0;
        skipPageView.clear();
        assessmentListBasedOnSkip.clear();
        List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(String.valueOf(getCurrentGridQuestionID) + "_ASS");
        Page QuestionPageBean = gridQuestionMapDialog.get(String.valueOf(getCurrentGridQuestionID) + "_QUESTION");
        mainGridAssessmentMapDialog.put(page.getQuestionNumber() + "_ASS", MAssesmant);
        gridQuestionMapDialog.put(page.getQuestionNumber() + "_QUESTION", page);
        List<AssesmentBean> mAssesmantmain = modifiedAssessementListOnSKIP(MAssesmant, database);
        for (int i = 0; i < MAssesmant.size(); i++) {
            mainAcessmentList.add(String.valueOf(MAssesmant.get(i).getQid()));
        }
        createNotTaggedSkipIds(MAssesmant, database, survey_ID);
        List<Response> getResponse = prePareResponseList(mAssesmantmain, responselistBasedOnSkip);
        showChangeLangDialog(getResponse, mAssesmantmain, surveyQuestionActivity, surveyQuestionActivity, QuestionPageBean, database, child, 14, page, "2");
    }
    private static void createNotTaggedSkipIds(List<AssesmentBean> MAssesmant, SQLiteDatabase database, int survey_ID) {
        notTaggedQids.clear();
        for (int k = 0; k < MAssesmant.size(); k++) {
            notTaggedQids.add(String.valueOf(MAssesmant.get(k).getQid()));
        }
        for (int i = 0; i < MAssesmant.size(); i++) {
            String getSkipStatus = DataBaseMapperClass.getSkipCodeIfExist(MAssesmant.get(i), database,survey_ID);
            if (!getSkipStatus.isEmpty()) {
                String[] splitSkipIds = getSkipStatus.split(",");
                notTaggedQids.removeAll(Arrays.asList(splitSkipIds));
                Logger.logD("notTaggedQids", "notTaggedQids sorted list " + notTaggedQids.toString());
            }
        }
    }
    /**
     * @param context
     * @param activity
     * @param currentQuestionPage
     * @param database
     * @param child
     * @param gridType
     * @param questionPageBean
     */
    public  void showChangeLangDialog(final List<Response> mResponse, final List<AssesmentBean> mAssesmant, final Context context, final SurveyQuestionActivity activity, final Page currentQuestionPage,
                                            final SQLiteDatabase database, final View child, final int gridType,
                                            final Page questionPageBean, final String skipCode) {
        List<AssesmentBean> gridDisplayPageList = new ArrayList<>();
        final surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        surveyQuestionGridInlineInterface = activity;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.form_dialog, null);
        final LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dynamic_mini_grid);
        final LinearLayout optionwidgetLL = layout;
        optionwidgetLL.removeAllViews();
        List<View> addView = new ArrayList<>();
        final ScrollView scrollView = (ScrollView) dialogView.findViewById(R.id.dialogScroll);
        List<AssesmentBean> mainQList = mAssesmant;
        int assessemntSize = mainQList.size();
        Logger.logV("MainList", "MainListSize" + mainQList.size());
        List<AssesmentBean> tempList = new ArrayList<>();
        tempList.add(mainQList.get(0));
        addView.clear();

        renderAllTypeOfWidget(assessemntSize, context, mAssesmant, gridType, questionPageBean, activity,
                optionwidgetLL, mResponse, addView, layout, database, preferences);

        dialogBuilder.setView(dialogView);
        final TextView txt = (TextView) dialogView.findViewById(R.id.assessmentquestion);
        txt.setTypeface(null, Typeface.BOLD);
        final Button saveAndCreate = (Button) dialogView.findViewById(R.id.saveandcreate);
        final Button saveAndExit = (Button) dialogView.findViewById(R.id.saveandexit);
        if (gridType == 14) {
            saveAndCreate.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            txt.setText(questionPageBean.getSubQuestion());
        } else {
            txt.setVisibility(View.GONE);
        }
        if (!"".equals(skipCode)) {
            saveAndExit.setText("Next");
            saveAndCreate.setText("Previous");
            saveAndCreate.setVisibility(View.VISIBLE);

        } else {
            saveAndExit.setText("Save");
        }
        final int currentQuestionNumber = currentQuestionPage.getQuestionNumber();

        dialogBuilder.setTitle(currentQuestionPage.getQuestion());
        saveAndCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saveAndCreate.getText().equals("Previous")) {
                    rowInflater++;
                    storeInlineindexPostion=rowInflater;
                    final List<Page> mSubQuestions = new ArrayList<>();
                    Page page = new Page(rowInflater, 1998, 200, "N", "N", false, null, 0, String.valueOf(rowInflater + 1), "", "", "", "1", "");
                    mSubQuestions.add(page);
                    /**
                     * module to validate the all the Field based on the layout filled . returing back the answerd list
                     */
                    List<String> answered = methodToValidationField(currentQuestionNumber, context, addView, database, 16, page);
                    if (answered != null && mAssesmant.size() == answered.size()) {
                        Logger.logD(LOGGER_TAG, "the all field validation pass");
                        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        int survey_ID = prefs.getInt("survey_id", 0);
                        List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
                        List<Response> responselist = new ArrayList<>();
                        List<AnswersPage> mOptions;
                        for (int i = 0; i < MAssesmant.size(); i++) {
                            if (MAssesmant.get(i).getQtype().equals("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                            } else {
                                mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 1));
                            }
                            Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, mSubQuestions.get(0).getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), MAssesmant.get(i).getQtype());
                            responselist.add(response);
                            Logger.logD("GridValues in Response", responselist.get(i).toString());
                        }
                        fillInlineRow.put(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()), responselist);
                        listHashMapKey.add(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()));
                        fillInlineHashMapKey.put(String.valueOf(currentQuestionNumber), listHashMapKey);
                        Logger.logD("Size if the filled Complet in Response in hashMap", listHashMapKey.size() + "");
                        Logger.logD("Size if the filled Complet in Response in hashMap", listHashMapKey.size() + "");
                        ClearALlViews(addView, scrollView, mAssesmant);
                        ToastUtils.displayToast("Added one response", context);
                        if (fillInlineRow.size() > 0) {
                            saveAndExit.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Logger.logD(LOGGER_TAG, "the field validation failed");
                    }

                } else {
                    ToastUtils.displayToast("No Previous question", activity);
                }
            }
        });
        b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();
        saveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!saveAndExit.getText().equals("Next")) {
                    try {
                        if (gridType == 16) {
                            rowInflater++;
                            storeInlineindexPostion=rowInflater;
                            final List<Page> mSubQuestions = new ArrayList<>();
                            final Page page = new Page(rowInflater, 1998, 200, "N", "N", false, null, 0, String.valueOf(rowInflater + 1), "", "", "", "1", "");
                            mSubQuestions.add(page);
                            /**
                             * module to validate the all the Field based on the layout filled . returing back the answerd list
                             */
                            List<String> answered = methodToValidationField(currentQuestionNumber, context, addView, database, 16, page);
                            if (answered != null && mAssesmant.size() == answered.size()) {
                                Logger.logD(LOGGER_TAG, "the all field validation pass");
                                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                int survey_ID = prefs.getInt("survey_id", 0);
                                List<AssesmentBean> mAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
                                List<Response> responselist = new ArrayList<>();
                                List<AnswersPage> mOptions;
                                for (int i = 0; i < mAssesmant.size(); i++) {
                                    if (mAssesmant.get(i).getQtype().equals("R") || mAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                        mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                                    } else if (mAssesmant.get(i).getQtype().equalsIgnoreCase("T")) {
                                        mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                                    } else {
                                        mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                                    }
                                    Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, mSubQuestions.get(0).getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), mAssesmant.get(i).getQtype());
                                    responselist.add(response);
                                    Logger.logD("GridValues in Response", responselist.get(i).toString());
                                }
                                fillInlineRow.put(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()), responselist);
                                listHashMapKey.add(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()));
                                Logger.logD("listHashMapKey--<<>>>", listHashMapKey.toString() + "");
                                Logger.logD("listHashMapKey--<<>>>", fillInlineRow.size() + "");

                                fillInlineHashMapKey.put(String.valueOf(currentQuestionNumber), listHashMapKey);
                                Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                                b.dismiss();
                                ToastUtils.displayToast("Added one response", context);
                                surveyQuestionGridInlineInterface.OnSuccessfullGridInline(fillInlineRow, child, currentQuestionNumber, fillInlineHashMapKey, gridType);

                            } else {
                                Logger.logD(LOGGER_TAG, "Invalid input");
                            }
                        } else {
                            Logger.logD(LOGGER_TAG, "im in the Save to exit option with  GRid Question ");
                            List<String> answered = methodToValidationField(currentQuestionNumber, context, addView, database, 14, questionPageBean);
                            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            int survey_ID = prefs.getInt("survey_id", 0);
                            if (answered != null && answered.size() == mAssesmant.size()) {
                                Logger.logD(LOGGER_TAG, "the all field validation pass");
                                List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
                                List<Response> responselist = new ArrayList<>();
                                List<AnswersPage> mOptions;
                                for (int i = 0; i < MAssesmant.size(); i++) {
                                    if (questionPageBean.getAnswer().equalsIgnoreCase("N")) {
                                        if (MAssesmant.get(i).getQtype().equalsIgnoreCase("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                                        } else if (MAssesmant.get(i).getQtype().equalsIgnoreCase("T")) {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                                        } else {
                                            mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 0));
                                        }
                                        if (mOptions.size() > 0) {
                                            Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, questionPageBean.getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), MAssesmant.get(i).getQtype());
                                            responselist.add(response);
                                        }
                                    } else {
                                        if (questionPageBean.getAnswer().equalsIgnoreCase("R")) {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(questionPageBean.getQuestionId(), database, answered.get(i), true);
                                        } else {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(questionPageBean.getQuestionId(), database, "", false);
                                        }
                                        if (mOptions.size() > 0) {
                                            Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, questionPageBean.getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), questionPageBean.getAnswer());
                                            responselist.add(response);
                                            Logger.logD("GridValues in Response", responselist.get(i).toString());

                                        }
                                    }

                                }
                                GridResponseHashMap.put(String.valueOf(currentQuestionNumber) + "_" + String.valueOf(questionPageBean.getQuestionId()), responselist);
                                GridlistHashMapKey.add(String.valueOf(currentQuestionNumber) + "_" + String.valueOf(questionPageBean.getQuestionId()));
                                GridResponseHashMapKeys.put(String.valueOf(currentQuestionNumber), GridlistHashMapKey);
                                Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                                b.dismiss();
                                ToastUtils.displayToast("Added one response", context);
                                surveyQuestionGridInlineInterface.OnSuccessfullGridInline(GridResponseHashMap, child, currentQuestionNumber, GridResponseHashMapKeys, gridType);

                            } else {
                                ToastUtils.displayToast("please fill Mandatory fields", context);

                            }
                        }

                    } catch (Exception e) {
                        Logger.logE("Exception", "Exception ....", e);
                    }
                } else {
                    try {

                        /**
                         * module to validate the all the Field based on the layout filled . returing back the answerd list
                         */
                        List<String> answeredtemp = methodToValidationField(currentQuestionNumber, context, addView, database, 16, questionPageBean);
                        boolean getLastQuestionChangesStatus= getLastQuestionChangesStatus(answeredtemp);
                        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        int survey_ID = prefs.getInt("survey_id", 0);
                        //    grid skip case 1
                        UpdateResponseToSkipBased(answeredtemp, mAssesmant, currentQuestionNumber, questionPageBean,
                                database, preferences, survey_ID, addView,gridType);

                        validatewithNextFunctionality(answeredtemp, mAssesmant, questionPageBean, currentQuestionNumber, database,
                                dialogView, addView, context, activity, currentQuestionPage, child, preferences, survey_ID,gridType);


                    } catch (Exception e) {
                        Logger.logE("Exception", "in", e);

                    }
                }

            }

        });


    }

    private boolean getLastQuestionChangesStatus(List<String> answeredtemp) {

        List<AssesmentBean> getPreviousResponseList= new ArrayList<>();
        if (skipPageCount==0){
            getPreviousResponseList  = assessmentListBasedOnSkip.get(String.valueOf(skipPageCount));
            if (getPreviousResponseList !=null && !getPreviousResponseList.isEmpty()){
               int  getAnswerCode=  getPreviousResponseList.get(getPreviousResponseList.size()-1).getQid();
              if (responselistBasedOnSkip !=null && !responselistBasedOnSkip.isEmpty())   {
                  Response getAnswerCodeResponse= responselistBasedOnSkip.get(String.valueOf(getAnswerCode));
                  if (!getAnswerCodeResponse.getAnswer().equals(answeredtemp.get(answeredtemp.size()-1))){
                      deleteResponseFlag=0;
                  }  else{
                      deleteResponseFlag=1;
                  }
              }
              
            }
        } else{
            getPreviousResponseList  = assessmentListBasedOnSkip.get(String.valueOf(skipPageCount));
            if (getPreviousResponseList !=null && !getPreviousResponseList.isEmpty()){
                int  getAnswerCode=  getPreviousResponseList.get(getPreviousResponseList.size()-1).getQid();
                if (responselistBasedOnSkip !=null && !responselistBasedOnSkip.isEmpty())   {
                    Response getAnswerCodeResponse= responselistBasedOnSkip.get(String.valueOf(getAnswerCode));
                    if (!getAnswerCodeResponse.getAnswer().equals(answeredtemp.get(answeredtemp.size()-1))){
                        deleteResponseFlag=0;
                    }  else{
                        deleteResponseFlag=1;
                    }
                }
                
            }
        }

        
        return true;
    }

    private  void validatewithNextFunctionality(List<String> answeredtemp, List<AssesmentBean> mAssesmant, Page questionPageBean,
                                                int currentQuestionNumber, SQLiteDatabase database, View dialogView, List<View> addView,
                                                Context context, SurveyQuestionActivity activity, Page currentQuestionPage, View child, SharedPreferences preferences, int survey_ID, int gridType) {
        if (answeredtemp != null) {
            String getSkipCode = answeredtemp.get(answeredtemp.size() - 1);
            AssesmentBean getQuestionCode = mAssesmant.get(mAssesmant.size() - 1);
            List<AnswersPage> mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(getQuestionCode.getQid(), database, getSkipCode, true);
            String skipcode = "";
            if (!mOptions.isEmpty())
                skipcode = mOptions.get(0).getValidation();
            if (skipcode == null)
                skipcode = "";

            List<AssesmentBean> MainMAssesmant = mainGridAssessmentMapDialog.get(String.valueOf(questionPageBean.getQuestionNumber()) + "_ASS");
            if (!"".equals(skipcode)) {
                List<Response> responselist = new ArrayList<>();
                if (mOptions.size() > 0) {
                    for (int j = 0; j < mAssesmant.size(); j++) {
                        Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(j), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, 0, String.valueOf(survey_ID), mAssesmant.get(j).getQid(), mOptions.get(0).getId(), answeredtemp.get(j));
                        responselist.add(response);
                    }
                }
                List<AssesmentBean> mAssesmantToDisplay = new ArrayList<>();
                String[] splitSkipCode = skipcode.split(",");
                for (int i = 0; i < MainMAssesmant.size(); i++) {
                    for (String aSplitSkipCode : splitSkipCode) {
                        if (MainMAssesmant.get(i).getQid() == Integer.parseInt(aSplitSkipCode)) {
                            mAssesmantToDisplay.add(MainMAssesmant.get(i));
                        }
                    }


                }
                //gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", mAssesmantToDisplay);
                List<AssesmentBean> mAssesmantmain = modifiedAssessementListOnSKIP(mAssesmantToDisplay, database);
                if (!mAssesmantmain.isEmpty()) {
                    ClearAllDialogViews(dialogView, addView);
                    List<Response> getResponse = prePareResponseList(mAssesmantmain, responselistBasedOnSkip);
                    RenderNextSetOfQuestion(getResponse, mAssesmantmain, context, activity, currentQuestionPage, database, child, gridType,
                            questionPageBean, "2", dialogView, preferences, addView);
                } else {
                    Button saveAndExit = (Button) dialogView.findViewById(R.id.saveandexit);
                    saveAndExit.setText("Save");
                   // ToastUtils.displayToast("Seems to be no  question", activity);

                    saveAndExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GridResponseHashMap.put(String.valueOf(currentQuestionPage.getQuestionNumber()) + "_" + String.valueOf(questionPageBean.getQuestionId()), responselist);
                            GridlistHashMapKey.add(String.valueOf(currentQuestionPage.getQuestionNumber()) + "_" + String.valueOf(questionPageBean.getQuestionId()));
                            GridResponseHashMapKeys.put(String.valueOf(currentQuestionPage.getQuestionNumber()), GridlistHashMapKey);
                            Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                              b.dismiss();
                              ToastUtils.displayToast("Added one response", context);
                        }
                    });
                    saveAndExit.performClick();
                }

            } else {
                List<Response> responselist = new ArrayList<>();
                if (mOptions.size() > 0) {
                    for (int j = 0; j < mAssesmant.size(); j++) {
                        Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(j), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, 0, String.valueOf(survey_ID), mAssesmant.get(j).getQid(), mOptions.get(0).getId(), answeredtemp.get(j));
                        responselist.add(response);
                    }
                } else {
                    for (int j = 0; j < mAssesmant.size(); j++) {
                        Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(j), "", "0", currentQuestionNumber, 0, String.valueOf(survey_ID), mAssesmant.get(j).getQid(), 0, answeredtemp.get(j));
                        responselist.add(response);
                    }
                }
                List<AssesmentBean> mAssesmantToDisplay = new ArrayList<>();
                int getcurrentIndex = mAssesmant.get(mAssesmant.size() - 1).getQid();
                for (int i = 0; i < MainMAssesmant.size(); i++) {
                    AssesmentBean assessment = MainMAssesmant.get(i);
                    if (assessment.getQid() > getcurrentIndex && notTaggedQids.contains(String.valueOf(assessment.getQid())))
                        mAssesmantToDisplay.add(assessment);
                }
                //gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", mAssesmantToDisplay);
                List<AssesmentBean> mAssesmantmain = modifiedAssessementListOnSKIP(mAssesmantToDisplay, database);

                if (!mAssesmantmain.isEmpty()) {
                    ClearAllDialogViews(dialogView, addView);
                    List<Response> getResponse = prePareResponseList(mAssesmantmain, responselistBasedOnSkip);
                    RenderNextSetOfQuestion(getResponse, mAssesmantmain, context, activity, currentQuestionPage, database, child, gridType,
                            questionPageBean, "2", dialogView, preferences, addView);
                } else {
                    Button saveAndExit = (Button) dialogView.findViewById(R.id.saveandexit);
                    saveAndExit.setText("Save");
                    // ToastUtils.displayToast("Seems to be no  question", activity);

                    saveAndExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (gridType==14){
                                List<Response> getCompleteResponseList=bindToStoreToFinialHashMap(responselistBasedOnSkip);
                                GridResponseHashMap.put(String.valueOf(currentQuestionPage.getQuestionNumber()) + "_" + String.valueOf(questionPageBean.getQuestionId()), getCompleteResponseList);
                                GridlistHashMapKey.add(String.valueOf(currentQuestionPage.getQuestionNumber()) + "_" + String.valueOf(questionPageBean.getQuestionId()));
                                GridResponseHashMapKeys.put(String.valueOf(currentQuestionPage.getQuestionNumber()), GridlistHashMapKey);
                                Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                                b.dismiss();
                                ToastUtils.displayToast("Added one response", context);
                                surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface = activity;
                                surveyQuestionGridInlineInterface.OnSuccessfullGridInline(GridResponseHashMap, child, currentQuestionPage.getQuestionNumber(), GridResponseHashMapKeys, 14);
                            }  else if (gridType==16){
                                List<Response> getCompleteResponseList=bindToStoreToFinialHashMap(responselistBasedOnSkip);
                                rowInflater++;
                                storeInlineindexPostion=rowInflater;
                                List<Page> mSubQuestions = new ArrayList<>();
                                Page page = new Page(rowInflater, 1998, 200, "N", "N", false, null, 0, String.valueOf(rowInflater + 1), "", "", "", "1", "");
                                mSubQuestions.add(page);
                                fillInlineRow.put(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()), getCompleteResponseList);
                                listHashMapKey.add(currentQuestionNumber + "_" + String.valueOf(mSubQuestions.get(0).getQuestionId()));
                                Logger.logD("listHashMapKey--<<>>>", listHashMapKey.toString() + "");
                                Logger.logD("listHashMapKey--<<>>>", fillInlineRow.size() + "");

                                fillInlineHashMapKey.put(String.valueOf(currentQuestionNumber), listHashMapKey);
                                Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                                b.dismiss();
                                ToastUtils.displayToast("Added one response", context);
                                surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface = activity;
                                surveyQuestionGridInlineInterface.OnSuccessfullGridInline(fillInlineRow, child, currentQuestionNumber, fillInlineHashMapKey, gridType);

                            }

                        }
                    });
                    saveAndExit.performClick();
                }

            }
        }
    }

    private List<Response> bindToStoreToFinialHashMap(Map<String, Response> responselistBasedOnSkip) {
       List<Response> getTempList= new ArrayList<>();
        Set<String> getMapKeys= responselistBasedOnSkip.keySet();
        if (!getMapKeys.isEmpty()){
            for (String eachKeys: getMapKeys){
                Response response= responselistBasedOnSkip.get(eachKeys);
                getTempList.add(response);
            }
        }
        return getTempList;
    }
    private static void renderAllTypeOfWidget(int assessemntSize, Context context, List<AssesmentBean> mAssesmant,
                                              int gridType, Page questionPageBean, SurveyQuestionActivity activity, LinearLayout optionwidgetLL,
                                              List<Response> mResponse, List<View> addView, LinearLayout layout, SQLiteDatabase database,
                                              SharedPreferences preferences) {
        optionwidgetLL.removeAllViews();
        for (int i = 1; i < assessemntSize + 1; i++) {
            int subCount = i;
            LinearLayout.LayoutParams paramassessmentQuestionText;
            paramassessmentQuestionText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView assessmentQuestionText = new TextView(context);
            if (mAssesmant.get(i - 1).getMandatory() == 1) {
                setRedStar(assessmentQuestionText, mAssesmant.get(i - 1).getAssessment());
            } else {
                assessmentQuestionText.setText(mAssesmant.get(i - 1).getAssessment());
            }
            assessmentQuestionText.setLayoutParams(paramassessmentQuestionText);
            paramassessmentQuestionText.setMargins(10, 10, 10, 10);
            String priorityQtype;
            String getGroupVaidation = "";
            if (gridType != 16) {
                if (!questionPageBean.getAnswer().equals("N")) {
                    priorityQtype = questionPageBean.getAnswer();
                    getGroupVaidation = questionPageBean.getValidation();
                    Logger.logD("priorityQtype", "priorityQtype in SubQuestion" + priorityQtype);
                    Logger.logD("priorityQtype", "getGroupVaidation in SubQuestion" + getGroupVaidation);
                } else {
                    priorityQtype = mAssesmant.get(i - 1).getQtype();
                    getGroupVaidation = mAssesmant.get(i - 1).getGroupValidation();
                    Logger.logD("priorityQtype", "priorityQtype in Assessment" + priorityQtype);
                }
            } else {
                if (!mAssesmant.get(i - 1).getQtype().equalsIgnoreCase("N")) {
                    priorityQtype = mAssesmant.get(i - 1).getQtype();
                    getGroupVaidation = mAssesmant.get(i - 1).getGroupValidation();
                } else {
                    priorityQtype = questionPageBean.getAnswer();
                    getGroupVaidation = mAssesmant.get(i - 1).getGroupValidation();
                }
            }
            switch (priorityQtype) {
                case "T":
                    View editTextView = activity.getLayoutInflater().inflate(R.layout.edittext, optionwidgetLL, false);//child.xml
                    TextView question = editTextView.findViewById(R.id.mainQuestion);
                    TextInputLayout v = (TextInputLayout) editTextView.findViewById(R.id.textInput);
                   if (mAssesmant.get(i - 1).getMandatory() == 1) {
                       SupportClass.setRedStar(question, mAssesmant.get(i - 1).getAssessment());
                    } else {
                       question.setText(mAssesmant.get(i - 1).getAssessment());
                   }


                    v.setHintTextAppearance(R.style.hintstyle);
                    EditText editView = (EditText) editTextView.findViewById(R.id.ans_text);
                    editView.setSingleLine(true);
                    editView.setHintTextColor(activity.getResources().getColor(R.color.black));
                    question.setFocusable(true);
                    editView.setTextColor(Color.BLACK);
                    subCount = subCount + 20 + 2 + i;
                    editView.setId(subCount);
                    if (mResponse != null && mResponse.size() > i - 1) {
                        editView.setText(mResponse.get(i - 1).getAnswer());
                    }
                    String validaiton = "";
                    if (gridType != 16) {
                        if (!questionPageBean.getAnswer().equals("N")) {
                            validaiton = DataBaseMapperClass.getValidationExpressionFromQuestion(questionPageBean.getQuestionId(), database);
                        } else {
                            validaiton = mAssesmant.get(i - 1).getGroupValidation();
                        }

                    } else {
                        validaiton = mAssesmant.get(i - 1).getGroupValidation();
                    }
                    if (!"".equals(validaiton)) {
                        Logger.logD("mathString", "mathString" + validaiton);
                        String[] array;
                        if (validaiton.length() > 1) {
                            array = validaiton.split(":");
                            if ("R".equalsIgnoreCase(array[0]) || "o".equalsIgnoreCase(array[0])) {
                                int maxLength;
                                InputFilter[] FilterArray;
                                switch (array[1]) {
                                    case "N":
                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "D":
                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);

                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);

                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);

                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                optionwidgetLL.addView(editTextView);
                                addView.add(editView);

                                editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editView.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(editTextView);
                        addView.add(editView);


                    }
                    break;
                case "R":
                    layout.addView(assessmentQuestionText);
                    int s = 1;
                    List<AnswersPage> mOptions;
                    if (!mAssesmant.get(i - 1).getQtype().equalsIgnoreCase("N"))
                        mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    else
                        mOptions = DataBaseMapperClass.getOptionsAnswersForSubquestionBased(questionPageBean.getQuestionId(), database);

                    RadioGroup radioGroup = new RadioGroup(context);
                    radioGroup.setLayoutParams(new RadioGroup.LayoutParams(
                            390,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    radioGroup.setOrientation(LinearLayout.VERTICAL);
                    radioGroup.setGravity(Gravity.LEFT);
                    radioGroup.setId(i + 1000 + i + i + s);
                    Logger.logD("GroupId", radioGroup.getId() + "");
                    s++;
                    int buttons = mOptions.size();
                    for (int r = 1; r <= buttons; r++) {
                        RadioButton rbn = new RadioButton(context);
                        rbn.setTag(mAssesmant.get(i - 1).getQid());
                        int min = 20;
                        int max = 80;
                        int random = new Random().nextInt((max - min) + 1) + min;
                        rbn.setId(random);
                        Logger.logD("getRadioCode", rbn.getId() + "");
                        rbn.setText(mOptions.get(r - 1).getAnswer());
                        if (mResponse != null && mResponse.size() > i - 1) {
                            if (mResponse.get(i - 1).getAns_code().equals(mOptions.get(r - 1).getAnswerCode())) {
                                rbn.setChecked(true);

                            }
                        }

                        radioGroup.addView(rbn);
                    }
                    /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                            try {
                                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                                int getRadioCode = (int) checkedRadioButton.getTag();
                                Logger.logD("getRadioCode", getRadioCode + "");
                                boolean isChecked = checkedRadioButton.isChecked();
                            } catch (Exception e) {
                               Logger.logE(TAG,"renderAllTypeOfWidget",e);
                            }
                        }
                    });*/
                    View dividerLine = activity.getLayoutInflater().inflate(R.layout.divider_line, optionwidgetLL, false);

                    optionwidgetLL.addView(radioGroup);
                    optionwidgetLL.addView(dividerLine);
                    addView.add(radioGroup);

                    break;
                case "S":
                    layout.addView(assessmentQuestionText);
                    List<AnswersPage> mOptionsDroupdown;
                    if (!mAssesmant.get(i - 1).getQtype().equalsIgnoreCase("N")) {
                        mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                    } else {
                        mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(questionPageBean.getQuestionId(), database, preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                    }


                    List<String> optionText = new ArrayList<>();
                    Spinner spinner = new Spinner(context);

                    spinner.setLayoutParams(new RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    spinner.getBackground().setColorFilter(activity.getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_ATOP);
                    for (int d = 0; d < mOptionsDroupdown.size(); d++) {
                        optionText.add(mOptionsDroupdown.get(d).getAnswer());
                    }
                    ArrayAdapter<AnswersPage> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_multi_row_textview, mOptionsDroupdown);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);
                    spinner.setAdapter(spinnerArrayAdapter);
                    if (mResponse != null && mResponse.size() > i - 1) {
                        spinner.setSelection(optionText.indexOf(mResponse.get(i - 1).getAnswer()));
                    }
                    View dividerLineLine = activity.getLayoutInflater().inflate(R.layout.spinnerline, optionwidgetLL, false);
                    View dividerLineRadio = activity.getLayoutInflater().inflate(R.layout.divider_line, optionwidgetLL, false);
                    optionwidgetLL.addView(spinner);
                    optionwidgetLL.addView(dividerLineLine);
                    optionwidgetLL.addView(dividerLineRadio);
                    addView.add(spinner);

                    break;
                case "C":
                    layout.addView(assessmentQuestionText);
                    List<AnswersPage> mOptionsCheckbox = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    LinearLayout checkboxContainer = new LinearLayout(context);
                    checkboxContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            409,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    checkboxContainer.setOrientation(LinearLayout.VERTICAL);
                    checkboxContainer.setGravity(Gravity.CENTER_VERTICAL);
                    if (mOptionsCheckbox.size() > 0) {
                        for (int c = 0; c < mOptionsCheckbox.size(); c++) {
                            CheckBox checkbox = new CheckBox(context);
                            checkbox.setText(mOptionsCheckbox.get(c).getAnswer());
                            checkboxContainer.addView(checkbox);

                        }
                    }
                    optionwidgetLL.addView(checkboxContainer);
                    addView.add(checkboxContainer);

                    break;
                case "D":
                    layout.addView(assessmentQuestionText);
                    final Button button = new Button(context);
                    button.setTextColor(Color.WHITE);
                    button.setHintTextColor(Color.WHITE);
                    button.setHint("Pick Date");
                    button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    button.setId(1000 + 2 + i);
                    dateButton.add(button);
                    final String finalGetGroupVaidation = getGroupVaidation;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (Button btn : dateButton) {
                                if (btn.getId() == view.getId()) {
                                    buttonDynamicDateGrid.clear();
                                    buttonDynamicDateGrid.put("1", btn);
                                }

                            }
                            // SupportClass.this.activity.showDateDialog(2, finalGetGroupVaidation, button);
                        }
                    });
                    optionwidgetLL.addView(button);
                    addView.add(button);
                    break;

                default:
                    layout.addView(assessmentQuestionText);
                    LinearLayout.LayoutParams paramEdit1;
                    paramEdit1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                    EditText dateEdit = new EditText(context);
                    dateEdit.setText("");
                    dateEdit.setTextColor(Color.BLACK);
                    subCount = subCount + 20 + 2 + i;
                    dateEdit.setId(subCount);
                    dateEdit.setLayoutParams(paramEdit1);
                    paramEdit1.setMargins(4, 4, 4, 4);
                    dateEdit.setPadding(10, 0, 0, 10);
                    dateEdit.setBackgroundResource(R.drawable.textfieldbg);

                    if (mResponse != null && mResponse.size() > i - 1) {
                        dateEdit.setText(mResponse.get(i - 1).getAnswer());

                    }
                    if (!"".equals(mAssesmant.get(i - 1).getGroupValidation())) {
                        String mathString = mAssesmant.get(i - 1).getGroupValidation();
                        Logger.logD("mathString", "mathString" + mathString);
                        String[] array;
                        if (mathString != null && mathString.length() > 1) {
                            array = mathString.split(":");
                            if ("R".equalsIgnoreCase(array[0]) || "o".equalsIgnoreCase(array[0])) {
                                int maxLength;
                                InputFilter[] FilterArray;
                                switch (array[1]) {
                                    case "N":
                                        optionwidgetLL.addView(dateEdit);
                                        addView.add(dateEdit);
                                        dateEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        dateEdit.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(dateEdit);
                                        addView.add(dateEdit);
                                        dateEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        dateEdit.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(dateEdit);
                                        addView.add(dateEdit);
                                        dateEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        dateEdit.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                optionwidgetLL.addView(dateEdit);
                                addView.add(dateEdit);
                                dateEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                dateEdit.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(dateEdit);
                        addView.add(dateEdit);

                    }
                    break;
            }
        }
    }

    private static void ClearAllDialogViews(View dialogView, List<View> addView) {
        LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dynamic_mini_grid);
        layout.removeAllViews();
        addView.clear();


    }

    private  void RenderNextSetOfQuestion(List<Response> responselist, List<AssesmentBean> mAssesmantmain, Context context, SurveyQuestionActivity activity, Page currentQuestionPage, SQLiteDatabase database,
                                                View child, int gridType, Page questionPageBean, String isSkip, View dialogView, SharedPreferences preferences, List<View> addView) {
        final LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dynamic_mini_grid);
        final LinearLayout optionwidgetLL = layout;
        optionwidgetLL.removeAllViews();
        List<AssesmentBean> mainQList = mAssesmantmain;
        addView.clear();
        renderAllTypeOfWidget(mainQList.size(), context, mAssesmantmain, gridType, questionPageBean, activity,
                optionwidgetLL, responselist, addView, layout, database, preferences);
        Button nextBtn = (Button) dialogView.findViewById(R.id.saveandexit);
        Button saveAndCreate = (Button) dialogView.findViewById(R.id.saveandcreate);
        if (nextBtn.getText().equals("Next")){
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", mAssesmantmain);
                    List<String> answeredtemp = methodToValidationField(currentQuestionPage.getQuestionNumber(), context,
                            addView, database, gridType, currentQuestionPage);
                    SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    int survey_ID = prefs.getInt("survey_id", 0);
                    //    grid skip case 2
                    boolean getLastQuestionChangesStatus= getLastQuestionChangesStatus(answeredtemp);
                    UpdateResponseToSkipBased(answeredtemp, mAssesmantmain, currentQuestionPage.getQuestionNumber(), questionPageBean,

                            database, preferences, survey_ID, addView, gridType);
                    validatewithNextFunctionality(answeredtemp, mAssesmantmain, questionPageBean, currentQuestionPage.getQuestionNumber(), database,
                            dialogView, addView, context, activity, currentQuestionPage, child, preferences, survey_ID, gridType);

                }
            });
        }

        saveAndCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skipPageCount != 0) {
                    skipPageCount--;
                    List<AssesmentBean> getPreviousResponseList = assessmentListBasedOnSkip.get(String.valueOf(skipPageCount));
                    List<View> getPageView = skipPageView.get(String.valueOf(skipPageCount));
                    gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", getPreviousResponseList);
                    ClearAllDialogViews(dialogView, addView);
                    List<Response> getResponse = prePareResponseList(getPreviousResponseList, responselistBasedOnSkip);
                    RenderNextSetOfQuestion(getResponse, getPreviousResponseList, context, activity, currentQuestionPage, database, child, gridType,
                            questionPageBean, "2", dialogView, preferences, getPageView);
                } else {
                    ToastUtils.displayToast("No preview", activity);
                }
            }
        });
    }

    private static List<Response> prePareResponseList(List<AssesmentBean> getResponseList, Map<String, Response> responselistBasedOnSkip) {
        List<Response> getTempList = new ArrayList<>();
        for (int i = 0; i < getResponseList.size(); i++) {
            Set<String> getAllKeys = responselistBasedOnSkip.keySet();
            if (!getAllKeys.isEmpty()) {
                for (String keyIndex : getAllKeys) {
                    if (keyIndex.equalsIgnoreCase(String.valueOf(getResponseList.get(i).getQid()))) {
                        getTempList.add(responselistBasedOnSkip.get(keyIndex));
                    }
                }
            }
        }
        return getTempList;

    }

    public static void UpdateResponseToSkipBased(List<String> answeredtemp, List<AssesmentBean> mAssesmant, int currentQuestionNumber, Page questionPageBean,
                                                 SQLiteDatabase database, SharedPreferences preferences, int survey_ID, List<View> addView, int gridType) {
        if (answeredtemp != null && answeredtemp.size() == mAssesmant.size()) {
            Logger.logD(LOGGER_TAG, "the all field validation pass");
            List<Response> responselist = new ArrayList<>();
            List<AnswersPage> mOptions;
            for (int i = 0; i < mAssesmant.size(); i++) {
                if (questionPageBean.getAnswer().equalsIgnoreCase("N")) {
                    if (mAssesmant.get(i).getQtype().equalsIgnoreCase("R") || mAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                        mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answeredtemp.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                    } else if (mAssesmant.get(i).getQtype().equalsIgnoreCase("T")) {
                        mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                    } else {
                        mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 0));
                    }
                    if (mOptions.size() > 0) {
                        if (gridType==14) {
                            Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, questionPageBean.getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), mAssesmant.get(i).getQtype());
                            responselistBasedOnSkip.put(String.valueOf(mAssesmant.get(i).getQid()), response);
                            responselist.add(response);
                        } else{
                            Logger.logD("PrimaryKey-->",storeInlineindexPostion+"");
                            Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, storeInlineindexPostion, String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), mAssesmant.get(i).getQtype());
                            responselistBasedOnSkip.put(String.valueOf(mAssesmant.get(i).getQid()), response);
                            responselist.add(response);
                        }

                    }
                } else {
                    if (mAssesmant.get(i).getQtype().equalsIgnoreCase("R")) {
                        mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(mAssesmant.get(i).getQid(), database, answeredtemp.get(i), true);
                    } else {
                        mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                    }
                    if (mOptions.size() > 0) {
                        if (gridType==14) {
                            Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, questionPageBean.getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), mAssesmant.get(i).getQtype());
                            responselistBasedOnSkip.put(String.valueOf(mAssesmant.get(i).getQid()), response);
                            responselist.add(response);
                        }else{
                            Logger.logD("PrimaryKey-->",storeInlineindexPostion+"");
                            Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, storeInlineindexPostion, String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), mAssesmant.get(i).getQtype());
                            responselistBasedOnSkip.put(String.valueOf(mAssesmant.get(i).getQid()), response);
                            responselist.add(response);
                        }

                    }
                }


            }
            assessmentListBasedOnSkip.put(String.valueOf(skipPageCount), mAssesmant);
            deleteNextSetOfAssessmentFromHashmap(skipPageCount, assessmentListBasedOnSkip);
            //if (addOREditFlag.contentEquals("add") && deleteResponseFlag==0) {
            if (deleteResponseFlag==0) {
                deleteNextSetOfResponseFromHashmap(Integer.valueOf(responselist.get(responselist.size() - 1).getGroup_id()), responselistBasedOnSkip);
            }
            List<View> getTempList = new ArrayList<>();
            getTempList.addAll(addView);
            skipPageView.put(String.valueOf(skipPageCount), getTempList);
            skipPageCount++;
            Logger.logD("assessmentListBasedOnSkip", assessmentListBasedOnSkip.size() + "");
        }
    }

    private static void deleteNextSetOfResponseFromHashmap(int assessmentId, Map<String, Response> assessmentListBasedOnSkip) {
        Logger.logD("current Assessment Ids", assessmentId + "");
        Map<String, Response>  getTempList=new HashMap<>();
        getTempList.putAll(assessmentListBasedOnSkip);

        try {
            Set<String> getAllKeys = getTempList.keySet();
            if (!getAllKeys.isEmpty()) {
                for (String keyIndex : getAllKeys) {
                    if (Integer.parseInt(keyIndex) > assessmentId) {
                        assessmentListBasedOnSkip.remove(keyIndex);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE("assessmentListBasedOnSkip", "", e);
        }
    }

    private static void deleteNextSetOfAssessmentFromHashmap(int skipPageCount, Map<String, List<AssesmentBean>> assessmentListBasedOnSkip) {
        try {
            Map<String, List<AssesmentBean>> getTempList=new HashMap<>();
            getTempList.putAll(assessmentListBasedOnSkip);
            Set<String> getAllKeys = getTempList.keySet();
            if (!getAllKeys.isEmpty()) {
                for (String keyIndex : getAllKeys) {
                    if (Integer.parseInt(keyIndex) > skipPageCount) {
                        assessmentListBasedOnSkip.remove(keyIndex);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE("assessmentListBasedOnSkip", "", e);
        }

    }

    private static void ClearALlViews(List<View> layout, final ScrollView scrollView, List<AssesmentBean> mAssesmant) {
        for (int clearview = 0; clearview < mAssesmant.size(); clearview++) {
            switch (mAssesmant.get(clearview).getQtype()) {
                case "T":
                    EditText ed = (EditText) layout.get(clearview);
                    ed.setText("");
                    break;
                case "R":
                    RadioGroup rg = (RadioGroup) layout.get(clearview);
                    rg.clearCheck();
                    break;
                case "C":
                    LinearLayout checkBoxLinearLayout = (LinearLayout) layout.get(clearview);
                    for (int i = 0; i < checkBoxLinearLayout.getChildCount(); i++) {
                        CheckBox ch = (CheckBox) checkBoxLinearLayout.getChildAt(i);
                        ch.setChecked(false);
                    }
                    break;
                case "S":
                    break;
                case "D":
                    Button dateButton = (Button) layout.get(clearview);
                    dateButton.setText("");
                    break;
            }
            Logger.logD(LOGGER_TAG, "all the edittext view Clear");


        }
    }

    public static void setWhiteStar(TextView labelObj, String text) {
        Spannable word = new SpannableString(text);
        labelObj.setText(word);
        Spannable wordTwo = new SpannableString(" *");
        wordTwo.setSpan(new ForegroundColorSpan(Color.WHITE), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelObj.append(wordTwo);
    }

    public  void moduleToCreateInlineDialogForm(Page currentQuestionPage, SQLiteDatabase database, SurveyQuestionActivity surveyQuestionActivity,
                                                View child, SharedPreferences preferences, int surveysId) {
        skipPageCount = 0;
        Context context = surveyQuestionActivity;
        SurveyQuestionActivity activity = surveyQuestionActivity;
        final List<AssesmentBean> MAssesmant = DataBaseMapperClass.getAssesements(currentQuestionPage.getQuestionNumber(), database, preferences.getInt(Constants.SELECTEDLANGUAGE, 1));

        mainGridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", MAssesmant);
        for (int i = 0; i < MAssesmant.size(); i++) {
            AssesmentBean assessmentbean = mainGridAssessmentMapDialog.get(currentQuestionPage.getQuestionNumber() + "_ASS").get(i);
            mainAcessmentList.add(String.valueOf(assessmentbean.getQid()));
        }
        gridQuestionMapDialog.put(currentQuestionPage.getQuestionNumber() + "_QUESTION", currentQuestionPage);
        List<AssesmentBean> mAssesmantmain = modifiedAssessementListOnSKIP(MAssesmant, database);
        Logger.logD("mAssesmantmain", "mAssesmantmain sorted list " + mAssesmantmain);
        gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", mAssesmantmain);
       // inlist flow startz from here , passing 2 as skip code existing .
        createNotTaggedSkipIds(MAssesmant, database,surveysId);
        List<Response> getResponse = prePareResponseList(mAssesmantmain, responselistBasedOnSkip);
        showChangeLangDialog(getResponse, mAssesmantmain, context, activity, currentQuestionPage, database, child, 16, currentQuestionPage, "2");
    }

    private static List<AssesmentBean> modifiedAssessementListOnSKIP(List<AssesmentBean> mAssesmant, SQLiteDatabase database) {
        List<AssesmentBean> sortedAssessementBean = new ArrayList<>();
        for (int i = 0; i < mAssesmant.size(); i++) {
            String skipStatus = checkSkipCodeAndSplitList(mAssesmant.get(i), database);
            Logger.logD("skipStatus", "skipStatus--> " + skipStatus);
            if (!"".equals(skipStatus)) {
                sortedAssessementBean.add(mAssesmant.get(i));
                break;

            } else {
                Logger.logD("skipStatus", "skipcode is true skip code exist added to list" + mAssesmant.get(i).getQid());
                sortedAssessementBean.add(mAssesmant.get(i));
            }
        }
        return sortedAssessementBean;
    }

    private static String checkSkipCodeAndSplitList(AssesmentBean mAssesmant, SQLiteDatabase database) {

        String skipStatus = "";
        String questionQuery = "SELECT skip_code FROM Options where assessment_pid=" + mAssesmant.getQid();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(questionQuery, null);
            Logger.logD("blockquery", "SpinnerQuestionQuery" + questionQuery);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String skipcode = "";
                    if (mAssesmant.getQid() == 22) {
                        skipcode = "";
                    } else {
                        skipcode = cursor.getString(cursor.getColumnIndex("skip_code"));
                    }

                    if (!"".equals(skipcode)) {
                        skipStatus = skipcode;
                        break;
                    } else {
                        skipStatus = "";
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Logger.logE("Exception", "getting questions based on blocks", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return skipStatus;


    }


    private static class RemoveTask extends AsyncTask<Context, Integer, String> {
        net.sqlcipher.database.SQLiteDatabase database;
        DBHandler dbhandler;
        String surveyID;
        SurveyQuestionActivity ctx;
        private SharedPreferences preferences;

        private RemoveTask(net.sqlcipher.database.SQLiteDatabase db, DBHandler handler, String survey_id, SurveyQuestionActivity context) {
            database = db;
            dbhandler = handler;
            surveyID = survey_id;
            ctx = context;
            preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        }

        @Override
        protected String doInBackground(Context... arg0) {
            String data = "";
            String selectQuery = "SELECT * FROM Response where survey_id='" + surveyID + "'";
            database = dbhandler.getdatabaseinstance_read();
            try {
                Cursor cursor = database.rawQuery(selectQuery, null);
                if (cursor != null && cursor.moveToFirst()) {
                    data = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                    Log.d(LOGGER_TAG, data + "");
                    cursor.close();
                }
                deleteData(ctx);
                cursor.close();
            } catch (Exception e) {
                Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in SurveyQuestionsActivity  isIntNumber method ", e);
            }
            return null;
        }


        /**
         * @param ctx
         */
        private void deleteData(final SurveyQuestionActivity ctx) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    if (preferences.getBoolean("SaveDraftButtonFlag", false)) {
                        alertDialogBuilder.setMessage("Are you sure, you want to remove the details?");
                    } else {
                        alertDialogBuilder.setMessage(R.string.deleteSurvey);
                    }
                    alertDialogBuilder.setPositiveButton(R.string.proceed,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    actualData();
                                }
                            });
                    alertDialogBuilder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Logger.logV("", "deleteData");
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }

                public void actualData() {
                    try {
                        String removeData = "DELETE FROM Survey WHERE uuid = '" + surveyID + "'";
                        database.execSQL(removeData);
                        removeData = "DELETE FROM Response WHERE survey_id = '" + surveyID + "'";
                        database.execSQL(removeData);
                    } catch (Exception e) {
                        Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in SurveyQuestionsActivity  actualData method ", e);
                    }
                    Intent intent = new Intent(ctx, MyIntentService.class);
                    ctx.startService(intent);
                    ctx.finish();

                }
            });
        }
    }

    /**
     * @param labelObj
     * @param text
     */
    public static void setRedStar(TextView labelObj, String text) {
       
        labelObj.setText(Html.fromHtml(text));
        Spannable wordTwo = new SpannableString(" *");
        wordTwo.setSpan(new ForegroundColorSpan(Color.RED), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelObj.append(wordTwo);
    }


    /**
     * @param labelObj
     * @param text
     */
    public static void setOffline(TextView labelObj, String text) {
        Spannable word = new SpannableString(text);
        labelObj.setText(word);
        Spannable wordTwo = new SpannableString(" (offline)");
        wordTwo.setSpan(new ForegroundColorSpan(Color.RED), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelObj.append(wordTwo);
    }


    /**
     * @param saveData
     * @param activity
     */
    public void setSaveData(boolean saveData, Activity activity) {
        SharedPreferences myAppPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor edit = myAppPreference.edit();
        edit.putBoolean(Constants.SAVE_DATA, saveData);
        edit.apply();
    }

    private static List<String> methodToValidationField(int currentQuestionNumber, Context context,
                                                        List<View> addView, SQLiteDatabase database, int QType, Page page) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> answerList = new ArrayList<>();
        try {
            Logger.logD(LOGGER_TAG, "the dialog LinearLayout child count" + addView.size());
            List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
            for (int i = 0; i < MAssesmant.size(); i++) {
                Logger.logD(LOGGER_TAG, "addViewIds" + addView.get(i).getId());
                String priorityQtype = "";
                if (QType == 14 && page != null) {
                    if (page.getAnswer().equals("N")) {
                        priorityQtype = page.getAnswer();
                    } else {
                        priorityQtype = MAssesmant.get(i).getQtype();
                        Logger.logD("priorityQtype", "priorityQtype in Assessment" + priorityQtype);
                    }
                } else {
                    priorityQtype = MAssesmant.get(i).getQtype();
                }


                switch (priorityQtype) {
                    case "T":
                        EditText ed = (EditText) addView.get(i);

                        String validationExpression;
                        if (QType == 14 && page != null && !page.getAnswer().equals("N"))
                            validationExpression = DataBaseMapperClass.getValidationExpressionFromQuestion(page.getQuestionId(), database);
                        else
                            validationExpression = MAssesmant.get(i).getGroupValidation();
                        String[] arrayValidation = validationExpression.split(":");
                        if (!"".equals(ed.getText().toString()) || MAssesmant.get(i).getMandatory() != 1) {

                            Logger.logD("validationExpression", "validation expression from the database" + Arrays.toString(arrayValidation));
                            boolean checkValidation = false;
                            if (arrayValidation.length > 1)
                                checkValidation = ValidationUtils.performNextValidation(arrayValidation, ed.getText().toString().trim());
                            else
                                checkValidation = true;

                            if (checkValidation) {
                                Logger.logD(LOGGER_TAG, "the answer values in dialog box" + ed.getText().toString());
                                if (answerList != null) {
                                    answerList.add(ed.getText().toString());
                                }
                            } else {
                                ed.setError(arrayValidation[4]);
                                ed.setFocusable(true);
                                answerList = null;
                            }
                        } else {
                            ed.setError("field is Mandatory");
                            ed.setFocusable(true);
                            answerList = null;
                        }
                        break;
                    case "R":
                        RadioGroup radiogroup = (RadioGroup) addView.get(i);
                        for (int r = 0; r < radiogroup.getChildCount(); r++) {
                            RadioButton button = (RadioButton) radiogroup.getChildAt(r);
                            if (button.isChecked()) {
                                Logger.logD("ButtonId", "Get Button Id" + button.getId());
                                String checkedItemData = button.getText().toString();
                                if (answerList != null) {
                                    answerList.add(checkedItemData);
                                }
                                break;
                            }
                            if (radiogroup.getCheckedRadioButtonId() == -1) {
                                button.setError("field is Mandatory");
                                answerList = null;
                            }
                        }
                        break;
                    case "C":
                        CheckBox checkbox = null;
                        int checkBoxCount = 0;
                        List<String> OptionCode = new ArrayList<>();
                        List<AnswersPage> mOptions = DataBaseMapperClass.getOptionsAnswers(MAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 1));
                        LinearLayout ll = (LinearLayout) addView.get(i);
                        for (int c = 0; c < ll.getChildCount(); c++) {
                            checkbox = (CheckBox) ll.getChildAt(c);
                            if (checkbox.isChecked()) {
                                checkbox.setError(null);
                                checkBoxCount = checkBoxCount + 1;
                                OptionCode.add(String.valueOf(mOptions.get(c).getAnswerCode()));

                            }
                        }
                        if (answerList != null) {
                            answerList.add(OptionCode.toString());

                        }
                        if (checkBoxCount == 0) {
                            if (checkbox != null) {
                                checkbox.setError("Mandatory Question");
                            }
                            answerList = null;
                        }
                        break;
                    case "S":
                        Spinner typeInSpinner;
                        typeInSpinner = (Spinner) addView.get(i);
                        Logger.logD("spinner_selected_answer", typeInSpinner.getSelectedItem().toString());
                        if (answerList != null) {
                            answerList.add(typeInSpinner.getSelectedItem().toString());
                        }
                        break;
                    case "D":
                        Button edDatePicker = (Button) addView.get(i);
                        edDatePicker.setHintTextColor(Color.WHITE);
                        if (!"".equals(edDatePicker.getText().toString())) {
                            if (true) {
                                Logger.logD(LOGGER_TAG, "the answer values in dialog box" + edDatePicker.getText().toString());
                                if (answerList != null) {
                                    answerList.add(edDatePicker.getText().toString());
                                }
                            } else {
                                edDatePicker.setError("Invalid date formate");
                                edDatePicker.setFocusable(true);
                                answerList = null;
                            }
                        } else {
                            edDatePicker.setError("field is Mandatory");
                            edDatePicker.setFocusable(true);
                            answerList = null;
                        }

                        break;

                    default:
                        EditText edDate = (EditText) addView.get(i);

                        String validationExpression1;
                        if (QType == 14 && page != null && !page.getAnswer().equals("N"))
                            validationExpression1 = DataBaseMapperClass.getValidationExpression(currentQuestionNumber, database);

                        else
                            validationExpression1 = MAssesmant.get(i).getGroupValidation();

                        if (!"".equals(edDate.getText().toString())) {
                            String[] arrayValidation1 = validationExpression1.split(":");
                            boolean checkValidation = false;
                            if (arrayValidation1.length > 1)
                                checkValidation = ValidationUtils.performNextValidation(arrayValidation1, edDate.getText().toString().trim());
                            else
                                checkValidation = true;

                            if (checkValidation) {
                                Logger.logD(LOGGER_TAG, "the answer values in dialog box" + edDate.getText().toString());
                                if (answerList != null) {
                                    answerList.add(edDate.getText().toString());
                                }
                            } else {
                                edDate.setError(arrayValidation1[4]);
                                edDate.setFocusable(true);
                                answerList = null;
                            }
                        } else {
                            edDate.setError("field is Mandatory");
                            edDate.setFocusable(true);
                            answerList = null;
                        }
                        break;
                }

            }
        } catch (Exception e) {
            Logger.logE("Exception", "in", e);
        }
        return answerList;
    }

    public static void showDialogEdit(final List<Response> mResponse, final List<AssesmentBean> mAssesmant,
                                      final Context context, final SurveyQuestionActivity activity, Page currentQuestionPage, final SQLiteDatabase database, final String hashMapKey,
                                      final View child, final int gridType, final Page subQuestionpage) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface;
        surveyQuestionGridInlineInterface = activity;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.form_dialog, null);
        final LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dynamic_mini_grid);
        final LinearLayout optionwidgetLL = layout;
        optionwidgetLL.removeAllViews();
        final List<View> addView = new ArrayList<>();
        int assessemntSize = mAssesmant.size();
        Logger.logV("the size of the n", "size" + assessemntSize);
        for (int i = 1; i < assessemntSize + 1; i++) {
            int subCount = i;
            LinearLayout.LayoutParams paramassessmentQuestionText;
            paramassessmentQuestionText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView assessmentQuestionText = new TextView(context);

            if (mAssesmant.get(i - 1).getMandatory() == 1) {
                setRedStar(assessmentQuestionText, mAssesmant.get(i - 1).getAssessment());
            } else {
                assessmentQuestionText.setText(mAssesmant.get(i - 1).getAssessment());
            }

            assessmentQuestionText.setLayoutParams(paramassessmentQuestionText);
            paramassessmentQuestionText.setMargins(4, 10, 4, 10);

            String priorityQtype = "";
            String getGroupVaidation = "";
            if (gridType == 14 && subQuestionpage != null) {
                if (!subQuestionpage.getAnswer().equals("N")) {
                    priorityQtype = subQuestionpage.getAnswer();
                    getGroupVaidation = subQuestionpage.getValidation();

                    Logger.logD("priorityQtype", "priorityQtype in SubQuestion" + priorityQtype);
                    Logger.logD("priorityQtype", "getGroupVaidation in SubQuestion" + getGroupVaidation);
                } else {
                    priorityQtype = mAssesmant.get(i - 1).getQtype();
                    Logger.logD("priorityQtype", "priorityQtype in Assessment" + priorityQtype);
                    getGroupVaidation = mAssesmant.get(i - 1).getGroupValidation();
                }
            } else {
                priorityQtype = mAssesmant.get(i - 1).getQtype();
                getGroupVaidation = mAssesmant.get(i - 1).getGroupValidation();
            }
            switch (priorityQtype) {
                case "T":
                    LinearLayout.LayoutParams paramEdit;
                    paramEdit = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                    View editTextView = activity.getLayoutInflater().inflate(R.layout.edittext, optionwidgetLL, false);//child.xml
                    TextView question = editTextView.findViewById(R.id.mainQuestion);
                    question.setVisibility(View.GONE);
                    TextInputLayout v = (TextInputLayout) editTextView.findViewById(R.id.textInput);
                    v.setHint(mAssesmant.get(i - 1).getAssessment() + " *");
                    v.setHintTextAppearance(R.style.hintstyle);
                    EditText editView = (EditText) editTextView.findViewById(R.id.ans_text);
                    editView.setSingleLine(true);
                    editView.setHintTextColor(activity.getResources().getColor(R.color.black));
                    question.setFocusable(true);

                    subCount = subCount + 20 + 2 + i;
                    editView.setId(subCount);
                    if (mResponse != null) {
                        for (int k = 0; k < mResponse.size(); k++) {
                            if (mResponse.get(k).getGroup_id() == mAssesmant.get(i - 1).getQid()) {
                                editView.setText(mResponse.get(k).getAnswer());
                            }
                        }


                    }
                    if (!"".equals(mAssesmant.get(i - 1).getGroupValidation())) {
                        String mathString = mAssesmant.get(i - 1).getGroupValidation();
                        Logger.logD("mathString", "mathString" + mathString);
                        String[] array;
                        if (mathString != null && mathString.length() > 1) {
                            array = mathString.split(":");
                            if ("R".equalsIgnoreCase(array[0]) || "o".equalsIgnoreCase(array[0])) {
                                int maxLength;
                                InputFilter[] FilterArray;
                                switch (array[1]) {
                                    case "N":

                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(editTextView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                optionwidgetLL.addView(editTextView);
                                addView.add(editView);
                                editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editView.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(editTextView);
                        addView.add(editView);

                    }
                    break;
                case "R":
                    layout.addView(assessmentQuestionText);
                    int s = 1;
                    List<AnswersPage> mOptions;
                    if (!mAssesmant.get(i - 1).getQtype().equalsIgnoreCase("N"))
                        mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    else
                        mOptions = DataBaseMapperClass.getOptionsAnswersForSubquestionBased(subQuestionpage.getQuestionId(), database);

                    RadioGroup radioGroup = new RadioGroup(context);
                    radioGroup.setLayoutParams(new RadioGroup.LayoutParams(
                            390,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    radioGroup.setOrientation(LinearLayout.VERTICAL);
                    radioGroup.setGravity(Gravity.LEFT);
                    radioGroup.setId(i + 1000 + i + i + s);
                    Logger.logD("GroupId", radioGroup.getId() + "");
                    s++;
                    int buttons = mOptions.size();
                    for (int r = 1; r <= buttons; r++) {
                        radioGroup.setId(i + 10000 + r);
                        RadioButton rbn = new RadioButton(context);
                        rbn.setId(i + 1000 + i + r);
                        rbn.setText(mOptions.get(r - 1).getAnswer());

                        if (mResponse != null) {
                            for (int k = 0; k < mResponse.size(); k++) {
                                if (mResponse.get(k).getAns_code().equals(mOptions.get(r - 1).getAnswerCode())) {
                                    rbn.setChecked(true);
                                }
                            }

                        }

                        radioGroup.addView(rbn);
                    }
                    optionwidgetLL.addView(radioGroup);
                    addView.add(radioGroup);
                    break;
                case "S":
                    layout.addView(assessmentQuestionText);
                    List<AnswersPage> mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                    List<String> optionText = new ArrayList<>();
                    Spinner spinner = new Spinner(context);
                    spinner.setLayoutParams(new RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    spinner.getBackground().setColorFilter(activity.getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_ATOP);

                    for (int d = 0; d < mOptionsDroupdown.size(); d++) {
                        optionText.add(mOptionsDroupdown.get(d).getAnswer());
                    }
                    ArrayAdapter<AnswersPage> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_multi_row_textview, mOptionsDroupdown);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);
                    if (mResponse != null) {
                        for (int k = 0; k < mResponse.size(); k++) {
                            for (int l = 0; l < mOptionsDroupdown.size(); l++) {
                                if (mResponse.get(k).getAns_code().equals(mOptionsDroupdown.get(l).getAnswerCode())) {
                                    spinner.setSelection(optionText.indexOf(mOptionsDroupdown.get(l).getAnswer()));
                                    Logger.logD(LOGGER_TAG, " previous Answer" + mOptionsDroupdown.get(l).getAnswer());
                                }
                            }

                        }

                    }
                    View dividerLineLine = activity.getLayoutInflater().inflate(R.layout.spinnerline, optionwidgetLL, false);//child.xml
                    View dividerLineRadio = activity.getLayoutInflater().inflate(R.layout.divider_line, optionwidgetLL, false);//child.xml
                    optionwidgetLL.addView(spinner);
                    optionwidgetLL.addView(dividerLineLine);
                    optionwidgetLL.addView(dividerLineRadio);
                    addView.add(spinner);
                    break;
                case "C":
                    layout.addView(assessmentQuestionText);
                    List<AnswersPage> mOptionsCheckbox = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    LinearLayout checkboxContainer = new LinearLayout(context);
                    checkboxContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            409,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    checkboxContainer.setOrientation(LinearLayout.VERTICAL);
                    checkboxContainer.setGravity(Gravity.CENTER_VERTICAL);
                    if (mOptionsCheckbox.size() > 0) {
                        for (int c = 0; c < mOptionsCheckbox.size(); c++) {
                            CheckBox checkbox = new CheckBox(context);
                            String getansweredOptionCode = "";
                            for (int u = 0; u < mAssesmant.size(); u++) {
                                boolean istrue = false;
                                for (int y = 0; y < mResponse.size(); y++) {

                                    if (mAssesmant.get(u).getQid() == mResponse.get(y).getGroup_id() && mResponse.get(y).getQ_type().equalsIgnoreCase("C")) {
                                        getansweredOptionCode = mResponse.get(y).getAnswer();
                                        istrue = true;
                                        break;
                                    }
                                    if (istrue)
                                        break;

                                }
                                if (istrue)
                                    break;
                            }
                            String string = getansweredOptionCode.replace("[", "").replace("]", "").replace("\"", "");
                            String[] array = string.split(",");
                            for (int k = 0; k < array.length; k++) {
                                if (mOptionsCheckbox.get(c).getAnswerCode().equals(array[k].trim())) {
                                    checkbox.setChecked(true);
                                }
                            }
                            checkbox.setText(mOptionsCheckbox.get(c).getAnswer());
                            checkboxContainer.addView(checkbox);

                        }
                    }
                    optionwidgetLL.addView(checkboxContainer);
                    addView.add(checkboxContainer);

                    break;
                case "D":
                    layout.addView(assessmentQuestionText);
                    final Button button = new Button(context);
                    button.setTextColor(Color.WHITE);
                    button.setHint("Pick Date");
                    button.setHintTextColor(Color.WHITE);
                    button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    button.setId(1000 + 2 + i);
                    dateButton.add(button);
                    final String finalGetGroupVaidationEdit = getGroupVaidation;

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (Button btn : dateButton) {
                                if (btn.getId() == view.getId()) {
                                    buttonDynamicDateGrid.clear();
                                    buttonDynamicDateGrid.put("1", btn);
                                }

                            }
                            activity.showDateDialog(2, finalGetGroupVaidationEdit, button);
                        }
                    });
                    if (mResponse != null) {

                        for (int k = 0; k < mResponse.size(); k++) {
                            for (int l = 0; l < mAssesmant.size(); l++) {
                                if (mResponse.get(k).getGroup_id() == (mAssesmant.get(l).getQid()) && mResponse.get(k).getQ_type().equalsIgnoreCase("D")) {
                                    button.setText(mResponse.get(k).getAnswer());
                                    Logger.logD(LOGGER_TAG, " date Answer" + mResponse.get(k).getAnswer());

                                }
                            }

                        }


                    }
                    optionwidgetLL.addView(button);
                    addView.add(button);
                    break;
                default:
                    layout.addView(assessmentQuestionText);
                    LinearLayout.LayoutParams paramEdit1;
                    paramEdit1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                    EditText editViewdate = new EditText(context);
                    editViewdate.setText("");
                    editViewdate.setTextColor(Color.BLACK);
                    subCount = subCount + 20 + 2 + i;
                    editViewdate.setId(subCount);
                    editViewdate.setLayoutParams(paramEdit1);
                    paramEdit1.setMargins(4, 4, 4, 4);
                    editViewdate.setPadding(10, 0, 0, 10);
                    editViewdate.setBackgroundResource(R.drawable.textfieldbg);
                    if (mResponse != null) {
                        editViewdate.setText(mResponse.get(i - 1).getAnswer());

                    }
                    if (!"".equals(mAssesmant.get(i - 1).getGroupValidation())) {
                        String mathString = mAssesmant.get(i - 1).getGroupValidation();
                        Logger.logD("mathString", "mathString" + mathString);
                        String[] array;
                        if (mathString != null && mathString.length() > 1) {
                            array = mathString.split(":");
                            if ("R".equalsIgnoreCase(array[0]) || "o".equalsIgnoreCase(array[0])) {
                                int maxLength;
                                InputFilter[] FilterArray;
                                switch (array[1]) {
                                    case "N":

                                        optionwidgetLL.addView(editViewdate);
                                        addView.add(editViewdate);
                                        editViewdate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editViewdate.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(editViewdate);
                                        addView.add(editViewdate);
                                        editViewdate.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editViewdate.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(editViewdate);
                                        addView.add(editViewdate);
                                        editViewdate.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editViewdate.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                optionwidgetLL.addView(editViewdate);
                                addView.add(editViewdate);
                                editViewdate.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editViewdate.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(editViewdate);
                        addView.add(editViewdate);

                    }
                    break;
            }
        }
        dialogBuilder.setView(dialogView);
        final TextView txt = (TextView) dialogView.findViewById(R.id.assessmentquestion);
        final Button saveAndCreate = (Button) dialogView.findViewById(R.id.saveandcreate);
        saveAndCreate.setVisibility(View.GONE);
        final Button saveAndExit = (Button) dialogView.findViewById(R.id.saveandexit);

        if (gridType == 14) {
            saveAndCreate.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            txt.setText(subQuestionpage.getSubQuestion());
        } else {
            txt.setVisibility(View.GONE);
        }
        dialogBuilder.setTitle(currentQuestionPage.getQuestion());

        final int currentQuestionNumber = currentQuestionPage.getQuestionNumber();
        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();
        saveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * module to validate the all the Field based on the layout filled . returing back the answerd list
                 */
                List<String> answered = methodToValidationField(currentQuestionNumber, context, addView, database, gridType, subQuestionpage);
                if (answered != null && answered.size() == mAssesmant.size()) {
                    Logger.logD(LOGGER_TAG, "the all field validation pass");
                    SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    int survey_ID = prefs.getInt("survey_id", 0);
                    List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
                    List<Response> responselist = new ArrayList<>();
                    List<AnswersPage> mOptions;
                    for (int i = 0; i < MAssesmant.size(); i++) {

                        if (gridType == 14) {
                            if (subQuestionpage.getAnswer().equalsIgnoreCase("N")) {
                                if (MAssesmant.get(i).getQtype().equalsIgnoreCase("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                    mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                                } else {
                                    mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 1));
                                }
                                String[] splitHashmapkey = hashMapKey.split("_");
                                if (mOptions.size() > 0) {
                                    Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, Integer.parseInt(splitHashmapkey[1]), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), MAssesmant.get(i).getQtype());
                                    responselist.add(response);
                                    Logger.logD("GridValues in Response", responselist.get(i).toString());
                                } else {
                                    ToastUtils.displayToast("Option code not found", context);
                                }
                            } else {
                                if (subQuestionpage.getAnswer().equalsIgnoreCase("R") || subQuestionpage.getAnswer().equalsIgnoreCase("S")) {
                                    mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(subQuestionpage.getQuestionId(), database, answered.get(i), true);
                                } else {
                                    mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(subQuestionpage.getQuestionId(), database, "", false);
                                }
                                String[] splitHashmapkey = hashMapKey.split("_");
                                if (mOptions.size() > 0) {
                                    Response response = new Response(String.valueOf(currentQuestionNumber),
                                            answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, Integer.parseInt(splitHashmapkey[1]), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(),
                                            subQuestionpage.getAnswer());
                                    responselist.add(response);
                                    Logger.logD("GridValues in Response", responselist.get(i).toString());
                                } else {
                                    ToastUtils.displayToast("Option code not found", context);
                                }
                            }
                        } else {
                            if (MAssesmant.get(i).getQtype().equalsIgnoreCase("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt(Constants.SELECTEDLANGUAGE, 1));
                            } else {
                                mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 1));
                            }
                            String[] splitHashmapkey = hashMapKey.split("_");
                            if (mOptions.size() > 0) {
                                Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, Integer.parseInt(splitHashmapkey[1]), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), MAssesmant.get(i).getQtype());
                                responselist.add(response);
                                Logger.logD("GridValues in Response", responselist.get(i).toString());
                            } else {
                                ToastUtils.displayToast("Option code not found", context);
                            }
                        }

                    }
                    String[] splitHashmapkey = hashMapKey.split("_");
                    if (gridType == 16) {
                        fillInlineRow.put(currentQuestionNumber + "_" + splitHashmapkey[1], responselist);
                        Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                        b.dismiss();
                        ToastUtils.displayToast("Response updated", context);
                        surveyQuestionGridInlineInterface.OnSuccessfullGridInline(fillInlineRow, child, currentQuestionNumber, fillInlineHashMapKey, 16);

                    } else if (gridType == 14) {
                        GridResponseHashMap.put(currentQuestionNumber + "_" + splitHashmapkey[1], responselist);
                        Logger.logD("Size if the filled Complet in Response in hashMap", fillInlineRow.size() + "");
                        b.dismiss();
                        ToastUtils.displayToast("Response updated", context);
                        surveyQuestionGridInlineInterface.OnSuccessfullGridInline(GridResponseHashMap, child, currentQuestionNumber, GridResponseHashMapKeys, 14);

                    }

                } else {
                    ToastUtils.displayToast("please fill Mandatory field", context);
                }

            }
        });

    }

    public void filterSupport(Bundle bundle, ListingActivity listingActivity, ExternalDbOpenHelper dbhelper) {
        storeAllDynamicSpinner.clear();
        int surveyId = bundle.getInt(Constants.SURVEY_ID);
        String levels;
        String labels;
        String[] orderLabels;
        String[] orderLeves;
        activity = listingActivity;
        SharedPreferences filterPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences sharedpreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filterdialog);
        LinearLayout dymaicactivitydisplay = (LinearLayout) dialog.findViewById(R.id.dymaicactivitydisplay);
        TextView headinglabel = (TextView) dialog.findViewById(R.id.headinglabel);
        levels = getAccessBasedLevels(sharedpreferences, surveyId, dbhelper);
        labels = getAccessBasedLables(sharedpreferences, surveyId, dbhelper);

        orderLabels = labels.split(",");
        orderLeves = levels.split(",");
        if (orderLabels.length > 0 && orderLeves.length > 0) {

            createDynamicSpinnerAndLabel(orderLabels, orderLeves, dymaicactivitydisplay, dbhelper, surveyId);
        } else {
            Toast.makeText(activity, "Sorry! Levels are empty", Toast.LENGTH_SHORT).show();

        }
        createDynamicSpinnerAndLabel(orderLabels, orderLeves, dymaicactivitydisplay, dbhelper, surveyId);
        filterButtonClickFunctionality(dialog, surveyId);
        String getFilterRecords = filterPreference.getString(Constants.FILTER + "@" + surveyId, "");
        handleDialogBackPress(dialog, getFilterRecords);
        dialog.show();


    }

    private String getAccessBasedLables(SharedPreferences sharedpreferences, int surveyId, ExternalDbOpenHelper dbhelper) {
        String getAccessBasedLocation = "";
        if (sharedpreferences.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("1")) {
            getAccessBasedLocation = sharedpreferences.getString(Constants.O_LABLES, "");
        } else if (sharedpreferences.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("0")) {
            getAccessBasedLocation = dbhelper.getOrderlabels(surveyId);
        }
        return getAccessBasedLocation;
    }

    private String getAccessBasedLevels(SharedPreferences filterPreference, int surveyId, ExternalDbOpenHelper dbhelper) {
        String getAccessBasedLocation = "";
        if (filterPreference.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("1")) {
            getAccessBasedLocation = filterPreference.getString(Constants.O_LEAVEL, "");
        } else if (filterPreference.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("0")) {
            getAccessBasedLocation = dbhelper.getOrderLevels(surveyId);
        }
        return getAccessBasedLocation;
    }

    private void handleDialogBackPress(Dialog dialog, String getFilterRecords) {
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK && getFilterRecords.isEmpty()) {
                    activity.finish();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
                return true;
            }
        });

    }

    private void filterButtonClickFunctionality(Dialog dialog, int surveyId) {
        Button filterbutton = (Button) dialog.findViewById(R.id.filterbutton);
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner getLastSpinner = storeAllDynamicSpinner.get(storeAllDynamicSpinner.size() - 1);
                LevelBeen levelBeen = (LevelBeen) getLastSpinner.getSelectedItem();
                if (levelBeen != null && !levelBeen.getName().equalsIgnoreCase("Select")) {
                    buildFilterRecordSharedPrefarence(surveyId, dialog);
                } else {

                    ToastUtils.displayToast("Please select upto least level", activity);
                }
            }
        });


    }

    private void buildFilterRecordSharedPrefarence(int surveyId, Dialog dialog) {
        SharedPreferences filterPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        StringBuilder selectedLevels = new StringBuilder();
        for (int i = 0; i < storeAllDynamicSpinner.size(); i++) {
            Spinner getLastSpinner = storeAllDynamicSpinner.get(i);
            LevelBeen levelBeen = (LevelBeen) getLastSpinner.getSelectedItem();
            if (i == 0)
                selectedLevels.append(levelBeen.getName());
            else
                selectedLevels.append(",").append(levelBeen.getName());
        }
        filterPreference.edit().putString(Constants.FILTER + "@" + surveyId, selectedLevels.toString()).apply();
        FilterCallBackInterface filterCallBackInterface = (FilterCallBackInterface) activity;
        filterCallBackInterface.filterCallBack();
        dialog.cancel();
    }


    /**
     * Dynamic setting lable and setting the value to the spinner and also adding lisiner dynamically.
     *
     * @param orderLabels           dynamic order labels.
     * @param orderLeves            dynamic order leave
     * @param dymaicactivitydisplay parent linearLayout
     * @param dbhelper              database instance
     * @param surveyId              survey id
     */
    private void createDynamicSpinnerAndLabel(String[] orderLabels, String[] orderLeves,
                                              LinearLayout dymaicactivitydisplay, ExternalDbOpenHelper dbhelper,
                                              int surveyId) {
        dymaicactivitydisplay.removeAllViews();
        storeAllDynamicSpinner.clear();
        try {
            for (int i = 0; i < orderLeves.length; i++) {
                View child = activity.getLayoutInflater().inflate(R.layout.dropdown, dymaicactivitydisplay, false);//child.xml
                TextView mainQuestionspinner = child.findViewById(R.id.mainQuestionspinner);
                Spinner dynamicSpinner = child.findViewById(R.id.spinner);
                dynamicSpinner.setTag(i);
                dynamicSpinner.setId(i);
                mainQuestionspinner.setText(orderLabels[i]);
                setValuesToSpinner(orderLeves[i], dynamicSpinner, dbhelper, surveyId);
                storeAllDynamicSpinner.add(dynamicSpinner);
                dymaicactivitydisplay.addView(child);
                setOnclickListnerDynamic(dynamicSpinner, orderLeves[i], dbhelper, surveyId);

            }

        } catch (Exception e) {
            Logger.logE(TAG, "createDynamicSpinnerAndLabel", e);
        }
    }


    /**
     * setting default value to the Dynamic spinner and if already exist get from the SP and set @ first spinner.
     *
     * @param orderLeve      dynamic order level
     * @param dynamicSpinner dynamic spinner .
     * @param dbhelper       encreapted database.
     * @param surveyId       survey id.
     */
    private void setValuesToSpinner(String orderLeve, Spinner dynamicSpinner, ExternalDbOpenHelper dbhelper, int surveyId) {
        SharedPreferences filterPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences sharedpreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        List<LevelBeen> getLevelsrecords = dbhelper.getLevelsrecords(orderLeve, dbhelper, sharedpreferences);
        String getFilterRecords = filterPreference.getString(Constants.FILTER + "@" + surveyId, "");
        if (!getFilterRecords.isEmpty())
            getLevelsrecords.remove(0);
        if (!getLevelsrecords.isEmpty()) {
            ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(activity, R.layout.spinner_multi_row_textview, getLevelsrecords);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
            dynamicSpinner.setAdapter(spinnerArrayAdapter);
        }
    }

    /**
     * Dynamic onClickLisiner and setting the value from the database.
     *
     * @param dynamicSpinner dynamic spinner .
     * @param orderLeve      dynamic location level
     * @param dbhelper       database instance
     * @param surveyId       survey id
     */
    private void setOnclickListnerDynamic(Spinner dynamicSpinner, String orderLeve, ExternalDbOpenHelper dbhelper,
                                          int surveyId) {
        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Logger.logD(TAG, "clicked Ites" + adapterView.getId());
                updateValuesDynamic(adapterView.getId(), orderLeve, dbhelper, surveyId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * updating Value to spinner dynamically
     *
     * @param selectedLevel loop selected level
     * @param orderLeve     order leave
     * @param dbhelper      database instance
     * @param surveyId      survey id
     */
    private void updateValuesDynamic(int selectedLevel, String orderLeve, ExternalDbOpenHelper dbhelper,
                                     int surveyId) {

        SharedPreferences filterPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences sharedpreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        try {
            String getStringLevel = orderLeve.substring(5, 6);
            int getLevel = Integer.valueOf(getStringLevel);
            for (int i = selectedLevel; i < storeAllDynamicSpinner.size() - 1; i++) {

                Logger.logD(TAG, "clicked spinner" + selectedLevel);
                Spinner getStoredSpinner = storeAllDynamicSpinner.get(selectedLevel);
                Spinner nextSpinner = storeAllDynamicSpinner.get(i + 1);
                Logger.logD(TAG, "clicked next spinner" + selectedLevel);
                LevelBeen levelBeen = (LevelBeen) getStoredSpinner.getSelectedItem();
                getLevel++;
                List<LevelBeen> stateList = dbhelper.setSpinnerD("level" + (getLevel - 1) + "_id", String.valueOf("level" + getLevel), levelBeen.getId(),
                        sharedpreferences);

                if (!stateList.isEmpty()) {
                    ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(activity, R.layout.spinner_multi_row_textview, stateList);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                    nextSpinner.setAdapter(spinnerArrayAdapter);
                    String getFilterRecords = filterPreference.getString(Constants.FILTER + "@" + surveyId, "");
                    if (!getFilterRecords.isEmpty()) {
                        spinnerPreSelectFunctionaliy(nextSpinner, stateList,
                                getFilterRecords);
                    }
                } else {
                    List<LevelBeen> emptyStateList = new ArrayList<>();
                    LevelBeen level1 = new LevelBeen(0, "Select");
                    emptyStateList.add(level1);
                    ArrayAdapter<LevelBeen> spinnerArrayAdapter = new ArrayAdapter(activity, R.layout.spinner_multi_row_textview, emptyStateList);
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                    nextSpinner.setAdapter(spinnerArrayAdapter);


                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "updateValuesDynamic", e);
        }
    }

    /**
     * Pre-populating the spinner value back
     *
     * @param nextSpinner      next spinner in the loop
     * @param stateList        spinner value list .
     * @param getFilterRecords getting value from the SP if already selected .
     */
    private void spinnerPreSelectFunctionaliy(Spinner nextSpinner,
                                              List<LevelBeen> stateList, String getFilterRecords) {

        String[] getSelectedLocation = getFilterRecords.split(",");
        for (int i = 0; i < stateList.size(); i++) {
            for (String aGetSelectedLocation : getSelectedLocation) {
                if (aGetSelectedLocation.equalsIgnoreCase(stateList.get(i).getName())) {
                    nextSpinner.setSelection(i);
                }
            }
        }

    }


}
