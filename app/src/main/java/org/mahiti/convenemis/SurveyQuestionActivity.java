package org.mahiti.convenemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.AnswersPage;
import org.mahiti.convenemis.BeenClass.AssesmentBean;
import org.mahiti.convenemis.BeenClass.Page;
import org.mahiti.convenemis.BeenClass.Response;
import org.mahiti.convenemis.BeenClass.SetAnswers;
import org.mahiti.convenemis.BeenClass.parentChild.Level1;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.DataBaseMapperClass;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.network.InsertResponseDump;
import org.mahiti.convenemis.network.InsertTask;
import org.mahiti.convenemis.network.SurveyGridInlineInterface.surveyQuestionGridInlineInterface;
import org.mahiti.convenemis.network.SurveyGridInlineInterface.surveyQuestionPreviewInterface;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.CommonForAllClasses;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.ConstantsUtils;
import org.mahiti.convenemis.utils.FileUtils;
import org.mahiti.convenemis.utils.FontUtils;
import org.mahiti.convenemis.utils.InternalStorageContentProvider;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.Operator;
import org.mahiti.convenemis.utils.PermissionClass;
import org.mahiti.convenemis.utils.PreferenceConstants;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.QuestionActivityUtils;
import org.mahiti.convenemis.utils.RestUrl;
import org.mahiti.convenemis.utils.RuleEngineUtils;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;
import org.mahiti.convenemis.utils.ValidationUtils;
import org.mahiti.convenemis.utils.multispinner.SingleSpinnerSearchFilter;
import org.mahiti.convenemis.utils.multispinner.SpinnerListenerFilter;

import java.io.File;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static org.mahiti.convenemis.database.DataBaseMapperClass.getUserAnsweredResponseFromDB;
import static org.mahiti.convenemis.database.DataBaseMapperClass.setAnswersForGrid;
import static org.mahiti.convenemis.utils.Constants.GridResponseHashMap;
import static org.mahiti.convenemis.utils.Constants.GridResponseHashMapKeys;
import static org.mahiti.convenemis.utils.Constants.buttonDynamicDateGrid;
import static org.mahiti.convenemis.utils.Constants.fillInlineHashMapKey;
import static org.mahiti.convenemis.utils.Constants.fillInlineRow;
import static org.mahiti.convenemis.utils.Constants.gridAssessmentMapDialog;
import static org.mahiti.convenemis.utils.Constants.gridQuestionMapDialog;
import static org.mahiti.convenemis.utils.Constants.gridSubQuestionMapDialog;
import static org.mahiti.convenemis.utils.Constants.listHashMapKey;
import static org.mahiti.convenemis.utils.Constants.rowInflater;



public class SurveyQuestionActivity extends BaseActivity implements View.OnClickListener, surveyQuestionPreviewInterface ,
        surveyQuestionGridInlineInterface {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String dateFormat = "dd-MM-yyyy";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    static final int DATE_DIALOG_ID = 1;

    //-------------------------*******************--------------------
    public static final List<String> getAllEditTextQuestionCode = new ArrayList<>();
    public static final List<String> getAllradiobuttonQuestionCode = new ArrayList<>();
    public static final List<String> getAllcheckboxQuestionCode = new ArrayList<>();
    public static final List<String> getAllspinnerQuestionCode = new ArrayList<>();
    public static final List<String> getAlldateQuestionCode = new ArrayList<>();
    public static final List<String> getAllImageuploadQuestionCode = new ArrayList<>();
    private static final int POP_UP_ACTIVITY = 200;
    private static final String QUESTION = "_QUESTION";

    private HashMap<String, LinearLayout> gridViewLinearLayoutHolder = new HashMap<>();

    private static final String TAG = "SurveyQuestionActivity";
    private static final String SURVEYID = "survey_id";
    private static final String TOOLTIP_COLOR = "#808080";
    private static final String MY_PREFS_NAME_SURVEY = "MyPrefs";
    private static final String MY_PREFERENCES = "MyPrefs";
    public static List<Response> answersEditText = new ArrayList<>();
    final int REQUEST_CODE_TAKE_PICTURE = 2;
    final int REQUEST_CODE_GALLERY = 1;
    public AnswersPage answerspage = null;
    protected List<Integer> blockIds;
    HashMap<String, List<Response>> hashMapAnswersEditText = new HashMap<>();
    private HashMap<String, LinearLayout> gridViewLinearLayoutHolderInline = new HashMap<>();

    int editcount = 0;
    int radiocount = 0;
    int checkCount = 0;
    int spinnerCount = 0;
    int imageUploadCount = 0;
    int dateCount = 0;
    float charge = 0;
    static int GridCount = 0;
    static int gridCountInline = 0;

    LinearLayout dynamicQuestionSet;
    boolean skipBlockLevelFlag = false;
    boolean showPopUpFlag = false;
    LinearLayout layoutCont;
    RadioButton[] radioButtons = new RadioButton[50];
    net.sqlcipher.database.SQLiteDatabase db;
    DBHandler surveyHandler;
    ArrayList<String> listQuestionType;
    List<String> mainQList;
    List<EditText> allEds = new ArrayList<>();
    List<RadioGroup> allRadioGroups = new ArrayList<>();
    List<SetAnswers> setAnswersList;
    // --------------- ****************--------------
    int questionDisplayPageCount = 1;
    HashMap<String, List<String>> multiMapOdd = new HashMap<>();
    int lastIndexUsedToFetchQID = 0;
    int dateDialogId = 1;
    int setDateView = 0;
    String pathOfImage = "";
    String dirName = "/surveyforms/";
    EditText latitudeValue;
    EditText longitudeValue;
    SimpleDateFormat sdf;
    List<String> questionPageList;
    List<String> answeredList;
    int count = 0;
    String surveyPrimaryKeyId = "";
    String skipcode = "";
    String qValidation = "";
    Animation animShake;
    SharedPreferences surveyPreferences;
    SharedPreferences.Editor editor;
    int questionIndex = 1;
    CheckNetwork chckNework;
    Operator operatorObj;
    ProgressDialog loginDialog;
    TextView blockName;
    List<String> allBlocksQList;
    boolean skipFlag = false;
    RestUrl restUrl;
    //  currentPageLastQuestionResponseCode variable is to get the response code before doing delete and insert
    String currentPageLastQuestionResponseCode = "";
    // unique integer Id generator
    int uniqueId = 0;
    HashMap<String, EditText> hashMap = new HashMap<>();
    HashMap<String, Button> hashMapDate = new HashMap<>();
    HashMap<String, Button> buttonDynamic = new HashMap<>();
    ArrayList<Button> bt = new ArrayList<>();
    HashMap<String, RadioGroup> hashMapRadio = new HashMap<>();
    HashMap<String, LinearLayout> hashMapCheck = new HashMap<>();
    HashMap<String, Spinner> hashMapDropdown = new HashMap<>();
    HashMap<String, List<AnswersPage>> hashMapForAnswerBeen = new HashMap<>();
    HashMap<String, TextView> hashMapTextError = new HashMap<>();
    Map<String,Spinner> dynamicSpinnerHashMap= new HashMap<>();
    File mFileTemp;
    int pageSetCount = 0;
    private String radioAnswerCode;
    private SQLiteDatabase surveyDatabase;
    private Button previousButton;
    private Button nextB;
    private int mYear;
    private int mMonth;
    private int mDay;
    // mentioned this object as global since onActivity result will return the path of captured or uploaded
    private ImageView imaimageUpload;
    private int statusFlag;
    private SharedPreferences prefs;
    private String qcode;
    private List<String> list;
    private ConveneDatabaseHelper dbOpenHelper;
    private HashMap<String, String> values;
    private List<Page> listOfPage;
    private boolean hasBlockId;
    private List<String> deletedCodes = new ArrayList<>();
    private AlertDialog alertDialog;
    private boolean saveToDraftFlag = false;
    private ScrollView scrollView;
    private int surveysId;
    String getParentsBeneficiary="";
    String getParentsBeneficiaryName="";
    List<String> getAllGridQuestionCode = new ArrayList<>();
    List<String> getAllGridQuestionCodeInline = new ArrayList<>();
    surveyQuestionGridInlineInterface surveyQuestionGridInlineInterface;

    /**
     * Date picker dialogue showing
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (view != null) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        }
    };
    private ExternalDbOpenHelper dbhelper;
    private SingleSpinnerSearchFilter spinnerSearch;
    private List<Integer> getBenificiaryQids;
    private List<Integer> getAddressQuestionIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        scrollView = findViewById(R.id.scrollView);
        toolbarTitle.setText(prefs.getString("Survey_tittle", null));
        LinearLayout pressBack = findViewById(R.id.backPress);
        dynamicQuestionSet = findViewById(R.id.dynamicQuestionSet);
        blockName = findViewById(R.id.blockName);
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        surveyPreferences = PreferenceManager.getDefaultSharedPreferences(SurveyQuestionActivity.this);
        chckNework = new CheckNetwork(this);
        surveyQuestionGridInlineInterface = SurveyQuestionActivity.this;
        SupportClass supportClass = new SupportClass();
        restUrl = new RestUrl(this);
        Intent surveyPrimaryKeyIntent = getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));
        }
        values = new HashMap<>();
        dbOpenHelper = ConveneDatabaseHelper.getInstance(this, surveyPreferences.getString(Constants.CONVENE_DB, ""), surveyPreferences.getString("UID", ""));
        dbhelper = ExternalDbOpenHelper.getInstance(this, surveyPreferences.getString(Constants.DBNAME, ""), surveyPreferences.getString("UID", ""));
        sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        String state = Environment.getExternalStorageState();
        FontUtils.setFontT0Preference(1, getApplicationContext());

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(this.getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        surveyDatabase = dbOpenHelper.getReadableDatabase();
        /*
         * Loading the encryption sqlcipher library
         */
        try {
            net.sqlcipher.database.SQLiteDatabase.loadLibs(this);
            surveyHandler = new DBHandler(this);
        } catch (Exception e) {
            Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in SurveyLoginActivity onCreate method ", e);
        }
        surveyHandler = new DBHandler(this);
        db = surveyHandler.getdatabaseinstance();
        nextB = findViewById(R.id.next);
        previousButton = findViewById(R.id.button1);
        TextView menuIconOptions = findViewById(R.id.imageMenu);
        setAnswersList = new ArrayList<>();
        listQuestionType = new ArrayList<>();
        mainQList = new ArrayList<>();
        answeredList = new ArrayList<>();
        questionPageList = new ArrayList<>();
        listOfPage = new ArrayList<>();
        list = new ArrayList<>();
        allBlocksQList = new ArrayList<>();
        blockIds = new ArrayList<>();
        clearAllWidgetMapCounts();
        if (questionDisplayPageCount == 1) {
            previousButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
        Logger.logV(TAG, "Surveyids" + surveysId);
        hasBlockId = DataBaseMapperClass.checkBlockFromSurvey(surveyDatabase, surveysId);
        mainQList.clear();
        pageSetCount = 0;
        allBlocksQList = DataBaseMapperClass.getQuestionBasedOnBlocks(surveyDatabase, surveysId, restUrl);
        Logger.logD(TAG, "allqlist in the hasBlockListElse" + allBlocksQList.size() + "-->" + allBlocksQList.toString());
        mainQList = DataBaseMapperClass.callDBForQuestionCode(surveyDatabase, surveysId, restUrl);// main Getting Question From DB
        Logger.logD(TAG, "MainList in the hasBlockListElse" + mainQList.size() + "-->" + mainQList.toString());
        /*
         * the below code to Update the Response to SD card.
         */
        supportClass.setSaveData(false, SurveyQuestionActivity.this);
        nextB.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        pressBack.setOnClickListener(this);
        preClearAllGlobalHashMap();
        /*Resume Survey Functionality*/
        if ("Yes".equals(surveyPreferences.getString(PreferenceConstants.RESUME_SURVEY, ""))) {
            int index = 0;
            db = surveyHandler.getdatabaseinstance();
            surveyDatabase = dbOpenHelper.getReadableDatabase();
            try {
                questionPageList.clear();
                answeredList = DataBaseMapperClass.getPendingAnsweredQuestionIds(db, surveyPrimaryKeyId, restUrl);
                if (answeredList.isEmpty()) {
                    ModuleToSetForm(count, pageSetCount, mainQList);
                }
                int lastIndexPostion = 0;
                do {
                    list = getHashMapValuesSet(index, mainQList);
                    lastIndexPostion = Integer.parseInt(list.get(list.size() - 1));
                    multiMapOdd.put(String.valueOf(questionDisplayPageCount), list);
                    questionDisplayPageCount++;
                    index = mainQList.indexOf(list.get(list.size() - 1));
                    index = index + 1;
                    if (index >= mainQList.indexOf(mainQList.get(mainQList.size() - 1))) {
                        pageSetCount++;
                        index = 0;
                        mainQList = DataBaseMapperClass.getQuestionsFromBlocks(surveyDatabase, prefs.getInt(SURVEYID, 0), pageSetCount, RuleEngineUtils.fetchRuleEngineFromPrefs(prefs.getString(Constants.RULE_ENGINE, "")));
                    }
                }
                while (lastIndexPostion != Integer.parseInt(answeredList.get(answeredList.size() - 1)));
                List<String> pendingListQids = multiMapOdd.get(String.valueOf(questionDisplayPageCount - 1));
                for (int j = 0; j < pendingListQids.size(); j++) {
                    lastIndexUsedToFetchQID = lastIndexUsedToFetchQID + 1;
                    ModuleForCreatingUI(pendingListQids.get(j));
                    questionIndex++;
                }
                questionDisplayPageCount--;
                pageSetCount++;
            } catch (Exception e) {
                Logger.logE(TAG, "Exception on Pending Selected Survey ", e);
                restUrl.writeToTextFile("Exception on Pending Selected Survey", "", "pendingSurvey");
            }
            editor = surveyPreferences.edit();
            editor.putString(PreferenceConstants.RESUME_SURVEY, "");
            editor.putBoolean(PreferenceConstants.SECTION_CHANGED, false);
            editor.apply();
        } else {
            ModuleToSetForm(count, pageSetCount, mainQList);

        }

        if (surveyPreferences.getBoolean("SaveDraftButtonFlag", false)) {
            menuIconOptions.setVisibility(View.GONE);
        } else {
            menuIconOptions.setVisibility(View.GONE);
        }
        menuIconOptions.setOnClickListener(this);

        // below code is get the device information to analyse the statistics
        operatorObj = new Operator(this);
        new OperatorTask(SurveyQuestionActivity.this).execute();

    }

    /**
     * preClearAllGlobalHashMap pre clearing the global hash map
     */
    private void preClearAllGlobalHashMap() {
        fillInlineRow.clear();
        fillInlineHashMapKey.clear();
        GridResponseHashMap.clear();
        GridResponseHashMapKeys.clear();
        listHashMapKey.clear();

    }

    /**
     * @param index
     * @param mainQList
     * @return
     */
    private List<String> getHashMapValuesSet(int index, List<String> mainQList) {
        List<String> filterList = new ArrayList<>();
        for (int i = index; i < mainQList.size(); i++) {
            if (answeredList.contains(mainQList.get(i))) {
                surveyDatabase = dbOpenHelper.getWritableDatabase();
                String skipQid = QuestionActivityUtils.checkPendingSkipCode(mainQList.get(i), surveyDatabase, this.restUrl);
                if (!("").equals(skipQid)) {
                    filterList.add(mainQList.get(i));
                    break;
                } else {
                    qValidation = QuestionActivityUtils.questionSkipLogic(mainQList.get(i), surveyDatabase);
                    if (("").equals(qValidation)) {
                        filterList.add(mainQList.get(i));
                    } else {
                        filterList.add(mainQList.get(i));
                        break;
                    }
                }
            } else {
                return filterList;
            }
        }
        return filterList;
    }

    @Override
    public void onBackPressed() {
        //TODO need to change the above code .
      if (surveyPreferences.getString(Constants.SURVEYSTATUSTYPR,"").equals("new")){
          Logger.logD(TAG,"New Survey");
          SupportClass supportClass = new SupportClass();
          supportClass.backButtonFunction(SurveyQuestionActivity.this, db, surveyHandler, surveyPrimaryKeyId);
      }else{
          Logger.logD(TAG,"Edit Survey");
          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
          alertDialogBuilder.setTitle(R.string.exitSurvey).setPositiveButton("YES", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  finish();
              }
          }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  Logger.logD(TAG,"onClick");
              }
          }).show();

      }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.font:
                FontUtils.setFontDialog(getApplicationContext(), this);
                return true;
            case R.id.backpress:
                clearAllWidgetMapCounts();
                SupportClass supportClass = new SupportClass();
                supportClass.backButtonFunction(SurveyQuestionActivity.this, db, surveyHandler, surveyPrimaryKeyId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @param count
     * @param pageSetCount
     * @param tempQidsList
     */
    private void ModuleToSetForm(int count, int pageSetCount, List<String> tempQidsList) {

        try {
            Logger.logD(TAG, "1. ModuleToSetForm - questionDisplayPageCount Value " + questionDisplayPageCount);
            if (questionDisplayPageCount == 1) {
                previousButton.setVisibility(View.GONE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
            }
            //setting 5 Questions in One Page
            questionPageList = QuestionActivityUtils.getQuestionFromMainList(count, tempQidsList, surveyDatabase, pageSetCount, prefs.getInt(SURVEYID, 0), restUrl,dbOpenHelper);
            List<String> currentPageQIDS = new ArrayList<>();
            int pageCount = questionPageList.size();
            for (int i = 0; i < pageCount; i++) {
                if (mainQList.contains(questionPageList.get(i))) {
                    lastIndexUsedToFetchQID = lastIndexUsedToFetchQID + 1;
                }
                currentPageQIDS.add(questionPageList.get(i));

            }
            multiMapOdd.put(String.valueOf(questionDisplayPageCount), currentPageQIDS);


            // bellow piece of code is for setting next button as Submit (questions with out any blocks)
            if (currentPageQIDS.get(currentPageQIDS.size() - 1).equals(allBlocksQList.get(allBlocksQList.size() - 1))) {
                statusFlag = moduleToHideShowButton(true, 1);
            } else {
                statusFlag = moduleToHideShowButton(false, 0);
                multiMapOdd.put(String.valueOf(questionDisplayPageCount), currentPageQIDS);
            }

            surveyDatabase = dbOpenHelper.getWritableDatabase();
            for (int j = 0; j < currentPageQIDS.size(); j++) {
                questionIndex = mainQList.indexOf(currentPageQIDS.get(j)) + 1;
                ModuleForCreatingUI(currentPageQIDS.get(j));
                questionIndex++;
            }
        } catch (Exception e) {
            Log.e("Excetion In UI", "Exception", e);
        }
    }

    /**
     * @param V
     */
    private void moduleForSettingNextQuestion(ViewGroup V) {
        V.removeAllViews();
    }

    /**
     * @param questionCode
     */
    private void ModuleForCreatingUI(String questionCode) {

        List<AnswersPage> mAnswersEditTextPage = DataBaseMapperClass.getAnswersForQuestionFromDB(questionCode, surveyDatabase, restUrl);               // Getting all the Answer and fill to answer Pager list
        answerspage = mAnswersEditTextPage.get(0);
        hashMapForAnswerBeen.put(questionCode, mAnswersEditTextPage);                                     // AnswerPageBeen contain capture Answer
        mapperForEntity(Integer.parseInt(questionCode));

    }

    /**
     * @param questionCode
     */
    private void mapperForEntity(int questionCode) {

        if (!surveyDatabase.isOpen()) {
            surveyDatabase = dbOpenHelper.getWritableDatabase();
        }
        listOfPage = DataBaseMapperClass.getQuestionOnBlocks(questionCode, surveyDatabase, surveysId, surveyPreferences.getInt(Constants.SELECTEDLANGUAGE, 0), mainQList);
        for (int k = 0; k < listOfPage.size(); k++) {
            int selectQuestionType = listOfPage.get(k).getQuestionId();
            int blockId = listOfPage.get(k).getBlockId();
            String blockNameTitle = DataBaseMapperClass.getBlockName(surveyDatabase, surveysId, blockId, surveyPreferences.getInt("selectedLanguage", 0));
            blockName.setText(blockNameTitle);
            listQuestionType.add(String.valueOf(selectQuestionType));
            String questionFont = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("question", "18");
            String answerFont = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("answer", "16");

            switch (selectQuestionType) {
                case 1:
                    textFieldQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 4:
                    radioGroupQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 2:
                    checkBoxQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 6:
                    dropDownQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 5:
                    dateQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 8:
                    imageQuestionDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 9:
                    addressWidgetDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 10:
                   beneficiaryParentDisplay(listOfPage.get(k), questionFont, answerFont, questionCode);
                    break;
                case 14:
                    normalGirdDisplay(listOfPage.get(k), questionCode);
                    break;
                case 16:
                    inLineGridDisplay(listOfPage.get(k));
                    break;
                default:
                    break;
            }
        }

    }

    private void inLineGridDisplay(Page page) {
        final View childInline = getLayoutInflater().inflate(R.layout.dialoginline, dynamicQuestionSet, false);
       TextView question = (TextView) childInline.findViewById(R.id.mainQuestion);
        Button dynamicInlineAdd = (Button) childInline.findViewById(R.id.addorcreateinline);
        validateMandatoryView(page,childInline,question);


        final Page questionID = page;
        final int getCurrentQuestionID = page.getQuestionNumber();
        dynamicInlineAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportClass.moduleToCreateInlineDialogForm(questionID, surveyDatabase, SurveyQuestionActivity.this, childInline, defaultPreferences);
            }
        });
        gridQuestionMapDialog.put(getCurrentQuestionID+QUESTION,questionID);
        try {
            final List<Response> setAnswersListInline = setAnswersForGrid(page.getQuestionNumber(), db, String.valueOf(surveyPrimaryKeyId));
            if (setAnswersListInline.size() > 0) {
                List<Integer> getinlineRowCount = DataBaseMapperClass.getRowCount(page.getQuestionNumber(), db, String.valueOf(surveyPrimaryKeyId));
                Logger.logD("getinlineRowCount", "the inline row Count size->" + getinlineRowCount.size());
                for (int i = 0; i < getinlineRowCount.size(); i++) {
                    List<Response> sortedList = new ArrayList<>();
                    listHashMapKey.add(getCurrentQuestionID + "_" + getinlineRowCount.get(i));
                    for (int j = 0; j < setAnswersListInline.size(); j++) {
                        if (setAnswersListInline.get(j).getPrimarykey() == getinlineRowCount.get(i)) {
                            Logger.logD("Add sorted list", "add only fo Primary key exist");
                            Response responsefill = setAnswersListInline.get(j);
                            sortedList.add(responsefill);
                            Logger.logD("Add sorted list", "the size of the sorted list");
                        }
                    }
                    fillInlineRow.put(String.valueOf(page.getQuestionNumber()) + "_" + getinlineRowCount.get(i), sortedList);
                    Logger.logD(TAG, "the list of the hashMap" + fillInlineRow.size());
                    Logger.logD("listHashMapKey--<<>>>", listHashMapKey.toString() + "");

                    fillInlineHashMapKey.put(String.valueOf(getCurrentQuestionID), listHashMapKey);
                }

                rowInflater = fillInlineRow.size() + 1;
                surveyQuestionGridInlineInterface.OnSuccessfullGridInline(fillInlineRow, childInline, getCurrentQuestionID, fillInlineHashMapKey, 16);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception", e);
        }
        dynamicQuestionSet.addView(childInline);
        gridViewLinearLayoutHolderInline.put(String.valueOf(page.getQuestionNumber()), dynamicQuestionSet);
        getAllGridQuestionCodeInline.add(String.valueOf(page.getQuestionNumber()));
    }

    private void validateMandatoryView(Page page, View childInline, TextView question) {
        if (page.getMandatory().contains("1")) {
            if (!page.getToolTip().equalsIgnoreCase("")) {
                final String getHelpText = page.getToolTip();
                ImageView tooltip = (ImageView) childInline.findViewById(R.id.tooltip);
                tooltip.setVisibility(View.VISIBLE);
                SupportClass.setWhiteStar(question, page.getQuestion());
                tooltip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SimpleTooltip.Builder(SurveyQuestionActivity.this)
                                .anchorView(v)
                                .text(getHelpText)
                                .gravity(Gravity.END)
                                .animated(true)
                                .transparentOverlay(false)
                                .build()
                                .show();
                    }
                });
            } else {
                SupportClass.setWhiteStar(question, page.getQuestion());
            }
        } else {
            if (!page.getToolTip().equalsIgnoreCase("")) {
                ImageView tooltip = (ImageView) childInline.findViewById(R.id.tooltip);
                tooltip.setVisibility(View.VISIBLE);
                question.setText(page.getQuestion());
            } else {
                question.setText(page.getQuestion());
            }

        }
    }

    private void normalGirdDisplay(Page page, int questionCode) {
        View   child = getLayoutInflater().inflate(R.layout.dialog_grid, dynamicQuestionSet, false);//child.xml
        TextView   question = (TextView) child.findViewById(R.id.mainQuestion);
        if (page.getMandatory().contains("1")) {
            if (!page.getToolTip().equalsIgnoreCase("")) {
                final String getHelpText = page.getToolTip();
                ImageView tooltip = (ImageView) child.findViewById(R.id.tooltip);
                tooltip.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(question, page.getQuestion());
                tooltip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SimpleTooltip.Builder(SurveyQuestionActivity.this)
                                .anchorView(v)
                                .text(getHelpText)
                                .gravity(Gravity.END)
                                .animated(true)
                                .arrowColor(Color.parseColor("#808080"))
                                .transparentOverlay(false)
                                .build()
                                .show();
                    }
                });
            } else {
                SupportClass.setRedStar(question, page.getQuestion());
            }
        } else {
            if (!page.getToolTip().equalsIgnoreCase("")) {

                final String getHelpText = page.getToolTip();
                ImageView tooltip = (ImageView) child.findViewById(R.id.tooltip);
                tooltip.setVisibility(View.VISIBLE);
                SupportClass.setRedStar(question, page.getQuestion());
                tooltip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SimpleTooltip.Builder(SurveyQuestionActivity.this)
                                .anchorView(v)
                                .text(getHelpText)
                                .gravity(Gravity.END)
                                .animated(true)
                                .arrowColor(Color.parseColor("#808080"))
                                .transparentOverlay(false)
                                .build()
                                .show();
                    }
                });
            } else {
                question.setText(page.getQuestion());
            }

        }
        createGridViewUpdate(child, page.getQuestionNumber(), page);
        dynamicQuestionSet.addView(child);
        getAllGridQuestionCode.add(String.valueOf(page.getQuestionNumber()));
    }

    private void createGridViewUpdate(final View gridchild, int questionNumber, final Page page) {

        /**
         * getting Question ID , fettching the Assessment ,subquestion, GridQuestion.
         */
        final List<String> GridlistHashMapKey = new ArrayList<>();
        final int getCurrentGridQuestionID = questionNumber;
        final List<Response> setAnswers_listInline = surveyHandler.setAnswersForGrid(getCurrentGridQuestionID, String.valueOf(surveyPrimaryKeyId));
        final List<AssesmentBean> MAssesmant = DataBaseMapperClass.getAssesements(getCurrentGridQuestionID, surveyDatabase, defaultPreferences.getInt("selectedLangauge", 0));
        final List<Page> mSubQuestions = DataBaseMapperClass.getSubquestionNew(getCurrentGridQuestionID, surveyDatabase, defaultPreferences.getInt("selectedLangauge", 0));
        gridSubQuestionMapDialog.put(getCurrentGridQuestionID + "_SUBQ", mSubQuestions);
        gridAssessmentMapDialog.put(String.valueOf(getCurrentGridQuestionID) + "_ASS", MAssesmant);
        gridQuestionMapDialog.put(String.valueOf(getCurrentGridQuestionID) + QUESTION, page);
        if (!setAnswers_listInline.isEmpty()) {
            for (int preSubQue = 0; preSubQue < mSubQuestions.size(); preSubQue++) {
                List<Response> getAnswer = new ArrayList<>();
                for (int preAnswer = 0; preAnswer < setAnswers_listInline.size(); preAnswer++) {
                    if (mSubQuestions.get(preSubQue).getQuestionId() == setAnswers_listInline.get(preAnswer).getPrimarykey()  ) {
                        Response response = setAnswers_listInline.get(preAnswer);
                        getAnswer.add(response);
                    }
                }
                GridResponseHashMap.put(String.valueOf(getCurrentGridQuestionID) + "_" + String.valueOf(mSubQuestions.get(preSubQue).getQuestionId()), getAnswer);
                GridlistHashMapKey.add(String.valueOf(getCurrentGridQuestionID) + "_" + String.valueOf(mSubQuestions.get(preSubQue).getQuestionId()));
                GridResponseHashMapKeys.put(String.valueOf(getCurrentGridQuestionID), GridlistHashMapKey);
            }
        }
        LinearLayout gridListLinearLayout = (LinearLayout) gridchild.findViewById(R.id.gridansweredList);

        for (int subRow = 0; subRow < mSubQuestions.size(); subRow++) {
            final View adapterView = getLayoutInflater().inflate(R.layout.gridviewlist_adapter, gridListLinearLayout, false);
            TextView setSubquestionText = (TextView) adapterView.findViewById(R.id.subquestiontext);
            TextView setAssessmentText = (TextView) adapterView.findViewById(R.id.assessmenttext);
            Button addOrEdit = (Button) adapterView.findViewById(R.id.addEdit);
            if (!GridResponseHashMapKeys.isEmpty()) {
                for (int responseSet = 0; responseSet < GridlistHashMapKey.size(); responseSet++) {
                    String[] spiltKey = GridlistHashMapKey.get(responseSet).split("_");
                    if (String.valueOf(mSubQuestions.get(subRow).getQuestionId()).equals(spiltKey[1])) {
                        addOrEdit.setText("Edit");
                        addOrEdit.setTag(String.valueOf(String.valueOf(getCurrentGridQuestionID + "_" + mSubQuestions.get(subRow).getQuestionId()) + "@EDIT"));

                    }

                }

            } else {
                addOrEdit.setTag(String.valueOf(String.valueOf(getCurrentGridQuestionID + "_" + mSubQuestions.get(subRow).getQuestionId()) + "@ADD"));

            }
            final int subRowTemp = subRow;
            addOrEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.logD(TAG, "Clicked button Tag ->" + v.getTag());
                    String[] spiltButtonTag = v.getTag().toString().split("@");
                    if (spiltButtonTag[1].equals("ADD")) {
                        SupportClass.createDialogFOrGrid(mSubQuestions.get(subRowTemp), v, SurveyQuestionActivity.this, surveyDatabase, getCurrentGridQuestionID);
                    } else if (spiltButtonTag[1].equals("EDIT")) {
                        String getResponseHashMapKey = spiltButtonTag[0];
                        if (GridResponseHashMap.size() > 0) {
                            List<Response> getAnsweredResponse = GridResponseHashMap.get(getResponseHashMapKey);
                            Logger.logD(TAG, "The Size of the edit Response ->" + getAnsweredResponse.size());
                            if (getAnsweredResponse.size() > 0) {
                                SupportClass.showDialogEdit(getAnsweredResponse, MAssesmant, SurveyQuestionActivity.this, SurveyQuestionActivity.this, page, surveyDatabase, spiltButtonTag[0], v, 14, mSubQuestions.get(subRowTemp));
                            } else {
                                ToastUtils.displayToast("Response empty", SurveyQuestionActivity.this);
                            }
                        }

                    }
                }
            });
            setSubquestionText.setText(mSubQuestions.get(subRow).getSubQuestion());
            setAssessmentText.setText("");
            gridListLinearLayout.addView(adapterView);
            gridViewLinearLayoutHolder.put(String.valueOf(getCurrentGridQuestionID), gridListLinearLayout);

        }

    }

    private void beneficiaryParentDisplay(Page page, String questionFont, String answerFont, int questionCode) {

        getBenificiaryQids= new ArrayList<>();
        View child = this.getLayoutInflater().inflate(R.layout.parentselecting, dynamicQuestionSet, false);//child.xml
        LinearLayout singleSpinnerContainer = (LinearLayout) child.findViewById(R.id.singlespinnercontainer);
        TextView parentHeading = (TextView) child.findViewById(R.id.parent_heading);
        if (page.getQuestion()!=null && !page.getQuestion().equals(""))
            parentHeading.setText(page.getQuestion());
        spinnerSearch=new SingleSpinnerSearchFilter(this);
        singleSpinnerContainer.addView(spinnerSearch);
        dynamicQuestionSet.addView(child);
        List<LevelBeen> list= DataBaseMapperClass.getBenificiaryParentDetails(db,page.getPartnerId());
        HashMap<String, AnswersPage> getDataAnswer = getUserAnsweredResponseFromDB(page.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);   // getting if already answered question
        int getIndex=0;
        if (!getDataAnswer.isEmpty()){
            for(int p=0;p<list.size();p++){
                AnswersPage answersPage=getDataAnswer.get(String.valueOf(questionCode));
                if (answersPage.getAnswer().equals(list.get(p).getUuid()))
                    getIndex=p;
            }
        }
        spinnerSearch.setFilterItems(list, getIndex, new SpinnerListenerFilter() {

            @Override
            public void onItemsSelected(List<LevelBeen> items) {
                try {
                    for(int j=0;j<items.size();j++){
                        if (items.get(j).isSelected()) {
                            Logger.logD("Selected","Item is"+list.get(j).getUuid());
                            getParentsBeneficiary=list.get(j).getUuid();
                            getParentsBeneficiaryName=list.get(j).getName();
                            getBenificiaryQids.add(page.getQuestionNumber());
                        }
                    }
                }catch (Exception e){
                    Logger.logE(TAG,"onItemsSelected in ",e);
                }

            }
        });
    }

    private void addressWidgetDisplay(Page page, String questionFont, String answerFont, int questionCode) {

        View child = this.getLayoutInflater().inflate(R.layout.address_widget, dynamicQuestionSet, false);//child.xml
  /*  TextView question = child.findViewById(R.id.mainQuestion);
    question.setText(page.getQuestion());*/
        LinearLayout relativeLayout = (LinearLayout) child.findViewById(R.id.relativeLayout);
        Spinner hamletspinner = (Spinner) child.findViewById(R.id.hamlet_spinner);
        Spinner villagespinner = (Spinner) child.findViewById(R.id.village_spinner);
        Spinner gramaPanchayathspinner = (Spinner) child.findViewById(R.id.gramaPanchayath_spinner);
        Spinner talukspinner = (Spinner) child.findViewById(R.id.taluk_spinner);
        Spinner districtspinner = (Spinner) child.findViewById(R.id.district_spinner);
        Spinner statespinner = (Spinner) child.findViewById(R.id.state_spinner);
        LinearLayout dynmicspinner = (LinearLayout) child.findViewById(R.id.dynmic_spinner);
        Spinner spinner = (Spinner) child.findViewById(R.id.spinner);
        TextView errorTextdropdownview = (TextView) child.findViewById(R.id.errorTextdropdownview);
        TextView mainQuestionspinner = (TextView) child.findViewById(R.id.mainQuestionspinner);
        List<Level1> countryList=dbhelper.setStateSpinner("Level1");
        List<Level1> statepinnerList=dbhelper.setStateSpinner("Level2");
        List<Level1> districtList=dbhelper.setStateSpinner("Level3");
        List<Level1> talukList=dbhelper.setStateSpinner("Level4");
        List<Level1> gpList=dbhelper.setStateSpinner("Level5");
        List<Level1> villageList=dbhelper.setStateSpinner("Level6");
        List<Level1> hamletList=dbhelper.setStateSpinner("Level7");

        ArrayAdapter<Level1> spinnerArrayAdapter = new ArrayAdapter<Level1>(this, R.layout.spinner_multi_row_textview, countryList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level2",surveyPrimaryKeyId,db);
                List<Level1> stateList=dbhelper.setSpinnerByID("level1_id","Level2",countryList.getId());
                ArrayAdapter<Level1> districtArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                districtArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                statespinner.setAdapter(districtArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        statespinner.setSelection(k);
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level3",surveyPrimaryKeyId,db);
                List<Level1> stateList=dbhelper.setSpinnerByID("level2_id","Level3",countryList.getId());
                ArrayAdapter<Level1> districtArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                districtArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                districtspinner.setAdapter(districtArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        districtspinner.setSelection(k);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level4",surveyPrimaryKeyId,db);

                List<Level1> stateList=dbhelper.setSpinnerByID("level3_id","Level4",countryList.getId());
                if (stateList.isEmpty()) {
                    Level1 level1 = new Level1(0, 0, "", 0, "Select Taluk");
                    stateList.add(level1);
                }
                ArrayAdapter<Level1> taArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                taArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                talukspinner.setAdapter(taArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        talukspinner.setSelection(k);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        talukspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level5",surveyPrimaryKeyId,db);

                List<Level1> stateList=dbhelper.setSpinnerByID("level4_id","Level5",countryList.getId());
                if (stateList.isEmpty()) {
                    Level1 level1 = new Level1(0, 0, "", 0, "Select gramaPanchayath");
                    stateList.add(level1);
                }
                ArrayAdapter<Level1> taArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                taArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                gramaPanchayathspinner.setAdapter(taArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        gramaPanchayathspinner.setSelection(k);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gramaPanchayathspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level6",surveyPrimaryKeyId,db);

                List<Level1> stateList=dbhelper.setSpinnerByID("level5_id","Level6",countryList.getId());
                if (stateList.isEmpty()) {
                    Level1 level1 = new Level1(0, 0, "", 0, "Select Village");
                    stateList.add(level1);
                }
                ArrayAdapter<Level1> taArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                taArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                villagespinner.setAdapter(taArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        villagespinner.setSelection(k);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        villagespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Level1 countryList= (Level1)  adapterView.getSelectedItem();
                int getStateIdFromResponse= DataBaseMapperClass.getStateResponse("level7",surveyPrimaryKeyId,db);

                List<Level1> stateList=dbhelper.setSpinnerByID("level6_id","Level7",countryList.getId());
                if (stateList.isEmpty()) {
                    Level1 level1 = new Level1(0, 0, "", 0, "Select Hamlate");
                    stateList.add(level1);
                }
                ArrayAdapter<Level1> taArrayAdapter = new ArrayAdapter<Level1>(SurveyQuestionActivity.this, R.layout.spinner_multi_row_textview, stateList);
                taArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
                hamletspinner.setAdapter(taArrayAdapter);
                for(int k=0;k<stateList.size();k++){
                    if (stateList.get(k).getId()==getStateIdFromResponse){
                        hamletspinner.setSelection(k);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getAddressQuestionIds= new ArrayList<>();
        getAddressQuestionIds.add(page.getQuestionNumber());
        dynamicSpinnerHashMap.put("level1",spinner);
        dynamicSpinnerHashMap.put("level2",statespinner);
        dynamicSpinnerHashMap.put("level3",districtspinner);
        dynamicSpinnerHashMap.put("level4",talukspinner);
        dynamicSpinnerHashMap.put("level5",gramaPanchayathspinner);
        dynamicSpinnerHashMap.put("level6",villagespinner);
        dynamicSpinnerHashMap.put("level7",hamletspinner);
        dynamicQuestionSet.addView(child);


    }

    /**
     * @param displayQuestionModel Page is the model which has question related info
     * @param questionFontSize
     * @param answerFont
     * @param questionCode
     */
    private void textFieldQuestionDisplay(Page displayQuestionModel, String questionFontSize, String answerFont, int questionCode) {
        HashMap<String, AnswersPage> answerMap1 = getUserAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);   // getting if already answered question
        View child = this.getLayoutInflater().inflate(R.layout.edittext, dynamicQuestionSet, false);//child.xml
        TextView question = child.findViewById(R.id.mainQuestion);
        question.setVisibility(View.GONE);
        question.setTextSize(Integer.valueOf(questionFontSize));
        LinearLayout LL = (LinearLayout) child.findViewById(R.id.relativeLayoutedit);
        TextInputLayout v = (TextInputLayout) child.findViewById(R.id.textInput);
        v.setHint(displayQuestionModel.getQuestion());
        v.setHintTextAppearance(R.style.hintstyle);
        final EditText edittext = (EditText) child.findViewById(R.id.ans_text);
        edittext.setSingleLine(true);
        edittext.setHintTextColor(getResources().getColor(R.color.black));
        question.setFocusable(true);

        if (answerMap1.size() > 0) {
            AnswersPage setAnswer = answerMap1.get(String.valueOf(questionCode));
            edittext.setText(setAnswer.getAnswer());
        }
        allEds.add(edittext);
        TextView errorTextEdit = new TextView(this);
        errorTextEdit.setTextColor(Color.RED);
        hashMap.put(String.valueOf(displayQuestionModel.getQuestionNumber()), edittext);

        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));
        getAllEditTextQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextEdit);
        ValidationUtils.setInputType(edittext, displayQuestionModel.getValidation().split(":"), restUrl);
        LL.addView(errorTextEdit);
        errorTextEdit.setVisibility(View.GONE);
        dynamicQuestionSet.addView(child);
    }

    /**
     * @param displayQuestionModel
     * @param questionTextView
     * @param toolTip
     */
    public void displayQuestionTextTooTip(Page displayQuestionModel, TextView questionTextView, ImageView toolTip) {
        // Set Question with mandatory or not as red start
        if (displayQuestionModel.getMandatory().contains("1"))
            SupportClass.setRedStar(questionTextView, displayQuestionModel.getQuestion());
        else
            questionTextView.setText(displayQuestionModel.getQuestion());
        // Set ToolTip as instruction to the user if exist
        if (!"".equalsIgnoreCase(displayQuestionModel.getToolTip())) {
            toolTip.setVisibility(View.VISIBLE);
            toolTip.setTag(displayQuestionModel.getToolTip());
            toolTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SimpleTooltip.Builder(SurveyQuestionActivity.this)
                            .anchorView(v)
                            .text(v.getTag().toString())
                            .gravity(Gravity.END)
                            .animated(true)
                            .arrowColor(Color.parseColor(TOOLTIP_COLOR))
                            .transparentOverlay(false)
                            .build()
                            .show();
                }
            });
        }

    }

    /**
     * @param displayQuestionModel
     * @param questionFont
     * @param answerFont
     * @param questionCode
     */
    public void dateQuestionDisplay(Page displayQuestionModel, String questionFont, String answerFont, int questionCode) {
        HashMap<String, AnswersPage> getDataAnswer = getUserAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);   // getting if already answered question
        View child = getLayoutInflater().inflate(R.layout.date_time, dynamicQuestionSet, false);//child.xml
        TextView question = child.findViewById(R.id.mainQuestion);
        question.setTextSize(Integer.valueOf(questionFont));
        LinearLayout LLayout = child.findViewById(R.id.linearLayout);
        Button button = child.findViewById(R.id.buttonDate);
        button.setTextColor(Color.WHITE);
        button.setText("Pick Date");

        button.setId(generateIncrementalInteger());
        TextView errorTextDateView = child.findViewById(R.id.errorTextdateview);
        // set Question and tool tip
        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));

        qcode = String.valueOf(displayQuestionModel.getQuestionNumber());
        bt.add(button);
        button.setTag(displayQuestionModel.getValidation());
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setDateView = 1;
                for (Button btn : bt) {
                    if (btn.getId() == v.getId()) {
                        buttonDynamic.put(String.valueOf(qcode), btn);
                    }
                }
                String mathString = v.getTag().toString();
                if (mathString != null && !"".equals(mathString)) {
                    String[] array = mathString.split(":");
                    switch (array[1]) {
                        case "NOV":
                            showDateDialog(dateDialogId, mathString,button);
                            break;
                        case "D":
                            showDateDialog(dateDialogId, mathString,button);
                            break;
                        case "T":
                            String timeFormatZoon = array[2];
                            showTimeDialog(dateDialogId, "ISO".equalsIgnoreCase(timeFormatZoon));
                            break;
                        default:
                            break;
                    }
                } else {
                    setDateView = 1;
                    showDateDialog(dateDialogId, mathString,button);
                }
            }

        });
        if (!getDataAnswer.isEmpty()) {
            button.setText(getDataAnswer.get(String.valueOf(questionCode)).getAnswer());
            button.setTextSize(Integer.valueOf(answerFont));
        }
        errorTextDateView.setTextColor(Color.RED);
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextDateView);
        errorTextDateView.setVisibility(View.GONE);

        hashMapDate.put(String.valueOf(displayQuestionModel.getQuestionNumber()), button);
        getAlldateQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        dynamicQuestionSet.addView(child);

    }

    /**
     * @param displayQuestionModel
     * @param questionFont
     * @param answerFont
     * @param questionCode
     */
    public void radioGroupQuestionDisplay(Page displayQuestionModel, String questionFont, String answerFont, int questionCode) {
        HashMap<String, AnswersPage> getradioQuestionAnswer = getUserAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);
        View child = this.getLayoutInflater().inflate(R.layout.radio, dynamicQuestionSet, false);
        TextView question = (TextView) child.findViewById(R.id.radiomainQuestion);
        question.setTextSize(Integer.valueOf(questionFont));
        question.setFocusable(true);
        LinearLayout LR = (LinearLayout) child.findViewById(R.id.relativeLayoutradio);
        RadioGroup RG =(RadioGroup) child.findViewById(R.id.myRadioGroup);
        RG.setTag(displayQuestionModel.getQuestionNumber());
        HashMap<String, List<AnswersPage>> answerValues = DataBaseMapperClass.getAnswerFromDBnew(displayQuestionModel.getQuestionNumber(), surveyDatabase, restUrl,/*defaultPreferences.getInt(Constants.SELECTEDLANGUAGE,0)*/1);
        List<AnswersPage> answerEditList = answerValues.get(String.valueOf(questionCode));
        // set Question and tool tip
        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));
        Logger.logD(TAG, "RAdio options size " + answerEditList.size());
        for (int j = 0; j < answerEditList.size(); j++) {
            radioButtons[j] = new RadioButton(this);
            radioButtons[j].setTag(answerValues.get(String.valueOf(questionCode)).get(j).getId());
            radioButtons[j].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.answer_text));
            radioButtons[j].setText(answerValues.get(String.valueOf(questionCode)).get(j).getAnswer());
            Logger.logD(TAG, "Radio options values" + answerValues.get(String.valueOf(questionCode)).get(j).getAnswer());
            radioButtons[j].setTextSize(Integer.valueOf(answerFont));
            if (!getradioQuestionAnswer.isEmpty() && answerValues.get(String.valueOf(questionCode)).get(j).getAnswerCode().equals(getradioQuestionAnswer.get(String.valueOf(questionCode)).getAnswerCode())) {
                radioButtons[j].setChecked(true);
            }
            // Set the Radio Button text color from hex string
            radioButtons[j].setTextColor(Color.parseColor("#000000"));
            radioButtons[j].setId(generateIncrementalInteger());
            RG.addView(radioButtons[j]);
        }
        TextView errorTextRadio = new TextView(this);
        errorTextRadio.setTextColor(Color.RED);
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextRadio);
        hashMapRadio.put(String.valueOf(displayQuestionModel.getQuestionNumber()), RG);

        getAllradiobuttonQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        allRadioGroups.add(RG);
        LR.addView(errorTextRadio);
        errorTextRadio.setVisibility(View.GONE);
        dynamicQuestionSet.addView(child);
    }

    /**
     * @param displayQuestionModel
     * @param questionFont
     * @param answerFont
     * @param questionCode
     */
    public void checkBoxQuestionDisplay(Page displayQuestionModel, String questionFont, String answerFont, int questionCode) {
        HashMap<String, List<AnswersPage>> getCheckBoxQuestionAnswer = DataBaseMapperClass.getUserCheckBOxAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);
        View child = this.getLayoutInflater().inflate(R.layout.checkbox, dynamicQuestionSet, false);//child.xml
        TextView question = child.findViewById(R.id.mainQuestioncheck);
        question.setTextSize(Integer.valueOf(questionFont));
        LinearLayout dynamicCheckLinear = child.findViewById(R.id.dynmic_check_box);

        HashMap<String, List<AnswersPage>> answerCheckBOxValues = DataBaseMapperClass.getAnswerFromDBnew(displayQuestionModel.getQuestionNumber(), surveyDatabase, restUrl, defaultPreferences.getInt(Constants.SELECTEDLANGUAGE, 0));
        // set Question and tool tip
        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));

        List<AnswersPage> getCount = answerCheckBOxValues.get(String.valueOf(questionCode));
        List<AnswersPage> getResponseCount = getCheckBoxQuestionAnswer.get(String.valueOf(questionCode));
        for (int p = 0; p < getCount.size(); p++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setTag(answerCheckBOxValues.get(String.valueOf(questionCode)).get(p).getId());
            checkBox.setTextColor(Color.parseColor("#000000"));
            if (getResponseCount != null) {
                String getansweredOptionCode = getResponseCount.get(0).getAnswer();
                String string = getansweredOptionCode.replaceAll("[\\p{Z}\\s]+", "");
                String[] array = string.substring(1, string.length() - 1).split(",");
                Logger.logD(TAG, " the answered check option ids" + array.length);

                for (int h = 0; h < array.length; h++) {
                    if (getCount.get(p).getAnswerCode().equals(array[h])) {
                        checkBox.setChecked(true);
                    }
                }
            }
            checkBox.setText(answerCheckBOxValues.get(String.valueOf(questionCode)).get(p).getAnswer());
            checkBox.setTextSize(Integer.valueOf(answerFont));
            dynamicCheckLinear.addView(checkBox);
        }
        TextView errorTextCheckBox = child.findViewById(R.id.errorTextCheckBox);
        errorTextCheckBox.setTextColor(Color.RED);
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextCheckBox);
        errorTextCheckBox.setVisibility(View.GONE);

        hashMapCheck.put(String.valueOf(displayQuestionModel.getQuestionNumber()), dynamicCheckLinear);
        getAllcheckboxQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        dynamicQuestionSet.addView(child);

    }

    /**
     * @param displayQuestionModel
     * @param questionFont
     * @param answerFont
     * @param questionCode
     */
    public void dropDownQuestionDisplay(Page displayQuestionModel, String questionFont, String answerFont, int questionCode) {
        HashMap<String, AnswersPage> getspinnerQuestionAnswer = getUserAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);   // getting if already answered question
        View child = this.getLayoutInflater().inflate(R.layout.dropdown, dynamicQuestionSet, false);//child.xml
        TextView question = child.findViewById(R.id.mainQuestionspinner);
        question.setTextSize(Integer.valueOf(questionFont));
        question.setPadding(4, 4, 4, 4);
        question.setFocusable(true);
        Spinner spinner = child.findViewById(R.id.spinner);
        spinner.setTag(displayQuestionModel.getQuestionNumber());

        List<AnswersPage> spinnerAnswer = DataBaseMapperClass.getAnswerFromDB(displayQuestionModel.getQuestionNumber(), surveyDatabase, 1/*surveyPreferences.getInt(Constants.SELECTEDLANGUAGE,0)*/);
        // set Question and tool tip
        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));
        // Application of the Array to the Spinner
        ArrayAdapter<AnswersPage> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_multi_row_textview, spinnerAnswer);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        // setting the Pre- entered Answered from the surveyDatabase .
        for (int p = 0; p < spinnerAnswer.size() && !getspinnerQuestionAnswer.isEmpty(); p++) {
            if (spinnerAnswer.get(p).getId() == getspinnerQuestionAnswer.get(String.valueOf(questionCode)).getId()) {
                spinner.setSelection(p);
                break;
            }
        }
        Logger.logD(TAG, "Spinner Value" + spinner.getSelectedItem());
        TextView errorTextDropDownView = child.findViewById(R.id.errorTextdropdownview);
        errorTextDropDownView.setTextColor(Color.RED);
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextDropDownView);
        errorTextDropDownView.setVisibility(View.GONE);

        hashMapDropdown.put(String.valueOf(displayQuestionModel.getQuestionNumber()), spinner);
        getAllspinnerQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        dynamicQuestionSet.addView(child);
    }

    /**
     * @param displayQuestionModel
     * @param questionFont
     * @param answerFont
     * @param questionCode
     */
    public void imageQuestionDisplay(Page displayQuestionModel, String questionFont, String answerFont, int questionCode) {
        if (!PermissionClass.checkPermission(this)) {
            PermissionClass.requestPermission(this);
        }
        HashMap<String, AnswersPage> getImageDataAnswer = getUserAnsweredResponseFromDB(displayQuestionModel.getQuestionNumber(), db, surveyPrimaryKeyId, restUrl);   // getting if already answered question
        View child = getLayoutInflater().inflate(R.layout.image_upload, dynamicQuestionSet, false);//child.xml
        TextView question = child.findViewById(R.id.mainQuestion);
        question.setTextSize(Integer.valueOf(questionFont));
        question.setFocusable(true);
        // set Question and tool tip
        displayQuestionTextTooTip(displayQuestionModel, question, child.findViewById(R.id.tooltip));

        imaimageUpload = child.findViewById(R.id.img_upload);
        imaimageUpload.setOnClickListener(this);
        if (getImageDataAnswer.size() > 0) {
            AnswersPage getImageAnswer = getImageDataAnswer.get(String.valueOf(questionCode));
            pathOfImage = getImageAnswer.getAnswer();
            File imageFile = new File(getImageAnswer.getAnswer());
            Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            if (null != image)
                imaimageUpload.setImageBitmap(image);
        }
        TextView errorTextImageView = child.findViewById(R.id.errorTextImageview);
        errorTextImageView.setTextColor(Color.RED);
        hashMapTextError.put(String.valueOf(displayQuestionModel.getQuestionNumber()), errorTextImageView);
        errorTextImageView.setVisibility(View.GONE);
        getAllImageuploadQuestionCode.add(String.valueOf(displayQuestionModel.getQuestionNumber()));
        dynamicQuestionSet.addView(child);
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private boolean performValidation(View v) {
        layoutCont = v.findViewById(R.id.dynamicQuestionSet);
        List<String> checkValue = getAllChildElements(layoutCont);
        return !checkValue.contains("false");
    }

    /**
     * module to take each child from the parent view and validation
     *
     * @param layoutCont
     * @return
     */
    public final List<String> getAllChildElements(LinearLayout layoutCont) {

        list = new ArrayList<>();
        final int mCount = layoutCont.getChildCount();
        Logger.logV(TAG, "Linear dynamic child Count" + mCount);
        // Loop through all of the children
        try {
            for (int i = 0; i < mCount; i++) {
                View mChild;
                switch (Integer.parseInt(listQuestionType.get(i))) {
                    /*
                     * 1 represent Edittext
                     */
                    case 1:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int questionCode = Integer.parseInt(getAllEditTextQuestionCode.get(editcount));
                            boolean edit = edittextFunctionality(questionCode, surveyDatabase, 1);
                            list.add(String.valueOf(edit));
                            if (edit) {
                                editcount++;
                            }
                        }
                        break;
                    /*
                      4 represent RadioButton
                     */
                    case 4:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int radioQuestionCode = Integer.parseInt(getAllradiobuttonQuestionCode.get(radiocount));
                            boolean radioBoolean = radioFunctionality(radioQuestionCode, surveyDatabase, 4);
                            list.add(String.valueOf(radioBoolean));
                            if (list.get(i).contains("true")) {
                                radiocount++;
                            }
                        }
                        break;
                    /*
                      2 represent CheckBox
                     */
                    case 2:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int checkBoxQuestionCode = Integer.parseInt(getAllcheckboxQuestionCode.get(checkCount));
                            boolean checkBoolean = checkFunctionality(checkBoxQuestionCode, surveyDatabase, 2);
                            list.add(String.valueOf(checkBoolean));
                            if (list.contains("true")) {
                                checkCount++;
                            }
                        }
                        break;
                    /*
                      6 represent DropDown
                     */
                    case 6:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int spinnerQuestionCode = Integer.parseInt(getAllspinnerQuestionCode.get(spinnerCount));
                            boolean spinBoolean = spinnerFunctionality(spinnerQuestionCode, surveyDatabase, 6);
                            list.add(String.valueOf(spinBoolean));
                            if (list.contains("true")) {
                                spinnerCount++;
                            }
                        }
                        break;
                    /*
                      5 represent Date
                     */
                    case 5:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int dateQuestionCode = Integer.parseInt(getAlldateQuestionCode.get(dateCount));
                            boolean dateBoolean = dateandtimeFunctionality(dateQuestionCode, surveyDatabase, 5);
                            list.add(String.valueOf(dateBoolean));
                            if (list.contains("true")) {
                                dateCount++;
                            }
                        }
                        break;
                    /*
                      8 represent Image
                     */
                    case 8:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int imageUploadQuestionCode = Integer.parseInt(getAllImageuploadQuestionCode.get(imageUploadCount));
                            boolean imageBoolean = ImageUploadFunctionality(imageUploadQuestionCode, surveyDatabase, 8);
                            list.add(String.valueOf(imageBoolean));
                            if (list.contains("true")) {
                                imageUploadCount++;
                            }
                        }
                        break;
                    /*
                      8 represent the Address widget newly added for beneficiary collection.
                     */
                    case 9:
                        list.add(String.valueOf(true));
                        Logger.logD("Validation part","Storing all the spinner values to survey table");
                        fillAllLevelToDatabase();

                        break;
                    case 10:
                        if (!getParentsBeneficiary.equals("")) {
                            list.add(String.valueOf(true));
                            fillAIResponseToDB(getBenificiaryQids.get(0), getParentsBeneficiary, 10,getParentsBeneficiaryName);
                        }
                        else
                            list.add(String.valueOf(true));
                        break;

                    case 14:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            int gridviewQuestionCOde = Integer.parseInt(getAllGridQuestionCode.get(GridCount));
                            Logger.logD(TAG, " the Count is gridViewLinearLayoutHolder ->" + gridViewLinearLayoutHolder.size());
                            LinearLayout ll = gridViewLinearLayoutHolder.get(String.valueOf(gridviewQuestionCOde));
                            boolean tempFlag = false;
                            for (int gridViewCount = 0; gridViewCount < ll.getChildCount(); gridViewCount++) {
                                View ViewContainer = (View) ll.getChildAt(gridViewCount);
                                TextView setErrorMessage = (TextView) ViewContainer.findViewById(R.id.errorText);
                                Button getButtonText = (Button) ViewContainer.findViewById(R.id.addEdit);
                                if (getButtonText.getText().equals("Add")) {
                                    setErrorMessage.setText("Mandatory field");
                                    setErrorMessage.startAnimation(animShake);
                                    tempFlag = false;
                                } else {
                                    setErrorMessage.setText("");
                                    tempFlag = true;
                                }
                            }
                            if (tempFlag){
                                //       if (true){
                                boolean fileBoolean = FunctionalityCodeStoreGRid(gridviewQuestionCOde);
                                //     list.add(String.valueOf(true));
                                list.add(String.valueOf(fileBoolean));
                            } else {
                                list.add(String.valueOf(false));
                            }
                        }
                        break;
                    /**
                     * represent Inline Grid
                     */
                    case 16:
                        mChild = layoutCont.getChildAt(0);
                        if (mChild instanceof ViewGroup) {
                            JSONObject obj4 = new JSONObject();
                            int GridviewQuestionCOde = Integer.parseInt(getAllGridQuestionCodeInline.get(gridCountInline));
                            Logger.logD("gridinlineQuestionCode", GridviewQuestionCOde + "");
                            boolean fileBoolean = gridNewInlineFunctionality(GridviewQuestionCOde);
                            list.add(String.valueOf(fileBoolean));
                            if (fileBoolean) {
                                gridCountInline++;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            if (!list.contains("false")) {
                setFlagCount();
            }

            if (list.contains(Constants.FALSE)) {
                setFlagCount();
            }
            Logger.logD("List Values", "List Values and Sizes " + list.toString());
        } catch (Exception e) {
            Log.e("exception", "Exception In Getting View    ", e);
            restUrl.writeToTextFile("Exception In Getting View ", "", "dynamicallySettingView");
        }
        return list;
    }

    private boolean gridNewInlineFunctionality(int gridviewQuestionCOde) {
        final List<String> getResponseKeys = new ArrayList<>();
        Logger.logD(TAG, "the grid QuestionID" + gridviewQuestionCOde);
        if (fillInlineRow.size() > 0) {

           return validateAndUpdateHashMap(getResponseKeys,gridviewQuestionCOde);

        } else {
            boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(gridviewQuestionCOde), surveyDatabase);
            if (checkMandatory) {
                return false;
            } else {
                return true;
            }

        }
    }

    private boolean validateAndUpdateHashMap(List<String> getResponseKeys, int gridviewQuestionCOde) {
        try {
            List<String> getAllKeys = fillInlineHashMapKey.get(String.valueOf(gridviewQuestionCOde));
            for (int i = 0; i < getAllKeys.size(); i++) {
                String[] s = getAllKeys.get(i).split("_");
                if (Integer.valueOf(s[0]) == gridviewQuestionCOde) {
                    getResponseKeys.add(getAllKeys.get(i));
                }
            }

            List<Response> answersEditTextTemp = new ArrayList<>();
            for (int j = 0; j < getResponseKeys.size(); j++) {
                List<Response> getResponseListFrmHashmap = fillInlineRow.get(getResponseKeys.get(j));

                for (int k = 0; k < getResponseListFrmHashmap.size(); k++) {
                    answersEditTextTemp.add(getResponseListFrmHashmap.get(k));
                }

            }
            hashMapAnswersEditText.put(String.valueOf(gridviewQuestionCOde + "_" + 16), answersEditTextTemp);
            Logger.logD(TAG, "Responsed filled to Response Table-> " + answersEditTextTemp.toString());
            return true;
        } catch (Exception e) {
            Logger.logE(TAG, "Exception", e);
            return false;
        }
    }

    private boolean FunctionalityCodeStoreGRid(int gridviewQuestionCOde) {
        final List<String> getResponseKeys = new ArrayList<>();
        Logger.logD(TAG, "the grid QuestionID" + gridviewQuestionCOde);
        if (GridResponseHashMap.size() > 0) {
            List<String> getAllKeys = GridResponseHashMapKeys.get(String.valueOf(gridviewQuestionCOde));
            Logger.logD(TAG, "the  all key count" + getAllKeys.size());
            for (int i = 0; i < getAllKeys.size(); i++) {
                String[] s = getAllKeys.get(i).split("_");
                if (Integer.valueOf(s[0]) == gridviewQuestionCOde) {
                    getResponseKeys.add(getAllKeys.get(i));
                    Logger.logD(TAG, "the only key for this QuestioID " + getResponseKeys.toString());
                }
            }
            List<Response> answersEditTextTemp = new ArrayList<>();
            for (int j = 0; j < getResponseKeys.size(); j++) {
                List<Response> getResponseListFrmHashmap = GridResponseHashMap.get(getResponseKeys.get(j));
                for (int k = 0; k < getResponseListFrmHashmap.size(); k++) {
                    answersEditTextTemp.add(getResponseListFrmHashmap.get(k));
                    hashMapAnswersEditText.put(String.valueOf(gridviewQuestionCOde + "_" + String.valueOf(14)), answersEditTextTemp);
                }


                Logger.logD(TAG, "Responsed filled to Response Table-> " + answersEditTextTemp.toString());

            }
            return true;
        } else {
            return false;
        }

    }

    private void fillAllLevelToDatabase() {
        try {
            int getUpdatedResult= surveyHandler.updateAddressRecordToSurveyTable(surveyPrimaryKeyId,dynamicSpinnerHashMap);
            Spinner getLevel7Spinner = dynamicSpinnerHashMap.get("level7");
            Level1 getLevel7Id = (Level1) getLevel7Spinner.getSelectedItem();
            fillResponseToDB(getAddressQuestionIds.get(0), String.valueOf(getLevel7Id.getName()), 9);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Clearing all question counts under each type
     */
    private void setFlagCount() {
        editcount = 0;
        radiocount = 0;
        checkCount = 0;
        spinnerCount = 0;
        dateCount = 0;
        imageUploadCount = 0;
    }

    /**
     * Clearing all maps and list
     */
    private void clearAllWidgetMapCounts() {
        setFlagCount();
        hashMapDropdown.clear();
        getAllspinnerQuestionCode.clear();
        getAllEditTextQuestionCode.clear();
        getAllradiobuttonQuestionCode.clear();
        getAllcheckboxQuestionCode.clear();
        getAlldateQuestionCode.clear();
        getAllImageuploadQuestionCode.clear();
        getAllGridQuestionCode.clear();
        buttonDynamic.clear();
        bt.clear();
        list.clear();
        GridResponseHashMap.clear();
        rowInflater = 0;
        GridCount=0;
        gridCountInline = 0;
        editcount = 0;
        getAllGridQuestionCodeInline.clear();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListenerGrid = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (view != null) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateGridDateView(view);
            }
        }
    };
    private void updateGridDateView(DatePicker view) {
        Date date = new Date();
        String UserFormate = String.valueOf((new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" ")));
        SimpleDateFormat sdf = new SimpleDateFormat(UserFormate, Locale.ENGLISH);
        String data = sdf.format(date);
        try {
            Button dynamicButton = buttonDynamicDateGrid.get("1");
            dynamicButton.setText(data);
        } catch (Exception e) {
            ToastUtils.displayToast("Button view not found Exception", SurveyQuestionActivity.this);
            Logger.logE(TAG, "Exception", e);
        }

    }


    /**
     * @param dateDialogId
     */
    public void showDateDialog(int dateDialogId, String arrayString, Button button) {
        if (dateDialogId == DATE_DIALOG_ID) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            Date date = new Date();

            String dt = sdf.format(date);

            try {
                Logger.logD("mathString", "mathString " + arrayString);
                if (arrayString != null && !"".equals(arrayString)) {
                    String[] array = arrayString.split("#");
                    String[] array1 = array[0].split(":");
                    if (("R".equalsIgnoreCase(array1[0]) || "o".equalsIgnoreCase(array1[0]))) {
                        if (array.length > 1) {
                            String[] array2 = array[1].split(":");
                            String prev = DBHandler.getAnswerFromPreviousQuestion(array2[1], surveyHandler, defaultPreferences.getString(PreferenceConstants.SURVEY_ID, ""));
                            Date date1 = sdf.parse(prev);
                            dpd.getDatePicker().setMinDate(date1.getTime());
                        }
                        if (array1[3].equalsIgnoreCase("00000000")) {
                            /*displaying all previous Dates*/
                            String getINTODateformate = CommonForAllClasses.getINTODateformate(array1[4]);
                            Date currentMax = sdf.parse(getINTODateformate);
                            dpd.getDatePicker().setMinDate(currentMax.getTime());
                        } else if (array1[4].equalsIgnoreCase("00000000")) {
                            /*displaying all future Dates*/
                            String getINTODateformate = CommonForAllClasses.getINTODateformate(array1[3]);
                            Date currentMin = sdf.parse(getINTODateformate);
                            Logger.logV("currentMin----> ", currentMin.toString());

                            /*dpd.getDatePicker().setMaxDate(currentMin.getTime());*/
                            dpd.getDatePicker().setMinDate(currentMin.getTime());
                        } else {
                            /*displaying based on Min and Max*/
                            String minValue = array1[3];
                            String getINTODateformate = CommonForAllClasses.getINTODateformate(minValue);

                            Date minDate = sdf.parse(getINTODateformate);
                            Logger.logD("Equation", "Equation for LMD " + array1[3]);
                            Date maxDate = sdf.parse(array1[4]);
                            dpd.getDatePicker().setMinDate(minDate.getTime());
                            dpd.getDatePicker().setMaxDate(maxDate.getTime());
                        }
                    }
                }
                dpd.show();
            } catch (ParseException e) {
                Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in TimeAndDateFragment class  edtLocaleName onclicklistener ", e);
            }
        } else if (dateDialogId == 2) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListenerGrid, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            Date date = new Date();

            String dt = sdf.format(date);

            try {
                Logger.logD("mathString", "mathString " + arrayString);
                if (arrayString != null && !"".equals(arrayString)) {
                    String[] array = arrayString.split("#");
                    String[] array1 = array[0].split(":");
                    if (("R".equalsIgnoreCase(array1[0]) || "o".equalsIgnoreCase(array1[0]))) {
                        if (array.length > 1) {
                            String[] array2 = array[1].split(":");
                            String prev = DBHandler.getAnswerFromPreviousQuestion(array2[1], surveyHandler, defaultPreferences.getString(PreferenceConstants.SURVEY_ID, ""));
                            Date date1 = sdf.parse(prev);
                            dpd.getDatePicker().setMinDate(date1.getTime());
                        } else if (array1[3].equalsIgnoreCase("01011900") && array1[4].equalsIgnoreCase("00000000")) {
                            dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                        } else if (array1[3].equalsIgnoreCase("00-00-0000")) {
                            /*displaying all previous Dates*/
                            Date currentMax = sdf.parse(array1[4]);
                            dpd.getDatePicker().setMinDate(currentMax.getTime());
                        } else if (array1[4].equalsIgnoreCase("00-00-0000")) {
                            /*displaying all future Dates*/
                            Date currentMin = sdf.parse(array1[3]);
                            /*dpd.getDatePicker().setMaxDate(currentMin.getTime());*/
                            dpd.getDatePicker().setMaxDate(currentMin.getTime());
                        } else {
                            /*displaying based on Min and Max*/
                            String minValue = array1[3];
                            Date minDate = sdf.parse(minValue);
                            Logger.logD("Equation", "Equation for LMD " + array1[3]);
                            Date maxDate = sdf.parse(array1[4]);
                            dpd.getDatePicker().setMinDate(minDate.getTime());
                            dpd.getDatePicker().setMaxDate(maxDate.getTime());
                        }
                    }
                } else {
                    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                }
                dpd.show();
            } catch (ParseException e) {
                Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in TimeAndDateFragment class  edtLocaleName onclicklistener ", e);
            }

        }

    }


    /**
     * @param arrayString
     */
    private void methodToShowDatePicker(String arrayString) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        try {
            Logger.logD("mathString", "mathString " + arrayString);
            if (arrayString != null && !"".equals(arrayString)) {
                String[] array = arrayString.split("#");
                String[] array1 = array[0].split(":");
                if (("R".equalsIgnoreCase(array1[0]) || "o".equalsIgnoreCase(array1[0]))) {
                    if (("00000000").equalsIgnoreCase(array1[3])) {
                            /*displaying all previous Dates*/
                        String futureVal = Utils.getINTODateformate(array1[4]);
                        Date currentMax = sdf.parse(futureVal);
                        dpd.getDatePicker().setMinDate(currentMax.getTime());
                    } else if (("00000000").equalsIgnoreCase(array1[4])) {
                            /*displaying all future Dates*/
                        String futureVal = Utils.getINTODateformate(array1[3]);
                        Date currentMin = sdf.parse(futureVal);
                        dpd.getDatePicker().setMinDate(currentMin.getTime());
                        dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    } else {
                            /*displaying based on Min and Max*/
                        String minVal = Utils.getINTODateformate(array1[3]);
                        String maxVal = Utils.getINTODateformate(array1[4]);
                        Date minDate = sdf.parse(minVal);
                        Logger.logD("Equation", "Equation for LMD " + array1[3]);
                        Date maxDate = sdf.parse(maxVal);
                        dpd.getDatePicker().setMinDate(minDate.getTime());
                        dpd.getDatePicker().setMaxDate(maxDate.getTime());
                    }
                }
            }
            dpd.show();
        } catch (Exception e) {
            Logger.logE(SurveyQuestionActivity.class.getSimpleName(), "Exception in TimeAndDateFragment class  edtLocaleName onclicklistener ", e);
            restUrl.writeToTextFile("Exception on Displaying the date picker", "", "ondisplayDatepicker");
        }
    }

    /**
     * @param spinnerQuestionCode
     * @param database
     * @param qType
     * @return
     */
    private boolean spinnerFunctionality(final int spinnerQuestionCode, final SQLiteDatabase database, final int qType) {
        Spinner spinnerValues = hashMapDropdown.get(String.valueOf(spinnerQuestionCode));
        boolean spinnerBoolean = false;
        TextView errorText = hashMapTextError.get(String.valueOf(spinnerQuestionCode));
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(spinnerQuestionCode), database);
        Logger.logD("CheckMandatory Spinner", "CheckMandatory Spinner" + checkMandatory);
        AnswersPage getSpinnerBean = (AnswersPage) spinnerValues.getSelectedItem();
        if (checkMandatory) {
            errorText.setVisibility(View.GONE);
            spinnerValues.requestFocus();
            Logger.logD(TAG, "Spinner Value" + spinnerValues.getSelectedItem().toString());
            if ((Constants.SELECT).equalsIgnoreCase(spinnerValues.getSelectedItem().toString())) {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Please select");
                errorText.startAnimation(animShake);
                errorText.setFocusableInTouchMode(true);
                spinnerBoolean = false;
            } else {
                errorText.setVisibility(View.GONE);
                spinnerBoolean = true;
                radioAnswerCode = String.valueOf(getSpinnerBean.getId());
                fillResponseToDBNONwidget(spinnerQuestionCode, getSpinnerBean.getAnswer(), String.valueOf(getSpinnerBean.getId()), qType); // filling Question and Answer to Been and Table
                skipcode = QuestionActivityUtils.checkSkipCode(String.valueOf(spinnerQuestionCode), database, radioAnswerCode);
            }

        } else {
            spinnerBoolean = true;
            errorText.setVisibility(View.GONE);
            String getAnswercode = DataBaseMapperClass.getAnswerCode(String.valueOf(spinnerQuestionCode), database, getSpinnerBean.getAnswer(), 1);
            radioAnswerCode = getAnswercode;
            fillResponseToDBNONwidget(spinnerQuestionCode, getSpinnerBean.getAnswer(), getAnswercode, qType); // filling Question and Answer to Been and Table

        }
        return spinnerBoolean;

    }

    /**
     * @param dateTimeQuestionCode
     * @param database
     * @param qType
     * @return
     */
    private boolean dateandtimeFunctionality(int dateTimeQuestionCode, SQLiteDatabase database, int qType) {
        Button button = hashMapDate.get(String.valueOf(dateTimeQuestionCode));
        TextView errorText = hashMapTextError.get(String.valueOf(dateTimeQuestionCode));
        boolean dateBoolean = false;
        List<AnswersPage> answersPageList = DataBaseMapperClass.getAnswersForQuestionFromDB(String.valueOf(dateTimeQuestionCode), this.surveyDatabase, restUrl);
        answerspage = answersPageList.get(0);
        String mathString = DataBaseMapperClass.getValidationExpressionFromQuestion(dateTimeQuestionCode, database);
          /*array containing main equation*/
            /*array1 containing first equation which is splitted from main equation*/
        String text = button.getText().toString();
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(dateTimeQuestionCode), database);
        if (checkMandatory) {
            if (mathString.length() > 1) {
                if (!"".equals(text)) {
                    if (ValidationUtils.dateInnerValidation(text, mathString, surveyHandler, errorText)) {
                        dateBoolean = true;
                        errorText.setVisibility(View.GONE);
                        fillResponseToDB(dateTimeQuestionCode, text, qType); // filling Question and Answer to Been and Table
                    } else {
                        dateBoolean = false;
                    }
                } else {
                    errorText.setFocusable(true);
                    errorText.requestFocus();
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(R.string.mandatoryQuestion);
                    errorText.startAnimation(animShake);
                    errorText.setFocusableInTouchMode(true);
                    dateBoolean = false;
                }
            } else {
                if (!"".equals(text)) {
                    dateBoolean = true;
                    errorText.setVisibility(View.GONE);
                    fillResponseToDB(dateTimeQuestionCode, text, qType); // filling Question and Answer to Been and Table
                } else {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(R.string.mandatoryQuestion);
                    errorText.startAnimation(animShake);
                    dateBoolean = false;
                    errorText.setFocusable(true);
                    errorText.requestFocus();
                    errorText.setFocusableInTouchMode(true);
                }
            }
        } else {
            dateBoolean = true;
            fillResponseToDB(dateTimeQuestionCode, text, qType); // filling Question and Answer to Been and Table
        }
        return dateBoolean;

    }

    /**
     * @param imageQuestionCode
     * @param database
     * @param qType
     * @return
     */
    private boolean ImageUploadFunctionality(int imageQuestionCode, SQLiteDatabase database, int qType) {
        String getimageAnswercode = DataBaseMapperClass.getImageAnswerCode(String.valueOf(imageQuestionCode), database, "");
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(imageQuestionCode), database);
        boolean imageBoolean = false;
        TextView errorTextImageview = hashMapTextError.get(String.valueOf(imageQuestionCode));
        if (checkMandatory) {
            if (!"".equals(pathOfImage)) {
                imageBoolean = true;
                fillResponseToDBNONwidget(imageQuestionCode, pathOfImage, getimageAnswercode, qType); // filling Question and Answer to Been and Table
            } else {
                errorTextImageview.setVisibility(View.VISIBLE);
                errorTextImageview.setText(R.string.mandatoryQuestion);
                errorTextImageview.startAnimation(animShake);
                imageBoolean = false;
            }
        } else {
            imageBoolean = true;
            fillResponseToDBNONwidget(imageQuestionCode, pathOfImage, getimageAnswercode, qType); // filling Question and Answer to Been and Table
        }
        return imageBoolean;
    }

    /**
     * @param checkBoxQuestionCode
     * @param database
     * @param qType
     * @return
     */
    private boolean checkFunctionality(int checkBoxQuestionCode, SQLiteDatabase database, int qType) {
        LinearLayout linearLayout = hashMapCheck.get(String.valueOf(checkBoxQuestionCode));
        String checked = "";
        int checkcounter = 0;
        boolean checkBoolean = false;
        String getAnswercode = "";
        TextView errorTextCheckBox = hashMapTextError.get(String.valueOf(checkBoxQuestionCode));
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(checkBoxQuestionCode), database);
        if (checkMandatory) {
            List<String> getALlSelectedids = new ArrayList<>();
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                CheckBox checkbox = (CheckBox) linearLayout.getChildAt(i);
                if (checkbox.isChecked()) {
                    checkcounter = checkcounter + 1;
                    checkBoolean = true;
                    checked = checkbox.getText().toString();
                    getAnswercode = DataBaseMapperClass.getAnswerCode(String.valueOf(checkBoxQuestionCode), database, checked, 1);
                    getALlSelectedids.add(getAnswercode);
                }

            }
            if (getALlSelectedids.size() > 0)
                fillResponseToDBNONwidget(checkBoxQuestionCode, getALlSelectedids.toString(), getAnswercode, qType); // filling Question and Answer to Been and Table
            if (checkcounter == 0) {
                linearLayout.requestFocus();
                linearLayout.setFocusable(true);
                linearLayout.setFocusableInTouchMode(true);
                errorTextCheckBox.setVisibility(View.VISIBLE);
                errorTextCheckBox.setText(R.string.mandatoryQuestion);
                errorTextCheckBox.startAnimation(animShake);
                checkBoolean = false;
            }
        } else {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                CheckBox checkbox = (CheckBox) linearLayout.getChildAt(i);
                if (checkbox.isChecked()) {
                    checkBoolean = true;
                    checked = checkbox.getText().toString();
                    getAnswercode = DataBaseMapperClass.getAnswerCode(String.valueOf(checkBoxQuestionCode), database, checked, 1);
                    fillResponseToDBNONwidget(checkBoxQuestionCode, checked, getAnswercode, qType); // filling Question and Answer to Been and Table
                    Log.v(TAG, "radio button checked " + checkbox.getText().toString());
                } else {
                    checkBoolean = true;
                }
            }
        }
        return checkBoolean;
    }

    /**
     * @param questionCode
     * @param database
     * @param qType
     * @return
     */
    private boolean radioFunctionality(final int questionCode, final SQLiteDatabase database, final int qType) {
        RadioGroup[] radioGroup = new RadioGroup[100];
        radioGroup[0] = hashMapRadio.get(String.valueOf(questionCode));
        TextView errorTextView = hashMapTextError.get(String.valueOf(questionCode));
        String checkedItemData = "";
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(questionCode), database);
        boolean radioBoolean = false;
        for (int i = 0; i < radioGroup[0].getChildCount(); i++) {
            final RadioButton button = (RadioButton) radioGroup[0].getChildAt(i);
            if (button.isChecked()) {
                radioBoolean = true;
                errorTextView.setVisibility(View.GONE);
                checkedItemData = button.getText().toString();
                String getAnswercode = DataBaseMapperClass.getAnswerCode(String.valueOf(questionCode), database, checkedItemData, 1);
                radioAnswerCode = getAnswercode;
                fillResponseToDBNONwidget(questionCode, checkedItemData, getAnswercode, qType); // filling Question and Answer to Been and Table
                skipcode = QuestionActivityUtils.checkSkipCode(String.valueOf(questionCode), database, radioAnswerCode);
            }
        }
        if (checkMandatory && radioGroup[0].getCheckedRadioButtonId() == -1) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(R.string.mandatoryQuestion);
            errorTextView.startAnimation(animShake);
            errorTextView.setFocusable(true);
            errorTextView.setFocusableInTouchMode(true);
            errorTextView.requestFocus();
            return false;
        }
        if (!checkMandatory && radioGroup[0].getCheckedRadioButtonId() == -1)
            radioBoolean = true;
        return radioBoolean;
    }


    /**
     * @param i
     * @param database
     * @param qType
     * @return
     */
    public boolean edittextFunctionality(int i, SQLiteDatabase database, int qType) {
        EditText editText = hashMap.get(String.valueOf(i));
        TextView errorText = hashMapTextError.get(String.valueOf(i));
        boolean editBoolean = false;
        List<AnswersPage> answersPageList = DataBaseMapperClass.getAnswersForQuestionFromDB(String.valueOf(i), this.surveyDatabase, restUrl);
        final Animation animShakeObj = AnimationUtils.loadAnimation(this, R.anim.shake);
        answerspage = answersPageList.get(0);
        String mathString = DataBaseMapperClass.getValidationExpressionFromQuestion(i, database);
        Logger.logV(TAG, "String validation" + mathString);
        String[] arrayValidation = mathString.split(":");
        String ansText = editText.getText().toString();
        boolean checkMandatory = DBHandler.getMandatoryQuestion(String.valueOf(i), database);
        Logger.logD(TAG, "EditTextMandatory" + checkMandatory);
        String getConstraints= prefs.getString(Constants.constraints,"");
        String[] tempDelete=getConstraints.split(",");
        for (int q=0;q<tempDelete.length;q++){
            if (!getConstraints.equals("") && Integer.parseInt(tempDelete[q])==i) {
                if(!surveyHandler.isConstraintValueExist(ansText,i)) {
                    errorText.setVisibility(View.GONE);
                }else{
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Record already exist ");
                    errorText.startAnimation(animShakeObj);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.setEnabled(true);
                    return false;
                }

            }
        }


        if (!checkMandatory) {
            errorText.setVisibility(View.GONE);
            fillResponseToDB(i, ansText, qType);
            return true;
        }

        if (mathString.length() > 1) {
            if (!"".equals(ansText)) {
                if (ValidationUtils.performNextValidation(arrayValidation, ansText)) {
                    errorText.setVisibility(View.GONE);
                    fillResponseToDB(i, ansText, qType);
                    editBoolean = true;
                } else if (saveToDraftFlag) {
                    errorText.setVisibility(View.GONE);
                    fillResponseToDB(i, ansText, qType);
                } else {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(arrayValidation[4]);
                    errorText.startAnimation(animShakeObj);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.setEnabled(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText(R.string.mandatoryQuestion);
                errorText.startAnimation(animShakeObj);
                editText.setFocusableInTouchMode(true);
                editText.setEnabled(true);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
            return editBoolean;
        }
        if ("".equals(ansText)) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(R.string.mandatoryQuestion);
            errorText.startAnimation(animShakeObj);
            editText.requestFocus();
            editText.setFocusableInTouchMode(true);
            editText.setEnabled(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            return false;
        } else {
            errorText.setVisibility(View.GONE);
            fillResponseToDB(i, ansText, qType);
            return true;
        }
    }

    private boolean isconstraintsFunctionality(String userEnteredText, int questionCode) {
      String getConstraints= prefs.getString(Constants.constraints,"");
      if (!getConstraints.equals("") && Integer.parseInt(getConstraints)==questionCode) {
          return surveyHandler.isConstraintValueExist(userEnteredText,questionCode);
      }else{
          return true;
      }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                Logger.logD(TAG, "Clicked next button");
                nextButtonClicked();
                break;
            case R.id.button1:
                Logger.logD(TAG, "Clicked previous button");
                previousButtonClicked();
                break;
            case R.id.img_upload:
                handleChangeProfilePic();
                break;
            case R.id.backPress:
                Logger.logD(TAG, "Clicked backPress button");
                clearAllWidgetMapCounts();
                if (surveyPreferences.getString(Constants.SURVEYSTATUSTYPR,"").equals("new")){
                    Logger.logD(TAG,"New Survey");
                    SupportClass supportClass = new SupportClass();
                    supportClass.backButtonFunction(SurveyQuestionActivity.this, db, surveyHandler, surveyPrimaryKeyId);
                }else{
                    Logger.logD(TAG,"Edit Survey");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.exitSurvey).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }
                break;
            case R.id.imageMenu:
                Logger.logD(TAG, "Clicked saveDraft button");
                saveAsDraft();
                break;
            default:
                break;
        }
    }

    /**
     * method
     */
    private void saveAsDraft() {

        answeredList.clear();
        hashMapAnswersEditText.clear();
        saveToDraftFlag = true;
        Boolean checkValidation = performValidation(dynamicQuestionSet);
        Logger.logV(TAG, "hashMapAnswersEditText" + checkValidation.toString() + hashMapAnswersEditText.size());
        for (String key : hashMapAnswersEditText.keySet()) {
            List<Response> value = hashMapAnswersEditText.get(key);
            if (value.size() > 1) {
                for (int getInnerResponse = 0; getInnerResponse < value.size(); getInnerResponse++) {
                    answersEditText.add(value.get(getInnerResponse));
                }
            } else {
                //execute to the normal Question of all the widget (Not grid or inline)
                answersEditText.add(value.get(0));
            }
        }
        for (int delete = 0; delete < answersEditText.size(); delete++) {
            Logger.logV(TAG, "AnswerCode Value" + answersEditText.get(delete).getQ_id());
            DataBaseMapperClass.deletePreviousSetOfQuestion(answersEditText.get(delete).getQ_id(), db, surveyPrimaryKeyId);
        }
        InsertTask insertTask = new InsertTask();
        Boolean booleanValue = insertTask.insertTask(answersEditText, surveyHandler, surveyPrimaryKeyId);
        Logger.logD(TAG, booleanValue.toString());
        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);
        Utilities.setLocationSurveyFlag(defaultPreferences,"clear");
        finish();
    }

    /**
     * method
     */
    private void nextButtonClicked() {
        try {
            List<String> questionList;
            List<String> displayQidsList = new ArrayList<>();

            Logger.logD(TAG, "1. questionDisplayPageCount (page 1 or not)" + questionDisplayPageCount);
            // questionDisplayPageCount is 1 means its first
            if (questionDisplayPageCount == 1) {
                previousButton.setVisibility(View.GONE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
            }

            Logger.logD(TAG, "2. questionDisplayPageCount (page -1 or not)" + questionDisplayPageCount);
            // questionDisplayPageCount is -1 means get the first page questions list
            if (questionDisplayPageCount == -1)
                questionList = multiMapOdd.get(String.valueOf(1));
            else
                questionList = multiMapOdd.get(String.valueOf(questionDisplayPageCount));
            if (questionList.isEmpty()) {
                ToastUtils.displayToast("Facing difficulties try again please...", getApplicationContext());
                return;
            }
            Logger.logD(TAG, "3. clearing answersEditText list<Response>, hashMapAnswersEditText");
            // clearing all previously saved answers

            //Modify by guru
            answersEditText.clear();
            hashMapAnswersEditText.clear();
            // making skip code variable to default
            skipcode = "";

            loginDialog = ProgressUtils.showProgress(SurveyQuestionActivity.this, false, "Please wait " + "...");
            loginDialog.show();

            boolean checkValidation = performValidation(dynamicQuestionSet);
            ProgressUtils.CancelProgress(loginDialog);
            Logger.logV(TAG, "checkValidation" + checkValidation);
            if (!checkValidation)
                return;

            if (statusFlag == 1) {
                nextB.setVisibility(View.GONE);
                previousButton.setVisibility(View.GONE);
                setAllDataToResponse();
                ToastUtils.displayToast("Please wait..", getApplicationContext());
                SharedPreferences.Editor editorSaveDraft = surveyPreferences.edit();
                editorSaveDraft.putString("recentPreviewRecord", "");
                editorSaveDraft.apply();
              /** PreviewPopUp pre = new PreviewPopUp();
                pre.showPreviewPopUp(SurveyQuestionActivity.this, surveyPrimaryKeyId, dbOpenHelper, nextB, previousButton, surveysId);
                Modified by Guru */
                callPopUpActivity();
                return;
            }
            Logger.logV(TAG, "last Answered Qid id from Mainlist" + lastIndexUsedToFetchQID);
            if (!questionList.isEmpty()) {
                String currentPageLastQuestionId = questionList.get(questionList.size() - 1);
                String answerType = DataBaseMapperClass.getAnswerTypeFromDb(this.surveyDatabase, currentPageLastQuestionId, surveysId);

                if (!("T").equalsIgnoreCase(answerType)) {
                    HashMap<String, AnswersPage> getAnswerMap = getUserAnsweredResponseFromDB(Integer.parseInt(currentPageLastQuestionId), db, surveyPrimaryKeyId, restUrl);
                    // Storing the last question code except textual questions
                    if (!getAnswerMap.isEmpty()) {
                        currentPageLastQuestionResponseCode = getAnswerMap.get(currentPageLastQuestionId).getAnswerCode();
                    } else {
                        currentPageLastQuestionResponseCode = "";
                    }
                    Logger.logD(TAG, "currentPageLastQuestionResponseCode = " + currentPageLastQuestionResponseCode);
                } else {
                    currentPageLastQuestionResponseCode = "";
                }
            }
            for (int i = 0; i < questionList.size(); i++) {
                // bellow piece of code is for direct skip code through options table
                Logger.logD(TAG, "pageCountValue-------" + radioAnswerCode);
                skipcode = QuestionActivityUtils.checkSkipCode(questionList.get(i), surveyDatabase, radioAnswerCode);
                Logger.logD(TAG, "Skip value of particular qid in question list " + skipcode);
                if ("".equals(skipcode)) {
                    count = lastIndexUsedToFetchQID - 1;
                    Logger.logD(TAG, "pageCountValue-------" + count);
                    skipBlockLevelFlag = false;
                } else {
                    Logger.logD(TAG, "pageCountValue" + pageSetCount);
                    String questionId = skipcode;
                    Logger.logV(TAG, "Skipcode value for current question " + questionId);
                    List<String> displayQids = Arrays.asList(questionId.split(","));
                    if (!displayQids.isEmpty()) {
                        for (int skipQid = 0; skipQid < displayQids.size(); skipQid++) {
                            try {
                                String questionQuery = "select skip_code from Options where question_pid= " + displayQids.get(skipQid) + " and survey_id = " + prefs.getInt(SURVEYID, 0) + " AND skip_code !=''";
                                Logger.logV(TAG, "Question List Query " + questionQuery);
                                Cursor questionCursor = surveyDatabase.rawQuery(questionQuery, null);
                                if (questionCursor.getCount() > 0 && questionCursor.moveToFirst()) {
                                    String skipQidCode = questionCursor.getString(questionCursor.getColumnIndex(Constants.SKIP_CODE));
                                    Logger.logV(TAG, "Question skipCode " + skipQidCode);
                                    if (!"".equals(skipQidCode)) {
                                        displayQidsList.add(displayQids.get(skipQid));
                                    }
                                    count = displayQidsList.indexOf(displayQids.get(0));
                                    deleteSkipData(questionList.get(i), questionId, questionList, radioAnswerCode);
                                    skipBlockLevelFlag = true;
                                    return;
                                } else {
                                    skipFlag = false;
                                    displayQidsList.add(displayQids.get(skipQid));
                                    count = displayQidsList.indexOf(displayQids.get(0));
                                    Logger.logV(TAG, "editTextCount value after skipcodes added to new list" + count);
                                    deleteSkipData(questionList.get(i), skipcode, questionList, radioAnswerCode);
                                    skipBlockLevelFlag = true;
                                    return;
                                }
                            } catch (Exception e) {
                                Logger.logE(TAG, "", e);
                            }
                        }
                        break;
                    }
                }
            }

            Logger.logV(TAG, "Skipflag values before checking skipBlockLevelFlag" + skipFlag);
            if (skipBlockLevelFlag) {
                if (questionList.get(questionList.size() - 1).equals(displayQidsList.get(displayQidsList.size() - 1))) {
                    Logger.logV(TAG, "from skip list to Next set of  question");
                    Logger.logV(TAG, "editTextCount value to get next set of questions " + count);
                    count = lastIndexUsedToFetchQID - 2;
                    listQuestionType.clear();                               // Clearing the list which contain anstype
                    clearAllWidgetMapCounts();
                    nextButtonFunctionality(count, displayQidsList);
                }
            } else {
                if ("".equals(skipcode) && questionList.get(questionList.size() - 1).equals(mainQList.get(mainQList.size() - 1))) {
                    setAllDataToResponse();
                    showSubmitPopUp(mainQList.get(mainQList.size() - 1));
                } else {
                    Logger.logV(TAG, "Normal Flow without skips lastquestionindex" + lastIndexUsedToFetchQID);
                    int lastQuestionIndex = lastIndexUsedToFetchQID - 1;
                    count = lastQuestionIndex + 1;
                    deleteSkipData(mainQList.get(lastQuestionIndex), "", questionList, radioAnswerCode);
                }
            }
            Logger.logV(TAG, "Skipflag values" + skipFlag);

            if (skipFlag) {
                listQuestionType.clear();                               // Clearing the list which contain anstype
                clearAllWidgetMapCounts();
                nextButtonFunctionality(count, mainQList);
            } else {
                spinnerCount = 0;
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception in Next Button Functionality ", e);
        }
    }

    /**
     * method
     */
    private void previousButtonClicked() {
        statusFlag = moduleToHideShowButton(false, 0);// method to store Response to Response table and give Json Stricture
        Logger.logV(TAG, "count" + count);
        try {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            if (questionDisplayPageCount == 1 && pageSetCount == 0 || questionDisplayPageCount == -1) {
                Toast.makeText(this, "NO Previous Question", Toast.LENGTH_SHORT).show();
                statusFlag = moduleToHideShowButton(false, 0);
            } else {
                listQuestionType.clear();
                answersEditText.clear();
                Logger.logV(TAG, "mainQList.get(0)" + multiMapOdd.get(String.valueOf(questionDisplayPageCount)));
                questionPageList = new ArrayList<>();
                questionDisplayPageCount--;
                questionPageList = multiMapOdd.get(String.valueOf(questionDisplayPageCount));

                for (int i = 0; i < questionPageList.size(); i++) {
                    lastIndexUsedToFetchQID = lastIndexUsedToFetchQID - 1;
                }
                if (Constants.blockQids.size() > 1) {
                    Logger.logV(TAG, "mainQList questionPageList.get(0)" + questionPageList.get(0));
                    Logger.logV(TAG, "mainQList.get(0)" + mainQList.get(0));
                    if (questionPageList.get(0).equals(mainQList.get(0))) {
                        pageSetCount--;
                        lastIndexUsedToFetchQID = 0;
                        count = 0;
                        Logger.logV(TAG, "pageSetCount" + pageSetCount);
                        mainQList = DataBaseMapperClass.getQuestionsFromBlocks(surveyDatabase, prefs.getInt(SURVEYID, 0), pageSetCount, RuleEngineUtils.fetchRuleEngineFromPrefs(prefs.getString(Constants.RULE_ENGINE, "")));
                    } else {
                        List<String> previousQids = multiMapOdd.get(String.valueOf(questionDisplayPageCount));
                        if (previousQids.isEmpty())
                            return;
                        clearAllWidgetMapCounts();
                        moduleForSettingNextQuestion(dynamicQuestionSet);  // module to Clear the UI and Reset for New Question
                        for (int j = 0; j < previousQids.size(); j++) {
                            Logger.logD(TAG, "Index Value in Previous Button " + mainQList.indexOf(previousQids.get(j)) + 1);
                            ModuleForCreatingUI(previousQids.get(j));
                            int lastPosition = mainQList.indexOf(previousQids.get(j));
                            if (mainQList.contains(previousQids.get(j))) {
                                lastIndexUsedToFetchQID = lastPosition + 1;
                            }

                        }
                    }
                }
                List<String> previousQuestionIdList = multiMapOdd.get(String.valueOf(questionDisplayPageCount));
                if (previousQuestionIdList.isEmpty())
                    return;
                clearAllWidgetMapCounts();
                // Module to clear the UI and Reset for New Question
                moduleForSettingNextQuestion(dynamicQuestionSet);
                int lastPosition;
                for (int j = 0; j < previousQuestionIdList.size(); j++) {
                    if (hasBlockId) {
                        questionIndex = allBlocksQList.indexOf(previousQuestionIdList.get(j)) + 1;
                    } else {
                        questionIndex = mainQList.indexOf(previousQuestionIdList.get(j)) + 1;
                    }
                    ModuleForCreatingUI(previousQuestionIdList.get(j));
                    lastPosition = mainQList.indexOf(previousQuestionIdList.get(j));

                    if (mainQList.contains(previousQuestionIdList.get(j))) {
                        lastIndexUsedToFetchQID = lastPosition + 1;
                    }
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "exception in selection IDS", e);
        }
    }

    /**
     * method
     */
    private void setAllDataToResponse() {
        try {
            JSONArray jsonDump = new JSONArray();
            for (String key : hashMapAnswersEditText.keySet()) {
                List<Response> value = hashMapAnswersEditText.get(key);
                if (value.size() > 1) {

                    for (int getInnerResponse = 0; getInnerResponse < value.size(); getInnerResponse++) {
                        Gson gson = new Gson();
                        String json = gson.toJson(value.get(getInnerResponse));
                        JSONObject request = new JSONObject(json);
                        jsonDump.put(request);
                        Logger.logD(TAG, "the Bean to Sting ->" + jsonDump.toString());
                        answersEditText.add(value.get(getInnerResponse));
                    }
                } else {
                    // execute to the normal question of all the widget (Not grid or inline)
                    answersEditText.add(value.get(0));
                }
            }

            for (int delete = 0; delete < answersEditText.size(); delete++) {
                Logger.logV(TAG, "AnswerCode Value" + answersEditText.get(delete).getQ_id());
                DataBaseMapperClass.deletePreviousSetOfQuestion(answersEditText.get(delete).getQ_id(), db, surveyPrimaryKeyId);
            }
            new InsertTask().insertTask(answersEditText, surveyHandler, surveyPrimaryKeyId);
            for (String key : hashMapAnswersEditText.keySet()) {
                List<Response> value = hashMapAnswersEditText.get(key);
                if (value.size() > 1) {
                    DataBaseMapperClass.deletePreviousSetOfResponseJsonDump(value.get(0).getQ_id(), db, surveyPrimaryKeyId);
                    new InsertResponseDump().InsertResponseDump(value.get(0), surveyHandler, surveyPrimaryKeyId, jsonDump.toString());
                    int getDumpPrimaryID = DataBaseMapperClass.getPrimaryID(db, surveyPrimaryKeyId, value.get(0).getQ_id());
                    Logger.logD(TAG, "the getDumpPrimaryID->" + getDumpPrimaryID);
                    surveyHandler.updatePrimaryidToResponse(value.get(0).getQ_id(), surveyPrimaryKeyId, getDumpPrimaryID);
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on adding values to responnse", e);
        }
        Logger.logV(TAG, "hashMapAnswersEditText" + hashMapAnswersEditText.size());
    }

    /**
     * method
     *
     * @param count
     * @param questionList
     */
    private void nextButtonFunctionality(int count, List<String> questionList) {
        setAllDataToResponse();
        // module to Clear the UI and Reset for New Question
        moduleForSettingNextQuestion(dynamicQuestionSet);
        questionDisplayPageCount++;
        // bellow condition is to say question id not found means some thing wrong
        ModuleToSetForm(count, pageSetCount, questionList);

    }

    /**
     * @param currentQid
     */
    private void showSubmitPopUp(final String currentQid) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("This will submit the form, would you like to continue?");
        builder1.setCancelable(true);
        skipcode = "";
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        int fromIndex = allBlocksQList.indexOf(currentQid);
                        fromIndex = fromIndex + 1;
                        deletedCodes.clear();
                        for (int deleteId = fromIndex; deleteId < allBlocksQList.size(); deleteId++) {
                            deletedCodes.add(String.valueOf(allBlocksQList.get(deleteId)));
                            Logger.logD(TAG, "deleteId " + deleteId);
                        }
                        //       deleteQidsFromResponse(deletedCodes);
                        Toast.makeText(SurveyQuestionActivity.this, "Validating...", Toast.LENGTH_SHORT).show();
//                        PreviewPopUp pre = new PreviewPopUp();
//                        pre.showPreviewPopUp(SurveyQuestionActivity.this, surveyPrimaryKeyId, dbOpenHelper, nextB, previousButton, surveysId);
                        //Modified by Guru
                        callPopUpActivity();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.show();
    }

    /**
     * bellow method is to submit to server and come out to summary page
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submitCloseActivity() {
        try {

            // Saving the response and clearing exiting page
            pageSetCount = 0;
            Constants.blockQids.clear();
            mainQList.clear();

            ToastUtils.displayToast("Submitting...", SurveyQuestionActivity.this);
            updateRecord(SurveyQuestionActivity.this);

            //Update End time in the Database for this record
            surveyHandler.updateEndSurveyStatusDataToDB(surveyPrimaryKeyId);

            // Copy database back for just now submitted record
            Utils.previousUserCopyEncryptedDataBase(SurveyQuestionActivity.this, "SurveyLoading", "ENCRYPTED.db", surveyPreferences.getString("user_name", ""), "yyyy-MM-dd HH:mm:ss");

            // Intimate previous screen by initialing intent service
            Intent intent = new Intent(this, MyIntentService.class);
            startService(intent);

            // Push data to server by API call in internet available
            if (chckNework.checkNetwork()) {
                AutoSyncActivity autoSync = new AutoSyncActivity(this);
                autoSync.callingAutoSync(2);
            }

            // close current activity screen and relaunch previous page with updates if any changes in current screen
            Intent nextIntent;
            if (surveyPreferences.getBoolean("isLocationBased", false)) {
                Utilities.setLocationSurveyFlag(surveyPreferences,"clear");
                finish();
                return;
            }
            else {

                intent.putExtra("surveyIdDCF","1");
             /*if (surveyPreferences.getString(Constants.SURVEYSTATUSTYPR,"").equalsIgnoreCase("new")){
                 Utilities.setLocationSurveyFlag(surveyPreferences,"clear");
                 Intent intent1=new Intent(this, LocationBasedActivity.class);
                 intent1.putExtra(Constants.PERIODICITY,"");
                 intent1.putExtra(Constants.P_LIMIT,1);
                 intent1.putExtra("periodicity_count",0);
                 intent1.putExtra(SURVEY_ID,94);
                 intent1.putExtra("survey_name","Farm Bund");
                 intent1.putExtra("benId","");
                 startActivity(intent1);
             }*/
                nextIntent = new Intent(SurveyQuestionActivity.this, ListingActivity.class);
                finish();
                            }
            //closeLaunchNextPage(nextIntent);
        } catch (Exception e) {
            Logger.logE("", "Submit functionality", e);
        }
    }

    /**
     * method
     *
     * @param intent passing intent
     */
    public void closeLaunchNextPage(Intent intent) {

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.HEADER_NAME,prefs.getString("Survey_tittle", ""));
        startActivity(intent);
        questionDisplayPageCount = 1;
        SupportClass supportClass = new SupportClass();
        supportClass.setSaveData(true, SurveyQuestionActivity.this);
        finish();
    }

    /**
     * @param m_context This method is used to update the record
     */
    public void updateRecord(Context m_context) {
        surveyHandler.updateendSurvey_statusDataToDB(surveyPrimaryKeyId, 1, "", String.valueOf(charge));
        surveyHandler.insert_VenueDetails(values, "Tabdetails");
        Intent i = new Intent(m_context, MyIntentService.class);
        m_context.startService(i);
    }

    /**
     * method
     */
    public void setText() {
        latitudeValue.setText(String.valueOf(Constants.locationLatitude));
        longitudeValue.setText(String.valueOf(Constants.locationLongitude));
        Constants.lang_i = String.valueOf(Constants.locationLongitude);
        Constants.lat_i = String.valueOf(Constants.locationLatitude);
    }

    /**
     * @param visibility making submit or next flag
     * @param flag       saving the same in to an integer variable
     * @return returning the integer
     */
    private int moduleToHideShowButton(boolean visibility, int flag) {
        int flagStatusReturn = 0;
        if (visibility) {
            nextB.setText(R.string.SUBMIT);
            flagStatusReturn = flag;
        } else {
            nextB.setText(R.string.NEXT);
            flagStatusReturn = flag;
        }
        return flagStatusReturn;
    }

    /**
     * displaying the for the date question type
     */
    private void updateDisplay() {
        try {
            if (setDateView == 1) {
                Date date = new Date();
                String UserFormate = (new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" ")).toString().trim();
                String data = new SimpleDateFormat(UserFormate, Locale.ENGLISH).format(date);
                Button dynamicButton = buttonDynamic.get(qcode);
                dynamicButton.setText(data);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on setting the date picker in forms", e);
        }
    }

    /**
     * @param dateDialogId
     * @param tf
     */
    private void showTimeDialog(int dateDialogId, boolean tf) {
        if (dateDialogId == this.dateDialogId) {
            Calendar now = Calendar.getInstance();
            MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                    //Not using now
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), tf);
            mTimePicker.show();
        }
    }


    /**
     * method
     */
    private void handleChangeProfilePic() {
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SurveyQuestionActivity.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveyQuestionActivity.this);
        builder.setTitle("Select Image");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (dialog == null)
                    return;
                if (item == 0)
                    takePicture();
                else
                    openGallery();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * method
     */
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
            /*
             * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
             */
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            Log.d(SurveyQuestionActivity.this.getClass().getSimpleName(), SurveyQuestionActivity.this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 301 && resultCode == 207) {
                locationFetch(data);
            }
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    String filePath = null;
                    Cursor cursor = null;
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath = cursor.getString(columnIndex);
                            cursor.close();
                        }
                        performGallery(filePath);
                    }
                    break;
                case REQUEST_CODE_TAKE_PICTURE:
                    performUpload();
                    break;
                case POP_UP_ACTIVITY:
                    checkResult(resultCode);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.logE(SurveyQuestionActivity.this.getClass().getSimpleName(), "Exception on Activity Result", e);
        }
    }

    /**
     * @param resultCode
     */
    private void checkResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK)
            submitCloseActivity();
        else{
            nextB.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param data
     */
    public void locationFetch(Intent data) {
        if (data == null || data.getExtras() == null)
            return;
        Bundle mBundle = data.getExtras();
        try {
            Constants.locationLatitude = Double.valueOf(mBundle.getString("latitude"));
            Constants.locationLongitude = Double.valueOf(mBundle.getString("longitude"));
            setText();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param questionCode
     * @param answer
     * @param qType
     */
    private void fillResponseToDB(int questionCode, String answer, int qType) {
        List<Response> answersCollection = new ArrayList<>();
        List<AnswersPage> ans_code = hashMapForAnswerBeen.get(String.valueOf(questionCode));
        int survey_ID = getSharedPreferences(MY_PREFS_NAME_SURVEY, MODE_PRIVATE).getInt(SURVEYID, 0);
        Response response = new Response(String.valueOf(questionCode), answer, ans_code.get(0).getAnswerCode(), "0", questionCode, 0, String.valueOf(survey_ID), 0, ans_code.get(0).getId(), QuestionActivityUtils.getQuestionType(qType));
        answersCollection.add(response);
        hashMapAnswersEditText.put(MessageFormat.format("{0}_{1}", questionCode, qType), answersCollection);
    }
    /**
     * @param questionCode
     * @param answer
     * @param qType
     */
    private void fillAIResponseToDB(int questionCode, String answer, int qType,String benificaryName) {
        List<Response> answersCollection = new ArrayList<>();
        List<AnswersPage> ans_code = hashMapForAnswerBeen.get(String.valueOf(questionCode));
        int survey_ID = getSharedPreferences(MY_PREFS_NAME_SURVEY, MODE_PRIVATE).getInt(SURVEYID, 0);
        Response response = new Response(String.valueOf(questionCode), answer, ans_code.get(0).getAnswerCode(), benificaryName, questionCode, 0, String.valueOf(survey_ID), 0, ans_code.get(0).getId(), QuestionActivityUtils.getQuestionType(qType));
        answersCollection.add(response);
        hashMapAnswersEditText.put(MessageFormat.format("{0}_{1}", questionCode, qType), answersCollection);
    }

    /**
     * @param displayQids
     */
    private void deleteQidsFromResponse(List<String> displayQids) {
        for (int i1 = 0; i1 < displayQids.size(); i1++) {
            String delete_query = "DELETE  FROM  Response where q_id= '" + displayQids.get(i1) + "' and survey_id = '" + surveyPrimaryKeyId + "'";
            db.execSQL(delete_query);
        }
    }

    /**
     * @param currentQid
     * @param skipQid
     * @param questionList
     * @param radioAnswerCode
     */
    private void deleteSkipData(final String currentQid, final String skipQid, List<String> questionList, String radioAnswerCode) {
        final List<String> displayQids = Arrays.asList(skipQid.split(","));
        int fromIndex = allBlocksQList.indexOf(currentQid);
        fromIndex = fromIndex + 1;
        deletedCodes.clear();
        for (int deleteId = fromIndex; deleteId < allBlocksQList.size(); deleteId++) {
            deletedCodes.add(String.valueOf(allBlocksQList.get(deleteId)));
        }
        //TODO for the deleteFlag in the below if condition
        if (!radioAnswerCode.equalsIgnoreCase(currentPageLastQuestionResponseCode) && !currentPageLastQuestionResponseCode.isEmpty()) {
            skipFlag = false;
            if ("".equals(displayQids.get(0))) {
                showAlertUserToDelete(skipQid, currentQid, displayQids, deletedCodes);
            } else {
                showAlertUserToDelete(displayQids.get(0), currentQid, displayQids, deletedCodes);
            }
        } else {
            if ("".equals(displayQids.get(0))) {
                count = mainQList.indexOf(currentQid);
                count++;
                listQuestionType.clear();
                clearAllWidgetMapCounts();
                nextButtonFunctionality(count, mainQList);
            } else {
                count = questionList.indexOf(displayQids.get(0));
                if (count == -1) {
                    count = 0;
                } else {
                    count = questionList.indexOf(displayQids.get(0));
                }
                listQuestionType.clear();
                clearAllWidgetMapCounts();
                nextButtonFunctionality(count, displayQids);
            }
            skipBlockLevelFlag = true;
        }
    }

    /**
     * @param skipQid
     * @param qid
     * @param qids
     * @param displayQids
     */
    private void showAlertUserToDelete(final String skipQid, final String qid, final List<String> qids, final List<String> displayQids) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SurveyQuestionActivity.this);
        alertDialogBuilder.setMessage("Do you want to remove the data in between the skip questions? ");
        alertDialogBuilder.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if ("".equals(qids.get(0)) && skipQid.isEmpty()) {
                            count = mainQList.indexOf(qid);
                            count++;
                            deleteQidsFromResponse(displayQids);
                            listQuestionType.clear();                               // Clearing the list which contain anstype
                            clearAllWidgetMapCounts();
                            nextButtonFunctionality(count, mainQList);
                        } else {
                            count = qids.indexOf(skipQid);
                            deleteQidsFromResponse(displayQids);
                            listQuestionType.clear();                               // Clearing the list which contain anstype
                            clearAllWidgetMapCounts();
                            nextButtonFunctionality(count, qids);
                        }
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showPopUpFlag = true;
                        alertDialog.dismiss();

                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * @param questionCode
     * @param answer
     * @param answerCode
     * @param qType
     */
    public void fillResponseToDBNONwidget(int questionCode, String answer, String answerCode, int qType) {

        List<Response> answersCollectionList = new ArrayList<>();
        String optionPID = DataBaseMapperClass.getAnswerPid(String.valueOf(questionCode), surveyDatabase, answerCode, answerCode);
        int survey_ID = getSharedPreferences(MY_PREFS_NAME_SURVEY, MODE_PRIVATE).getInt(SURVEYID, 0);
        Response response = new Response(String.valueOf(questionCode), answer, answerCode, "0", questionCode, 0, String.valueOf(survey_ID), 0, Integer.parseInt(optionPID), QuestionActivityUtils.getQuestionType(qType));
        answersCollectionList.add(response);
        hashMapAnswersEditText.put(questionCode + "_" + qType, answersCollectionList);
    }


    /**
     * upload image functionality
     */
    public void performUpload() {
        if (null != mFileTemp) {
            Bitmap image = ConstantsUtils.compressImage(mFileTemp.getPath(), getApplicationContext());
            imaimageUpload.setImageBitmap(image);
            FileUtils.createDirectoryAndSaveFile(image, String.valueOf(200), dirName, surveyPreferences.getString("UID", ""));
        }
    }

    /**
     * @param filepath display the image to image view with the file path
     */
    public void performGallery(String filepath) {
        try {
            Bitmap image = ConstantsUtils.compressImage(filepath, getApplicationContext());
            imaimageUpload.setImageBitmap(image);
            FileUtils.createDirectoryAndSaveFile(image, String.valueOf(255), dirName, surveyPreferences.getString("UID", ""));
        } catch (Exception e) {
            Logger.logE(TAG, SurveyQuestionActivity.this.getClass().getSimpleName(), e);
        }
    }

    /**
     * calling method to submit and close the current activity submission process
     *
     * @param status
     */
    @Override
    public void OnSuccessfulPreviewSubmit(Boolean status) {

        if (status) {
            submitCloseActivity();
        }

    }

    /**
     * method
     *
     * @return
     */
    public int generateIncrementalInteger() {
        uniqueId = uniqueId + 1;
        return uniqueId;
    }

    public void OnSuccessfullGridInline(final HashMap<String, List<Response>> hashMapGridResponse, View v, final int currentQuestionNumber, HashMap<String, List<String>> fillInlineHashMapKey, int gridType) {
        if (gridType == 16) {
            LinearLayout gridListLinearLayoutOnSuccessfullGridInline = (LinearLayout) v.findViewById(R.id.gridansweredListinline);
            try {
                if (gridListLinearLayoutOnSuccessfullGridInline.getChildCount() > 0) {
                    Logger.logD(TAG, "Clear all the views and added ready to add list if views");
                    gridListLinearLayoutOnSuccessfullGridInline.removeAllViews();
                }
                final List<String> getResponseKeys = new ArrayList<>();
                Logger.logD(TAG, "the Hashmap_Response Size " + hashMapGridResponse.size());
                if (hashMapGridResponse.size() > 0) {
                    List<String> getAllKeys = fillInlineHashMapKey.get(String.valueOf(currentQuestionNumber));
                    Logger.logD(TAG, "the  all key count" + getAllKeys.size());
                    for (int i = 0; i < getAllKeys.size(); i++) {
                        String[] s = getAllKeys.get(i).split("_");
                        if (Integer.valueOf(s[0]) == currentQuestionNumber) {
                            getResponseKeys.add(getAllKeys.get(i));
                            Logger.logD(TAG, "the only key for this QuestioID " + getResponseKeys.toString());
                        }
                    }
                    for (int j = 0; j < getResponseKeys.size(); j++) {
                        final View childInlineGrid = getLayoutInflater().inflate(R.layout.gridlist_adapter, null, false);
                        final View childTemp = getLayoutInflater().inflate(R.layout.dialoginline, null, false);
                        TextView setMandatoryText = (TextView) childInlineGrid.findViewById(R.id.mandatorytextnameEdit);
                        Button editResponseButton = (Button) childInlineGrid.findViewById(R.id.edit);
                        Button deleteResponseButton = (Button) childInlineGrid.findViewById(R.id.delete);
                        List<Response> getResponseListFrmHashmap = hashMapGridResponse.get(getResponseKeys.get(j));
                        List<AssesmentBean> MAssesmant = DataBaseMapperClass.getAssesements(currentQuestionNumber,surveyDatabase,1);
                        gridAssessmentMapDialog.put(String.valueOf(currentQuestionNumber)+ "_ASS",MAssesmant);



                        editResponseButton.setTag(String.valueOf(getResponseKeys.get(j) + "@EDIT"));
                        deleteResponseButton.setTag(String.valueOf(getResponseKeys.get(j) + "@DELETE"));
                        Logger.logD("If edited new Value Print", "-->" + getResponseListFrmHashmap.get(0).getAnswer() + "ANS-->" + setMandatoryText.getText().toString());
                        setMandatoryText.setText(String.valueOf(j) + ": " + MAssesmant.get(0).getAssessment() + " \n Ans  " + getResponseListFrmHashmap.get(0).getAnswer());
                        Logger.logD("If edited new Value Print After", "-->" + setMandatoryText.getText().toString());
                        deleteResponseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] spiltDeleteTag = v.getTag().toString().split("@");
                                Logger.logD(TAG, "hashMapGridResponse-->before " + hashMapGridResponse.size());
                                Logger.logD(TAG, "spiltDeleteTag " + spiltDeleteTag[0]);
                                String[] split_GetIndex = spiltDeleteTag[0].split("_");
                                hashMapGridResponse.remove(spiltDeleteTag[0]);
                                methodToClearHashMapKey(split_GetIndex[1], split_GetIndex[0], spiltDeleteTag[0]);
                                Logger.logD(TAG, "hashMapGridResponse-->after " + hashMapGridResponse.size());
                                ((LinearLayout) childInlineGrid.getParent()).removeView(childInlineGrid);
                            }
                        });

                        Logger.logD(TAG, "EditButton Tag for each  " + editResponseButton.getTag());
                        editResponseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] spiltTag = v.getTag().toString().split("@");
                                Logger.logD(TAG, "splitTag " + spiltTag[0]);
                                List<Response> getResponseForEdit = hashMapGridResponse.get(spiltTag[0]);
                                Logger.logD(TAG, " the size getResponseForEdit " + getResponseForEdit.size());
                                List<AssesmentBean> MAssessment = gridAssessmentMapDialog.get(String.valueOf(currentQuestionNumber) + "_ASS");
                                Page questionPagebean = gridQuestionMapDialog.get(String.valueOf(currentQuestionNumber) + QUESTION);
                                String[] splitKeypare = spiltTag[0].split("_");
                                Logger.logD(TAG, "splitTag " + spiltTag[1]);
                                Logger.logD(TAG, "rowInflater value " + rowInflater);
                                Logger.logD(TAG, "Key value value " + Integer.getInteger(splitKeypare[1]));
                                if (MAssessment!=null && questionPagebean!=null)
                                    SupportClass.showDialogEdit(getResponseForEdit, MAssessment, SurveyQuestionActivity.this, SurveyQuestionActivity.this, questionPagebean, surveyDatabase, spiltTag[0], childTemp, 16, questionPagebean);
                                else
                                    ToastUtils.displayToast("Some thing  went wrong",SurveyQuestionActivity.this);
                            }
                        });

                        Logger.logD(TAG, "gridListLinearLayoutOnSuccessfullGridInline PRE" + gridListLinearLayoutOnSuccessfullGridInline.getChildCount());
                        gridListLinearLayoutOnSuccessfullGridInline.addView(childInlineGrid);
                        Logger.logD(TAG, "gridListLinearLayoutOnSuccessfullGridInline POST" + gridListLinearLayoutOnSuccessfullGridInline.getChildCount());


                    }
                }
            } catch (Exception e) {

                Logger.logE("Exception", " Exception in Creating GridUI", e);
            }
        } else {
            Button bt = (Button) v;
            bt.setText("Edit");
            bt.setBackgroundColor(getResources().getColor(R.color.meroon));
            String getPreviousTag[] = bt.getTag().toString().split("@");

            bt.setTag(getPreviousTag[0] + "@EDIT");
            Logger.logD(TAG, "Button Tag" + bt.getTag());
        }

    }

    /**
     * methodToClearHashMapKey
     *
     * @param removeIndex removeIndex
     * @param questionID  questionID
     * @param hashMapKey  hashMapKey
     */
    private void methodToClearHashMapKey(String removeIndex, String questionID, String hashMapKey) {
        Logger.logD(TAG, "hashMapGridResponse key  -->before " + fillInlineHashMapKey.size());
        Logger.logD(TAG, "removeIndex" + removeIndex);
        Logger.logD(TAG, "removeIndex" + questionID);
        List<String> getHashMapKey = fillInlineHashMapKey.get(questionID);
        try {
            for (int i = 0; i < getHashMapKey.size(); i++) {
                Logger.logD(TAG, "hashMapKey" + hashMapKey);

                if (getHashMapKey.get(i).equals(hashMapKey)) {
                    getHashMapKey.remove(i);
                }
            }
            fillInlineHashMapKey.put(questionID, getHashMapKey);
            Logger.logD(TAG, "hashMapGridResponse key  -->after " + fillInlineHashMapKey.size());
        } catch (Exception e) {
            Logger.logD("Exception", "" + e);

        }

    }

    private class OperatorTask extends AsyncTask<Context, Integer, String> {
        Context ctx;

        OperatorTask(Context context) {
            ctx = context;
        }

        @Override
        protected String doInBackground(Context... params) {
            try {
                charge = operatorObj.getBatteryLevel(ctx);
                operatorObj.checkOperatorStatus(0);
                values.put(SURVEYID, String.valueOf(surveyPrimaryKeyId));
                values.put("CELL_ID", operatorObj.Cell_id);
                values.put("SIGNAL_STRENGTH", operatorObj.SignalStrength);
                values.put("LAC", operatorObj.Lac);
                values.put("MCC", operatorObj.Mcc);
                values.put("MNC", operatorObj.Mnc);
                values.put("LA", operatorObj.La);
                values.put("CARRIER", operatorObj.CarrierName);
                values.put("NETWORK_TYPE", operatorObj.NetworkType);
                values.put("PHONE_NUMBER", operatorObj.Phoneno);
                values.put("CHARGELEFT", operatorObj.chargepercentage);
                values.put("IS_CONNECTED_TO_CHARGE", "YES");
                values.put("SIM_SERIALNO", operatorObj.simSerialNumber);
                values.put("DEVICEID", operatorObj.deviceId);
                values.put("LAST_CHARGE_TIME", "");
                values.put("sectionId", String.valueOf(prefs.getInt(SURVEYID, 0)));
            } catch (Exception e) {
                Logger.logE(TAG, "Exception in OperatorTask class  doInBackground method", e);
                restUrl.writeToTextFile("Exception on sending the operator details", "", "sendOperatorDetails");
            }
            return null;
        }
    }
    private void callPopUpActivity() {
        editcount = 0;
        gridCountInline=0;
        Intent startIntert = new Intent(SurveyQuestionActivity.this, ShowSurveyPreview.class);
        startIntert.putExtra("surveyPrimaryKey",surveyPrimaryKeyId);
        startIntert.putExtra("survey_id",surveysId);
        startIntert.putExtra("visibility",true);
        startActivityForResult(startIntert,POP_UP_ACTIVITY);
    }
}