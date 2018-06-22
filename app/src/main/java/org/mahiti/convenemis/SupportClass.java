package org.mahiti.convenemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.mahiti.convenemis.BeenClass.AnswersPage;
import org.mahiti.convenemis.BeenClass.AssesmentBean;
import org.mahiti.convenemis.BeenClass.Page;
import org.mahiti.convenemis.BeenClass.Response;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.DataBaseMapperClass;
import org.mahiti.convenemis.network.SurveyGridInlineInterface.surveyQuestionGridInlineInterface;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.mahiti.convenemis.SurveyQuestionActivity.MY_PREFS_NAME;
import static org.mahiti.convenemis.utils.Constants.GridResponseHashMap;
import static org.mahiti.convenemis.utils.Constants.GridResponseHashMapKeys;
import static org.mahiti.convenemis.utils.Constants.buttonDynamicDateGrid;
import static org.mahiti.convenemis.utils.Constants.dateButton;
import static org.mahiti.convenemis.utils.Constants.fillInlineHashMapKey;
import static org.mahiti.convenemis.utils.Constants.fillInlineRow;
import static org.mahiti.convenemis.utils.Constants.gridAssessmentMapDialog;
import static org.mahiti.convenemis.utils.Constants.gridQuestionMapDialog;
import static org.mahiti.convenemis.utils.Constants.listHashMapKey;
import static org.mahiti.convenemis.utils.Constants.mainAcessmentList;
import static org.mahiti.convenemis.utils.Constants.mainGridAssessmentMapDialog;
import static org.mahiti.convenemis.utils.Constants.rowInflater;


public class SupportClass {

    private static final String LOGGER_TAG = "SupportClass";
    private static List<String> GridlistHashMapKey = new ArrayList<>();
    Activity activity;
    Context context;

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

    public static void createDialogFOrGrid(Page page, View child, SurveyQuestionActivity surveyQuestionActivity, SQLiteDatabase database, int getCurrentGridQuestionID) {
        List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(String.valueOf(getCurrentGridQuestionID) + "_ASS");
        Page QuestionPageBean = gridQuestionMapDialog.get(String.valueOf(getCurrentGridQuestionID) + "_QUESTION");
        SupportClass.showChangeLangDialog(null, MAssesmant, surveyQuestionActivity, surveyQuestionActivity, QuestionPageBean, database, child, 14, page, "");

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
    public static void showChangeLangDialog(final List<Response> mResponse, final List<AssesmentBean> mAssesmant, final Context context, final SurveyQuestionActivity activity, final Page currentQuestionPage, final SQLiteDatabase database, final View child, final int gridType, final Page questionPageBean, final String skipCode) {
        final surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        surveyQuestionGridInlineInterface = activity;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.form_dialog, null);
        final LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.dynamic_mini_grid);
        final LinearLayout optionwidgetLL = layout;
        optionwidgetLL.removeAllViews();
        final List<View> addView = new ArrayList<>();
        final ScrollView scrollView = (ScrollView) dialogView.findViewById(R.id.dialogScroll);


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
            paramassessmentQuestionText.setMargins(10, 10, 10, 10);
            layout.addView(assessmentQuestionText);
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
                    LinearLayout.LayoutParams paramEdit;
                    paramEdit = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
                    EditText editView = new EditText(context);
                    editView.setText("");
                    editView.setTextColor(Color.BLACK);
                    subCount = subCount + 20 + 2 + i;
                    editView.setId(subCount);
                    editView.setLayoutParams(paramEdit);
                    paramEdit.setMargins(4, 4, 4, 4);
                    editView.setPadding(10, 0, 0, 10);
                    editView.setBackgroundResource(R.drawable.textfieldbg);

                    if (mResponse != null && mResponse.size() > i - 1) {
                        editView.setText(mResponse.get(i - 1).getAnswer());
                        editView.setEnabled(false);

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
                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "D":
                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        //maxLengthMobile = arrayValidation[3].length();
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                //  layout.addView(editView);
                                optionwidgetLL.addView(editView);
                                addView.add(editView);
                                editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editView.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(editView);
                        addView.add(editView);

                    }
                    break;
                case "R":
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
                    //  List<Response> responseAnswer= DataBaseMapperClass.setAnswersForGridRadio(mAssesmant.get(i-1).getQid(),db,String.valueOf(surveyPrimaryKeyId));
                    for (int r = 1; r <= buttons; r++) {
                        radioGroup.setId(i + 10000 + r);
                        RadioButton rbn = new RadioButton(context);
                        rbn.setId(i + 1000 + i + r);
                        rbn.setText(mOptions.get(r - 1).getAnswer());

                        if (mResponse != null && mResponse.size() > i - 1) {
                            if (mResponse.get(i - 1).getAns_code().equals(mOptions.get(r - 1).getAnswerCode())) {
                                rbn.setChecked(true);
                                radioGroup.setEnabled(false);

                            }
                        }

                        radioGroup.addView(rbn);
                    }
                    optionwidgetLL.addView(radioGroup);
                    addView.add(radioGroup);
                    break;
                case "S":

                    List<AnswersPage> mOptionsDroupdown;
                    if (!mAssesmant.get(i - 1).getQtype().equalsIgnoreCase("N")) {
                        mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    } else {
                        mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(questionPageBean.getQuestionId(), database, preferences.getInt("selectedLangauge", 1));
                    }


                    List<String> optionText = new ArrayList<>();
                    Spinner spinner = new Spinner(context);

                    spinner.setLayoutParams(new RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    spinner.setBackgroundResource(R.drawable.textfieldbg);

                    for (int d = 0; d < mOptionsDroupdown.size(); d++) {
                        optionText.add(mOptionsDroupdown.get(d).getAnswer());
                    }
                    //    List<Response> responseAnswerSpinner= DataBaseMapperClass.setAnswersForGridRadio(mAssesmant.get(i-1).getQid(),db,String.valueOf(surveyPrimaryKeyId));
                    ArrayAdapter<AnswersPage> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, mOptionsDroupdown);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);
                    if (mResponse != null && mResponse.size() > i - 1) {
                        spinner.setSelection(optionText.indexOf(mResponse.get(i - 1).getAnswer()));
                    }
                    optionwidgetLL.addView(spinner);
                    addView.add(spinner);
                    break;
                case "C":
                    List<AnswersPage> mOptionsCheckbox = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    LinearLayout checkboxContainer = new LinearLayout(context);
                    checkboxContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            409,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    checkboxContainer.setOrientation(LinearLayout.VERTICAL);
                    checkboxContainer.setGravity(Gravity.CENTER_VERTICAL);
                    //  List<Response> responseAnswerCheckbox= DataBaseMapperClass.setAnswersForGridRadio(mAssesmant.get(i-1).getQid(),db,String.valueOf(surveyPrimaryKeyId));
                    if (mOptionsCheckbox.size() > 0) {
                        for (int c = 0; c < mOptionsCheckbox.size(); c++) {
                            CheckBox checkbox = new CheckBox(context);
                                    /*if (responseAnswerCheckbox.size()>0){
                                        for (int getCheck=0;getCheck<responseAnswerCheckbox.size();getCheck++){
                                            if (mOptionsCheckbox.get(c).getId()==responseAnswerCheckbox.get(getCheck).getPrimaryID()
                                                    && String.valueOf(mSubQuestions.get(j-1).getQuestionId()).equals(responseAnswerCheckbox.get(getCheck).getSubquestionId())){
                                                checkbox.setChecked(true);
                                            }
                                        }
                                    }*/
                            checkbox.setText(mOptionsCheckbox.get(c).getAnswer());
                            checkboxContainer.addView(checkbox);

                        }
                    }
                    optionwidgetLL.addView(checkboxContainer);
                    addView.add(checkboxContainer);

                    break;
                case "D":
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
                            activity.showDateDialog(2, finalGetGroupVaidation, button);
                        }
                    });
                    optionwidgetLL.addView(button);
                    addView.add(button);
                    break;

                default:
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
                                //  layout.addView(editView);
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
        dialogBuilder.setView(dialogView);
        final TextView txt = (TextView) dialogView.findViewById(R.id.assessmentquestion);
        txt.setTypeface(null, Typeface.BOLD);
        final Button saveAndCreate = (Button) dialogView.findViewById(R.id.saveandcreate);
        final Button saveAndExit = (Button) dialogView.findViewById(R.id.saveandexit);
        if (!"".equals(skipCode)) {
            saveAndExit.setText("Next");
        } else {
            saveAndExit.setText("Save");
        }

        if (gridType == 14) {
            saveAndCreate.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            txt.setText(questionPageBean.getSubQuestion());
        } else {
            txt.setVisibility(View.GONE);
        }
        final int currentQuestionNumber = currentQuestionPage.getQuestionNumber();

        //        txt.setText(questionPageBean.getSubQuestion());
        dialogBuilder.setTitle(currentQuestionPage.getQuestion());
        saveAndCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowInflater++;
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
                            mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt("selectedLangauge", 1));
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

            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();
        saveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!saveAndExit.getText().equals("Next")) {
                    try {
                        if (gridType == 16) {
                            rowInflater++;
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
                                        mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt("selectedLangauge", 1));
                                    } else if (mAssesmant.get(i).getQtype().equalsIgnoreCase("T")) {
                                        mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                                    } else {
                                        mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 1));
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
                                //  ToastUtils.displayToast("please fill Mandatory field", context);
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
                                        // bellow if block code is for sub question based answers fetching
                                        if (MAssesmant.get(i).getQtype().equalsIgnoreCase("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt("selectedLangauge", 1));
                                        } else if (MAssesmant.get(i).getQtype().equalsIgnoreCase("T")) {
                                            mOptions = DataBaseMapperClass.getOptionsAnswersTEXTBOX(mAssesmant.get(i).getQid(), database);
                                        } else {
                                            mOptions = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i).getQid(), database, preferences.getInt("selectedLangauge", 0));
                                        }
                                        if (mOptions.size() > 0) {
                                            Response response = new Response(String.valueOf(currentQuestionNumber), answered.get(i), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, questionPageBean.getQuestionId(), String.valueOf(survey_ID), mAssesmant.get(i).getQid(), mOptions.get(0).getId(), MAssesmant.get(i).getQtype());
                                            responselist.add(response);
                                            // Logger.logD("GridValues in Response", responselist.get(i).toString());
                                        }
                                    } else {
                                        // else block code is for sub question based answers fetching
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
                        List<String> answeredtemp = methodToValidationField(currentQuestionNumber, context, addView, database, 16, null);
                        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        int survey_ID = prefs.getInt("survey_id", 0);
                        if (answeredtemp != null) {
                            String getSkipCode = answeredtemp.get(answeredtemp.size() - 1);
                            AssesmentBean getQuestionCode = mAssesmant.get(mAssesmant.size() - 1);
                            List<AnswersPage> mOptions = DataBaseMapperClass.getOptionsAnswersForGridSubQBased(getQuestionCode.getQid(), database, getSkipCode, true);
                            //  String skipcode = QuestionActivityUtils.checkSkipCode(String.valueOf(getQuestionCode.getQid()), database, String.valueOf(mOptions.get(0).getId()));
                            String skipcode = "";
                            List<AssesmentBean> MainMAssesmant = mainGridAssessmentMapDialog.get(String.valueOf(getQuestionCode.getAssessmentId()) + "_ASS");


                            if (!"".equals(skipcode)) {
                                List<Response> responselist = new ArrayList<>();
                                if (mOptions.size() > 0) {
                                    for (int j = 0; j < mAssesmant.size(); j++) {
                                        Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(j), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, 0, String.valueOf(survey_ID), mAssesmant.get(j).getQid(), mOptions.get(0).getId(), answeredtemp.get(j));
                                        responselist.add(response);
                                    }
                                }
                                for (int i = mainAcessmentList.indexOf(skipcode); i < MainMAssesmant.size(); i++) {
                                    AssesmentBean assessment = MainMAssesmant.get(i);
                                    mAssesmant.add(assessment);
                                }
                                b.dismiss();
                                gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", mAssesmant);
                                SupportClass.showChangeLangDialog(responselist, mAssesmant, context, activity, currentQuestionPage, database, child, 16, null, "");
                            } else {
                                b.dismiss();
                                List<Response> responselist = new ArrayList<>();
                                if (mOptions.size() > 0) {
                                    for (int j = 0; j < mAssesmant.size(); j++) {
                                        Response response = new Response(String.valueOf(currentQuestionNumber), answeredtemp.get(j), mOptions.get(0).getAnswerCode(), "0", currentQuestionNumber, 0, String.valueOf(survey_ID), mAssesmant.get(j).getQid(), mOptions.get(0).getId(), answeredtemp.get(j));
                                        responselist.add(response);
                                    }
                                }
                                gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", MainMAssesmant);
                                SupportClass.showChangeLangDialog(responselist, MainMAssesmant, context, activity, currentQuestionPage, database, child, 16, null, "");
                            }
                        }

                    } catch (Exception e) {
                        Logger.logE("Exception", "in", e);

                    }
                }

            }

        });


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

    public static void moduleToCreateInlineDialogForm(Page currentQuestionPage, SQLiteDatabase database, SurveyQuestionActivity surveyQuestionActivity, View child, SharedPreferences preferences) {
        Context context = surveyQuestionActivity;
        SurveyQuestionActivity activity = surveyQuestionActivity;
        final List<AssesmentBean> MAssesmant = DataBaseMapperClass.getAssesements(currentQuestionPage.getQuestionNumber(), database, preferences.getInt("selectedLangauge", 1));
        gridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", MAssesmant);
        mainGridAssessmentMapDialog.put(currentQuestionPage.getQuestionNumber() + "_ASS", MAssesmant);
        for (int i = 0; i < MAssesmant.size(); i++) {
            AssesmentBean assessmentbean = mainGridAssessmentMapDialog.get(currentQuestionPage.getQuestionNumber() + "_ASS").get(i);
            mainAcessmentList.add(String.valueOf(assessmentbean.getQid()));
        }
        gridQuestionMapDialog.put(currentQuestionPage.getQuestionNumber() + "_QUESTION", currentQuestionPage);
        List<AssesmentBean> mAssesmantmain = modifiedAssessementListOnSKIP(MAssesmant, database);
        Logger.logD("mAssesmantmain", "mAssesmantmain sorted list " + mAssesmantmain);
        SupportClass.showChangeLangDialog(null, MAssesmant, context, activity, currentQuestionPage, database, child, 16, null, "");
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
        Spannable word = new SpannableString(text);
        labelObj.setText(word);
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

    private static List<String> methodToValidationField(int currentQuestionNumber, Context context, List<View> addView, SQLiteDatabase database, int QType, Page page) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> answerList = new ArrayList<>();
        try {
            Logger.logD(LOGGER_TAG, "the dialog LinearLayout child count" + addView.size());
            List<AssesmentBean> MAssesmant = gridAssessmentMapDialog.get(currentQuestionNumber + "_ASS");
            for (int i = 0; i < MAssesmant.size(); i++) {
                String priorityQtype = "";
                if (QType == 14 && page != null) {
                    if (!page.getAnswer().equals("N")) {
                        priorityQtype = page.getAnswer();

                        Logger.logD("priorityQtype", "priorityQtype in SubQuestion" + priorityQtype);
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

    public static void showDialogEdit(final List<Response> mResponse, final List<AssesmentBean> mAssesmant, final Context context, final SurveyQuestionActivity activity, Page currentQuestionPage, final SQLiteDatabase database, final String hashMapKey, final View child, final int gridType, final Page subQuestionpage) {
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
            layout.addView(assessmentQuestionText);
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
                    EditText editView = new EditText(context);
                    editView.setText("");
                    editView.setTextColor(Color.BLACK);
                    subCount = subCount + 20 + 2 + i;
                    editView.setId(subCount);
                    editView.setLayoutParams(paramEdit);
                    paramEdit.setMargins(4, 4, 4, 4);
                    editView.setPadding(10, 0, 0, 10);
                    editView.setBackgroundResource(R.drawable.textfieldbg);

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

                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                        maxLength = array[3].length();
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    case "NOV":

                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                    default:
                                        optionwidgetLL.addView(editView);
                                        addView.add(editView);
                                        editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                        maxLength = Integer.parseInt(array[3]);
                                        FilterArray = new InputFilter[1];
                                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                        editView.setFilters(FilterArray);
                                        break;
                                }
                            } else {
                                optionwidgetLL.addView(editView);
                                addView.add(editView);
                                editView.setInputType(InputType.TYPE_CLASS_TEXT);
                                int maxLength = array[3].length();
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editView.setFilters(FilterArray);
                            }
                        }
                    } else {
                        optionwidgetLL.addView(editView);
                        addView.add(editView);

                    }
                    break;
                case "R":
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
                    List<AnswersPage> mOptionsDroupdown = DataBaseMapperClass.getOptionsAnswers(mAssesmant.get(i - 1).getQid(), database, preferences.getInt("selectedLangauge", 1));
                    List<String> optionText = new ArrayList<>();
                    Spinner spinner = new Spinner(context);

                    spinner.setLayoutParams(new RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    for (int d = 0; d < mOptionsDroupdown.size(); d++) {
                        optionText.add(mOptionsDroupdown.get(d).getAnswer());
                    }
                    ArrayAdapter<AnswersPage> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, mOptionsDroupdown);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
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

                    optionwidgetLL.addView(spinner);
                    addView.add(spinner);


                    break;
                case "C":
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
                            String string = getansweredOptionCode.replace("[", "").replace("]","").replace("\"","");
                            String[] array = string.split(",");


//
//                            if (!string.isEmpty())
//                                array = string.substring(1, string.length() - 1).split(",");
                            for (int k = 0; k < array.length; k++) {
                                if (mOptionsCheckbox.get(c).getAnswerCode().equals(array[k])) {
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
                                if (mResponse.get(k).getGroup_id()==(mAssesmant.get(l).getQid()) && mResponse.get(k).getQ_type().equalsIgnoreCase("D")) {
                                    button.setText(mResponse.get(k).getAnswer());
                                    Logger.logD(LOGGER_TAG, " date Answer" +mResponse.get(k).getAnswer());

                                }
                            }

                        }


                    }
                    optionwidgetLL.addView(button);
                    addView.add(button);
                    break;
                default:
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
                                // bellow if block code is for sub question based answers fetching
                                if (MAssesmant.get(i).getQtype().equalsIgnoreCase("R") || MAssesmant.get(i).getQtype().equalsIgnoreCase("S")) {
                                    mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt("selectedLangauge", 1));
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
                                // else block code is for sub question based answers fetching
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
                                mOptions = DataBaseMapperClass.getOptionsAnswersForGrid(mAssesmant.get(i).getQid(), database, answered.get(i), preferences.getInt("selectedLangauge", 1));
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
}
