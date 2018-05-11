package org.mahiti.convenemis.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.beneficiaryList.Jsondata;
import org.mahiti.convenemis.BeenClass.regionallanguage.GetLanguageAssessment;
import org.mahiti.convenemis.BeenClass.regionallanguage.GetLanguageBlock;
import org.mahiti.convenemis.BeenClass.regionallanguage.GetLanguageOptions;
import org.mahiti.convenemis.BeenClass.regionallanguage.GetLanguageQuestions;
import org.mahiti.convenemis.BeenClass.regionallanguage.RegionalLanguage;
import org.mahiti.convenemis.beansClassSetQuestion.BlockBeen;
import org.mahiti.convenemis.beansClassSetQuestion.LanguageLabelsBeen;
import org.mahiti.convenemis.beansClassSetQuestion.OptionsBeen;
import org.mahiti.convenemis.beansClassSetQuestion.QuestionBeen;
import org.mahiti.convenemis.beansClassSetQuestion.SkipMandatoryBeen;
import org.mahiti.convenemis.beansClassSetQuestion.SkipRulesBeen;
import org.mahiti.convenemis.utils.CommonForAllClasses;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;
import org.mahiti.convenemis.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConveneDatabaseHelper extends SQLiteOpenHelper {

    // Path to the device folder with databases
    private static String DB_PATH;
    //singleton/ single instance reference of database instance
    private static ConveneDatabaseHelper _dbHelper;
    private SharedPreferences preferences;

    private static final String QUESTION_TEXT="question_text";
    private static final String OPTION_TEXT="option_text";
    private static final String TAG="ConveneDatabaseHelper";
    private static final String SKIP_DATA="SkipData";
    private static final String FROM_TABLE=" FROM ";
    private static final String SELECT_MAX="SELECT MAX(";
    public SQLiteDatabase database;
    public final Context context;
    private String uid;
    private String assessmentStr = "assessment";
    private String questionPidStr = "question_pid";
    private String activeStr = "active";
    private String mandatoryStr = "mandatory";
    private String languageIdStr = "language_id";
    private String updatedTimeStr = "updated_time";
    private String extraColumn1Str = "extra_column1";
    private String extraColumn2Str = "extra_column2";
    private String validationStr = "validation";
    private String currentDateStr = "current date--";
    private String asStr = ") AS ";

    /**
     *
     * @param context
     * @param databaseName
     * @param uId
     */
    public ConveneDatabaseHelper(Context context, String databaseName,String uId) {
        super(context, databaseName, null, CommonForAllClasses.version);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Write a full path to the databases of your application
        String packageName = context.getPackageName();
        DB_PATH = String.format("/data/data/%s/databases/", packageName);
        uid=uId;
        close();
        openDataBase();

    }

    /**
     * @param context      Context for calling constructor
     * @param databaseName
     * @return
     */
    public static ConveneDatabaseHelper getInstance(Context context, String databaseName,String uId)
    {
        Logger.logV(TAG,"the path of the db in the getinstance is"+databaseName);

        _dbHelper = new ConveneDatabaseHelper(context, databaseName,uId);

        return _dbHelper;
    }

    /**
     *method to open the sqlite database with open readwrite permission
     * @return
     * @throws SQLException
     */
    public  SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + preferences.getString(Constants.CONVENE_DB,"");
        Logger.logV(TAG,"the path of the db is"+path);
        try {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception var3) {
            Logger.logE(ConveneDatabaseHelper.class.getSimpleName(), "Exception in inopenDataBase method ", var3);
        }
        return database;
    }

    /**
     * method to fill the Assessment table from json response
     * @param listOfObjects
     */
    public void updateAssessmentArray(JSONArray listOfObjects) {
        try
        {
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.length();i++)
            {
                JSONObject object=listOfObjects.getJSONObject(i);
                cv.put("id",object.getInt("id"));
                cv.put(assessmentStr,object.getString(assessmentStr));
                cv.put(questionPidStr,object.getInt(questionPidStr));
                cv.put(activeStr,object.getInt(activeStr));
                cv.put(mandatoryStr,object.getInt(mandatoryStr));
                cv.put("group_validation",object.getString("group_validation"));
                String surveyIdStr = "survey_id";
                cv.put(PreferenceConstants.SURVEY_ID,object.getInt(surveyIdStr));
                cv.put(languageIdStr,object.getInt(languageIdStr));
                cv.put(updatedTimeStr,object.getString(updatedTimeStr));
                cv.put(extraColumn1Str,object.getString(extraColumn1Str));
                cv.put(extraColumn2Str,object.getInt(extraColumn2Str));
                cv.put("qtype",object.getString("qtype"));
                database.insertWithOnConflict(Constants.ASSESSMENT, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG,"Assessment json cv values==>"+ cv.toString());
            }


        }catch (Exception e) {
           Logger.logE(TAG,"",e);
        }

    }


    /**
     * method to check db exist or not ,if exist create or update
     */
    public void createDataBase() {
        boolean dbExist = checkDataBase();
        Logger.logV(TAG, "the db value is" + dbExist);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (dbExist) {
            String dbVersion = prefs.getString("q_dbversion_"+uid, "");
            Logger.logV(TAG, "the db value is" + dbVersion);
            //do nothing - database already exist
        } else {

            this.getReadableDatabase();
            try {
                SharedPreferences.Editor editor_version = prefs.edit();
                editor_version.putString("q_dbversion", uid+"_"+CommonForAllClasses.version);
                editor_version.apply();
                copyDataBase();
            } catch (IOException e) {
                Logger.logE(ExternalDbOpenHelper.class.getSimpleName(), "Exception in createDataBase method ", e);
            }
        }

    }

    /**
     * method  to check the database file exist or not
     * @return
     */
    private boolean checkDataBase()
    {
        try {
            final String mPath = DB_PATH + preferences.getString(Constants.CONVENE_DB,"");
            Logger.logV(TAG,"the check database path is ........"+mPath);
            final File file = new File(mPath);
            return file.exists();
        } catch (SQLiteException e) {
            Logger.logE(ExternalDbOpenHelper.class.getSimpleName(), "Exception in checkDataBase method ", e);
            return false;
        }
    }


    /**
     * method to get and copy the datatbase from assests
     * @throws IOException
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void copyDataBase() throws IOException {
        InputStream myInput=null;
        //Open your local db as the input stream
        myInput = context.getAssets().open(Constants.DB_NAMES);

        // Path to the just created empty db
        String outFileName = DB_PATH +preferences.getString(Constants.CONVENE_DB,"");

        try (OutputStream myOutput = new FileOutputStream(outFileName)){
            //Open the empty db as the output stream
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }catch (Exception e){
            Logger.logE(TAG,"",e);
        }

        //Close the streams

    }

    @Override
    public synchronized void close() {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
		/*Nothing to do in this method      */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				/*Nothing to do in this method      */
    }

    /**
     *method to get the level id based on selected level name
     * @param orderLevel
     * @param level
     * @return
     */
    public int getId(String orderLevel, String level) {
        Logger.logV(TAG, "the level is............" + level);
        int id = 0;
        SQLiteDatabase db = openDataBase();
        String query = "select * from " + orderLevel + " where " + "name='" + level + "'";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG, "the value is" + query);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(orderLevel + "_id"));
            Logger.logV(TAG, "the names are" + id);
            return id;
        }
        cursor.close();
        return id;
    }

    /**
     *method to fill the block table based on json block response
     * @param listOfObjects
     */
    public void updateBlock(BlockBeen listOfObjects) {

        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getBlock().size();i++)
            {
                cv.put("id",listOfObjects.getBlock().get(i).getId());
                cv.put("block_code",listOfObjects.getBlock().get(i).getBlockCode());
                cv.put("block_name",listOfObjects.getBlock().get(i).getBlockName());
                cv.put(languageIdStr,listOfObjects.getBlock().get(i).getLanguageId());
                cv.put(PreferenceConstants.SURVEY_ID,listOfObjects.getBlock().get(i).getSurveyId());
                cv.put("block_order",listOfObjects.getBlock().get(i).getBlockOrder());
                cv.put(activeStr,listOfObjects.getBlock().get(i).getActive());
                cv.put(updatedTimeStr,listOfObjects.getBlock().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getBlock().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getBlock().get(i).getExtraColumn2());
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getId()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getBlockCode()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getLanguageId()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getSurveyId()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getBlockOrder()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getActive()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getUpdatedTime()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getExtraColumn1()));
                Logger.logV(TAG,String.valueOf(listOfObjects.getBlock().get(i).getExtraColumn2()));

                database.insertWithOnConflict("Block", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }catch(Exception e){
            Logger.logE(TAG,"",e);
        }
    }

    /**
     * method to fill the Langauge Assessmnet table based on Language assessmnet json response
     * @param listOfObjects
     */
    public void updatelanguageAssessments(GetLanguageAssessment listOfObjects) {

        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getLanguageAssessment().size();i++)
            {
                cv.put("id",listOfObjects.getLanguageAssessment().get(i).getId()+"-"+listOfObjects.getLanguageAssessment().get(i).getLanguageId());
                cv.put(assessmentStr,listOfObjects.getLanguageAssessment().get(i).getAssessment());
                cv.put("assessment_pid",listOfObjects.getLanguageAssessment().get(i).getAssessmentPid());
                cv.put(languageIdStr,listOfObjects.getLanguageAssessment().get(i).getLanguageId());
                cv.put(updatedTimeStr,listOfObjects.getLanguageAssessment().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getLanguageAssessment().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getLanguageAssessment().get(i).getExtraColumn2());
                database.insertWithOnConflict("LanguageAssessment", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG,"Language Assessment Saved Successfully" + cv.toString());
            }

        }catch(Exception e){
            Logger.logE(TAG,"",e);
        }
    }

    /**
     * method to fill the Language Block table based on language block json response
     * @param listOfObjects
     */
    public void updatelanguageBlock(GetLanguageBlock listOfObjects) {

        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getLanguageBlock().size();i++)
            {
                cv.put("id",listOfObjects.getLanguageBlock().get(i).getId()+"-"+listOfObjects.getLanguageBlock().get(i).getLanguageId());
                cv.put("block_pid",listOfObjects.getLanguageBlock().get(i).getBlockPid());
                cv.put("block_name",listOfObjects.getLanguageBlock().get(i).getBlockName());
                cv.put(languageIdStr,listOfObjects.getLanguageBlock().get(i).getLanguageId());
                cv.put(updatedTimeStr,listOfObjects.getLanguageBlock().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getLanguageBlock().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getLanguageBlock().get(i).getExtraColumn2());
                database.insertWithOnConflict("LanguageBlock", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }

        }catch(Exception e){
            Logger.logE(TAG,"",e);
        }
    }

    /**
     * method to fill the langauge labels based on language label json response
     * @param listOfObjects
     */
    public void updatelanguageLabels(LanguageLabelsBeen listOfObjects) {

        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getLanguageLabels().size();i++)
            {
                cv.put("id",listOfObjects.getLanguageLabels().get(i).getId()+"-"+listOfObjects.getLanguageLabels().get(i).getLanguageId());
                cv.put("label_key",listOfObjects.getLanguageLabels().get(i).getLabelKey());
                cv.put("label_value",listOfObjects.getLanguageLabels().get(i).getLabelValue());
                cv.put(languageIdStr,listOfObjects.getLanguageLabels().get(i).getLanguageId());
                cv.put(updatedTimeStr,listOfObjects.getLanguageLabels().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getLanguageLabels().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getLanguageLabels().get(i).getExtraColumn2());
                database.insertWithOnConflict("LanguageLabel", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }catch(Exception e){
            Logger.logE(TAG,"",e);
        }
    }

    /**
     * method to fill the language question based on json response
     * @param listOfObjects
     */
    public void updatelanguageQuestion(GetLanguageQuestions listOfObjects) {

        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getLanguageQuestion().size();i++)
            {
                cv.put("id",listOfObjects.getLanguageQuestion().get(i).getId()+"-"+listOfObjects.getLanguageQuestion().get(i).getLanguageId());
                cv.put(questionPidStr,listOfObjects.getLanguageQuestion().get(i).getQuestionPid());
                cv.put(QUESTION_TEXT,listOfObjects.getLanguageQuestion().get(i).getQuestionText());
                cv.put("help_text",listOfObjects.getLanguageQuestion().get(i).getHelpText());
                cv.put("instruction",listOfObjects.getLanguageQuestion().get(i).getInstruction());
                cv.put(languageIdStr,listOfObjects.getLanguageQuestion().get(i).getLanguageId());
                cv.put(updatedTimeStr,listOfObjects.getLanguageQuestion().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getLanguageQuestion().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getLanguageQuestion().get(i).getExtraColumn2());
                database.insertWithOnConflict("LanguageQuestion", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG,"Language Question Saved Successfully " + cv.toString());
            }
            Utils.copyEncryptedConveneDataBase(context,preferences);
        }catch(Exception e){
            Logger.logE(TAG,"",e);
        }
    }


    /**
     * method to fill the options table based on json response
     * @param listOfObjects
     */
    public void updateOptions(OptionsBeen listOfObjects) {
        try {
            ContentValues cv = new ContentValues();
            database = this.getWritableDatabase();
            for (int i = 0; i < listOfObjects.getOptions().size(); i++) {
                cv.put("id", listOfObjects.getOptions().get(i).getId());
                cv.put(questionPidStr, listOfObjects.getOptions().get(i).getQuestionPid());
                cv.put("option_code", listOfObjects.getOptions().get(i).getOptionCode());
                cv.put("option_flag", listOfObjects.getOptions().get(i).getOptionFlag());
                Logger.logV(TAG,"SkipCOde value" + listOfObjects.getOptions().get(i).getSkipCode());
                if (("").equals(listOfObjects.getOptions().get(i).getSkipCode())) {
                    cv.put("skip_code", listOfObjects.getOptions().get(i).getSkipCode());
                } else {
                updateSkipData(database, String.valueOf(listOfObjects.getOptions().get(i).getId()), listOfObjects.getOptions().get(i).getSkipCode(), listOfObjects.getOptions().get(i).getActive(), listOfObjects.getOptions().get(i).getUpdatedTime(),listOfObjects.getOptions().get(i).getQuestionPid());
                cv.put("skip_code", listOfObjects.getOptions().get(i).getSkipCode());
                }
                cv.put(validationStr, listOfObjects.getOptions().get(i).getValidation());
                cv.put(OPTION_TEXT, listOfObjects.getOptions().get(i).getOptionText());
                cv.put(activeStr, listOfObjects.getOptions().get(i).getActive());
                cv.put(languageIdStr, listOfObjects.getOptions().get(i).getLanguageId());
                cv.put(PreferenceConstants.SURVEY_ID, listOfObjects.getOptions().get(i).getSurveyId());
                cv.put("image_path", listOfObjects.getOptions().get(i).getImagePath());
                cv.put("is_answer", listOfObjects.getOptions().get(i).getIsAnswer());
                cv.put(updatedTimeStr, listOfObjects.getOptions().get(i).getUpdatedTime());
                cv.put(extraColumn1Str, listOfObjects.getOptions().get(i).getExtraColumn1());
                cv.put(extraColumn2Str, listOfObjects.getOptions().get(i).getExtraColumn2());
                cv.put("Assessment_pid", listOfObjects.getOptions().get(i).getAssessmentPid());
                cv.put("option_order", listOfObjects.getOptions().get(i).getOption_order());
                database.insertWithOnConflict("Options", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG,"Choice table data"+ cv.toString());
            }
        } catch (Exception e) {
            Logger.logE(TAG,"Exception", e);
        }
    }

    /**
     * method to fill the skipdata table based on the json response
     * @param database
     * @param optionId
     * @param skipCode
     * @param active
     * @param updatedTime
     * @param questionPid
     */
    private void updateSkipData(SQLiteDatabase database, String optionId, String skipCode, Integer active, String updatedTime, Integer questionPid) {
        try{
            deleteData(optionId, database);
            List<String> displayQids= Arrays.asList(skipCode.split(","));
            if(!displayQids.isEmpty()){
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < displayQids.size(); i++) {
                        values.put("option_id", Integer.parseInt(optionId));
                        values.put("question_id", Integer.parseInt(displayQids.get(i)));
                        values.put("activate", active);
                        values.put("skip_order", 0);
                        values.put("extra_int", 0);
                        values.put("extra_str", "");
                        values.put("modified", updatedTime);
                        database.insert(SKIP_DATA, null, values);
                        Logger.logV(TAG, "SkipData is inserted Successfully" + values.toString());
                    }
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception while inserting into Skipdata table", e);
        }

    }


    /**
     * method to delete the options based on option id before filling the options
     * @param questionPid
     * @param database
     */
    private void deleteData(String questionPid, SQLiteDatabase database) {
        String where = "option_id = ?";
        String[] whereArgs = new String[]{questionPid + ""};
        try {
            database.delete(SKIP_DATA, where, whereArgs);
        } catch (Exception e) {
            Logger.logE(TAG,"Exception on deleting the skipdata", e);
        }
    }

    /**
     * method to fill the Question based on json response from question api
     * @param listOfObjects
     */
    public void updateQuestions(QuestionBeen listOfObjects) {
        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getQuestion().size();i++)
            {
                cv.put("id",listOfObjects.getQuestion().get(i).getId());
                cv.put("question_code",listOfObjects.getQuestion().get(i).getQuestionCode());
                cv.put("answer_type",listOfObjects.getQuestion().get(i).getAnswerType());
                cv.put(PreferenceConstants.SURVEY_ID,listOfObjects.getQuestion().get(i).getSurveyId());
                cv.put("block_id",listOfObjects.getQuestion().get(i).getBlockId());
                cv.put("sub_question",listOfObjects.getQuestion().get(i).getSubQuestion());
                cv.put(QUESTION_TEXT,listOfObjects.getQuestion().get(i).getQuestionText());
                cv.put("help_text",listOfObjects.getQuestion().get(i).getHelpText());
                cv.put("instruction_text",listOfObjects.getQuestion().get(i).getInstructionText());
                cv.put(activeStr,listOfObjects.getQuestion().get(i).getActive());
                cv.put(languageIdStr,listOfObjects.getQuestion().get(i).getLanguageId());
                cv.put(mandatoryStr,listOfObjects.getQuestion().get(i).getMandatory());
                cv.put("question_order",listOfObjects.getQuestion().get(i).getQuestionOrder());
                cv.put("image_path",listOfObjects.getQuestion().get(i).getImagePath());
                cv.put("answer",listOfObjects.getQuestion().get(i).getAnswer());
                cv.put("keyword",listOfObjects.getQuestion().get(i).getKeyword());
                cv.put(updatedTimeStr,listOfObjects.getQuestion().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getQuestion().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getQuestion().get(i).getExtraColumn2());
                cv.put(validationStr,listOfObjects.getQuestion().get(i).getValidation());
                cv.put("parent_beneficiary_id",String.valueOf(listOfObjects.getQuestion().get(i).getParentBeneficiaryid()));
                cv.put("question_id",String.valueOf(listOfObjects.getQuestion().get(i).getQuestionid()));
                database.insertWithOnConflict("Question", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }catch(Exception e){
           Logger.logE(TAG,"",e);
        }
    }


    /**
     * mthod to get the list of beneficiary details from beneficiary table
     * @return
     */
    public List<Jsondata> getBeneficiaryList()
    {
        List<Jsondata> list=new ArrayList<>();
        try {
            SQLiteDatabase databases=this.getReadableDatabase();
            String query="Select * from Benificiary"  ;
            Logger.logD(TAG,"Benificiary details" + query);
            Cursor cursor=databases.rawQuery(query,null);
            if(cursor.moveToFirst()){
                do{
                    String gpId=cursor.getString(cursor.getColumnIndex("gramapanchayath_id"));
                    String villageId=cursor.getString(cursor.getColumnIndex("village_id"));
                    String schoolId=cursor.getString(cursor.getColumnIndex("school_type_id"));
                    String name=cursor.getString(cursor.getColumnIndex("name"));
                    String address1=cursor.getString(cursor.getColumnIndex("address1"));
                    String address2=cursor.getString(cursor.getColumnIndex("address2"));
                    String pincode=cursor.getString(cursor.getColumnIndex("pincode"));
                    String districtId=cursor.getString(cursor.getColumnIndex("district_id"));
                    String contactNo=cursor.getString(cursor.getColumnIndex("contact_no"));
                    String bType=cursor.getString(cursor.getColumnIndex("btype"));
                    String stateId=cursor.getString(cursor.getColumnIndex("state_id"));
                    String talukId=cursor.getString(cursor.getColumnIndex("taluk_id"));
                    String age=cursor.getString(cursor.getColumnIndex("age"));
                    Jsondata bean=new Jsondata();
                    bean.setGramapanchayathId(gpId);
                    bean.setVillageId(villageId);
                    bean.setSchoolTypeId(schoolId);
                    bean.setName(name);
                    bean.setAddress1(address1);
                    bean.setAddress2(address2);
                    bean.setPincode(pincode);
                    bean.setDistrictId(districtId);
                    bean.setContactNo(contactNo);
                    bean.setBtype(bType);
                    bean.setStateId(stateId);
                    bean.setTalukId(talukId);
                    bean.setAge(age);
                    list.add(bean);
                }while (cursor.moveToNext());

            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception in getAnganwadi list ==>" , e);
        }
        return list;
    }


    /**
     * method to fill the skipmandatory table based on json response from skipmandatory api
     * @param listOfObjects
     */
    public void updateSkipMandatory(SkipMandatoryBeen listOfObjects) {
        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getSkipMandatory().size();i++)
            {
                cv.put("id",listOfObjects.getSkipMandatory().get(i).getId());
                cv.put(questionPidStr,listOfObjects.getSkipMandatory().get(i).getQuestionPid());
                cv.put("question_validation",listOfObjects.getSkipMandatory().get(i).getQuestionValidation());
                cv.put("validation_order",listOfObjects.getSkipMandatory().get(i).getValidationOrder());
                cv.put("skip_or_mandatory",listOfObjects.getSkipMandatory().get(i).getSkipOrMandatory());
                cv.put("sub_module_type",listOfObjects.getSkipMandatory().get(i).getSubModuleType());
                cv.put(updatedTimeStr,listOfObjects.getSkipMandatory().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getSkipMandatory().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getSkipMandatory().get(i).getExtraColumn2());
                database.insertWithOnConflict("SkipMandatory", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }catch(Exception e){
            Logger.logE(TAG,"updateSkipMandatory",e);
        }
    }

    /**
     * method to fill the skiprules based on json response from skiprules api
     * @param listOfObjects
     */
    public void updateSkipRules(SkipRulesBeen listOfObjects) {
        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getSkipRules().size();i++)
            {
                cv.put("id",listOfObjects.getSkipRules().get(i).getId());
                cv.put(PreferenceConstants.SURVEY_ID,listOfObjects.getSkipRules().get(i).getSurveyId());
                cv.put("question_id",listOfObjects.getSkipRules().get(i).getQuestionId());
                cv.put("sub_module_type",listOfObjects.getSkipRules().get(i).getSubModuleType());
                cv.put("show_status",listOfObjects.getSkipRules().get(i).getShowStatus());
                cv.put(updatedTimeStr,listOfObjects.getSkipRules().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getSkipRules().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getSkipRules().get(i).getExtraColumn2());
                database.insertWithOnConflict("SkipRules", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }catch(Exception e){
            Logger.logE(TAG,"updateSkipRules",e);
        }
    }

    /**
     * method to get the recently added last modified date  from table
     * @param table
     * @param columnNmae
     * @return
     */
    public String getLastUpDate(String table, String columnNmae) {
        String date = "";
        String selectQuery = SELECT_MAX + columnNmae + asStr +columnNmae +FROM_TABLE + table;
        Logger.logD(TAG,currentDateStr+ selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(columnNmae));
            cursor.close();
        }
        if(date==null)
            date="";
        db.close();
        Logger.logD(TAG, "getLastUpDate--" + date);
        return date;
    }

    /**
     *
     * @param table
     * @param columnNmae
     * @return
     */
    public int getCursorCount(String table, String columnNmae) {
        String date = "";
        String selectQuery = SELECT_MAX + columnNmae + asStr +columnNmae +FROM_TABLE + table;
        Logger.logD(TAG,currentDateStr+ selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(columnNmae));

        }
        if(date==null)
            date="";
        db.close();
        cursor.close();
        
        Logger.logD(TAG, "getLastUpDates--" + date);
        return cursor.getCount();
    }


    /**
     * method to get the recently added option last modified date from table
     * @param table
     * @param columnNmae
     * @return
     */
    public String getLastLanguageOptionsUpdates(String table, String columnNmae) {
        String date = "";
        String selectQuery = SELECT_MAX + columnNmae + asStr +columnNmae +FROM_TABLE + table;
        Logger.logD(TAG,currentDateStr+ selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(columnNmae));
            cursor.close();
        }
        if(date==null)
            date="";
        db.close();
        Logger.logD(TAG, "getLastUpDate--" + date);
        return date;
    }


    /**
     * method to fill the Languageoptions table based on json response from languageoptions api
     * @param listOfObjects
     */
    public void updateLanguageOptions(GetLanguageOptions listOfObjects) {
        try{
            ContentValues cv=new ContentValues();
            database=this.getWritableDatabase();
            for(int i=0;i<listOfObjects.getLanguageOptions().size();i++)
            {
                cv.put("id",listOfObjects.getLanguageOptions().get(i).getId()+"-"+listOfObjects.getLanguageOptions().get(i).getLanguageId());
                cv.put(OPTION_TEXT,listOfObjects.getLanguageOptions().get(i).getOptionText());
                cv.put(validationStr,listOfObjects.getLanguageOptions().get(i).getValidation());
                cv.put("option_pid",listOfObjects.getLanguageOptions().get(i).getOptionPid());
                cv.put(languageIdStr,listOfObjects.getLanguageOptions().get(i).getLanguageId());
                cv.put(updatedTimeStr,listOfObjects.getLanguageOptions().get(i).getUpdatedTime());
                cv.put(extraColumn1Str,listOfObjects.getLanguageOptions().get(i).getExtraColumn1());
                cv.put(extraColumn2Str,listOfObjects.getLanguageOptions().get(i).getExtraColumn2());
                cv.put(questionPidStr,listOfObjects.getLanguageOptions().get(i).getQuestionPid());
                database.insertWithOnConflict("LanguageOptions", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Logger.logV(TAG,"LanguageOptions Saved Successfully " + cv.toString());
            }
            Utils.copyEncryptedConveneDataBase(context,preferences);
        }catch(Exception e){
            Logger.logE(TAG,"updateLanguageOptions",e);
        }
    }

    /**
     * method to get the question text based on the qid .
     * @param questionID
     * @param languageId
     * @param surveysId
     * @return
     */
    public String getQuestion(int questionID, int languageId, int surveysId) {
        String questionName = "";
        String query;
        SQLiteDatabase sqldb = openDataBase();
        if (languageId == 1) {
            query = "SELECT DISTINCT Question.help_text, Question.id, Question.block_id, Question.question_code,  Question.answer_type,Question.mandatory, Question.question_text, Question.validation from Question , Options  where Question.id=" + questionID + " and Question.active = 2 and Question.survey_id= " + surveysId;
        } else {
            query = "SELECT DISTINCT Question.help_text, Question.id, Question.block_id, Question.question_code, Question.answer_type,  Question.mandatory, LanguageQuestion.question_text, Question.validation from Question ,LanguageQuestion, Options  where Question.id=" + questionID + " and Question.active = 2 and  LanguageQuestion.question_pid=Question.id and LanguageQuestion.language_id=" + languageId + " and  Question.survey_id= " + surveysId;
        }
        Cursor cursor = sqldb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do questionName = cursor.getString(cursor.getColumnIndex(QUESTION_TEXT));
            while (cursor.moveToNext());
            cursor.close();
        }
        close();
        return questionName;
    }


    /**
     * method to get the answer text based on the qid .
     * @param option_code
     * @param languageId
     * @param questionID
     */
    public String getOptionText(String option_code, int languageId, int questionID) {
        String questionName="";
        String query="";
        SQLiteDatabase sqldb = openDataBase();
        if (languageId!=1)
            query="SELECT o.id,l.option_text from Options o, Question q, LanguageOptions l  where o.question_pid=q.id and q.id=" + questionID+ " and l.option_pid=o.id and o.id = " + option_code + " and l.language_id= " + languageId+" ORDER BY o.option_order";
        else
            query="SELECT a.id,option_text from Options a, Question q where a.question_pid=q.id and a.id="+option_code+" ORDER BY a.option_order";

        Cursor cursor = sqldb.rawQuery(query, null);
        Logger.logD("Query","Query Options"+query+"-->"+cursor.getCount());
        if (cursor.moveToFirst()) {
            do questionName = cursor.getString(cursor.getColumnIndex(OPTION_TEXT));
            while (cursor.moveToNext());
            Logger.logD("","selected option"+ questionName);
            cursor.close();
        }
        close();
        return questionName;
    }


    /**
     * method to check language id is availbale for selected survey in Question table
     * @param surveyId
     * @param languageId
     * @return
     */
    public boolean checkLanguageExistOrNot(int surveyId, int languageId) {
        boolean isExist;
        String query="";
        SQLiteDatabase sqldb = openDataBase();
        query="SELECT * from Question where survey_id = "+ surveyId+ " and active = 2 and language_id = " +  languageId;
        Cursor cursor=sqldb.rawQuery(query,null);
        Logger.logD(TAG,"Query to check the language id present in question table" + query);
        isExist = cursor.getCount() > 0;
        close();
        return isExist;
    }

    /**
     * method to check language id is available for selected data colletion form in LanguageQuestion table
     * @param surveyId
     * @param languageId
     * @return
     */
    public boolean checkRegionalLanguageExist(int surveyId, int languageId) {
        boolean isExist = false;
        String query="";
        SQLiteDatabase sqldb = openDataBase();
        List<String> getQuestionId= getQuestionIdFromQuestion(surveyId);
        for(int i=0;i<getQuestionId.size();i++){
            query="SELECT * from LanguageQuestion where language_id = " +  languageId + "  and  question_pid = "+ getQuestionId.get(i);
            Cursor cursor=sqldb.rawQuery(query,null);
            if(cursor.getCount()<=0){
                isExist=false;
            }else{
                isExist=true;
                break;
            }
        }

        return isExist;
    }

    /**
     * method to get the list of question ids for selected survyee id
     * @param surveyId
     * @return
     */
    private List<String> getQuestionIdFromQuestion(int surveyId) {
        List<String>getAllQids=new ArrayList<>();
        String query="";
        SQLiteDatabase sqldb = openDataBase();
        query="SELECT id from Question where survey_id =" + surveyId + " and active = 2";
        Cursor cursor=sqldb.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do
                getAllQids.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
            while (cursor.moveToNext());
            cursor.close();
        }
        cursor.close();
        return getAllQids;
    }

    /**
     * method returns the string answer for particular question id from Question table
     * @param questionID
     * @return
     */
    public String getQuestionType(String questionID) {
        String questionType="";
        try {
            SQLiteDatabase sqldb = openDataBase();
            String query = "SELECT id,answer FROM Question where id="+questionID;
            Cursor cursor = sqldb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do questionType = cursor.getString(cursor.getColumnIndex("answer"));
                while (cursor.moveToNext());
                cursor.close();

            }
            cursor.close();
        } catch (SQLException e) {
            Logger.logE("Exception","in",e);
        }

        return questionType;
}

    /**
     * method to get the last modified date in RegionalLanguage table
     * @param table
     * @param updated_time
     * @return
     */
    public String getregionallLanguageUpdates(String table, String updated_time) {
        String date = "";
        String selectQuery = SELECT_MAX + updated_time + asStr +updated_time +FROM_TABLE + table;
        Logger.logD("current date",currentDateStr+ selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(updated_time));
            cursor.close();
        }
        if(date==null)
            date="";
        db.close();
        return date;
    }

    /**
     * method to fill the langauge table based on json response from language list api
     * @param result
     */
    public void UpdateRegionalLanguageTodatabase(String result) {
        try{
            Gson gson= new Gson();
            RegionalLanguage regionnalLanguage= gson.fromJson(result,RegionalLanguage.class);
            String languageStr = "Language";
            if (regionnalLanguage.getStatus()==2){
                ContentValues cv=new ContentValues();
                database=this.getWritableDatabase();

                for(int i=0;i<regionnalLanguage.getRegionalLanguage().size();i++)
                {
                    cv.put("id",regionnalLanguage.getRegionalLanguage().get(i).getId());
                    cv.put("language_code",regionnalLanguage.getRegionalLanguage().get(i).getLanguageCode());
                    cv.put("language_name",regionnalLanguage.getRegionalLanguage().get(i).getLanguageName());
                    cv.put(updatedTimeStr,regionnalLanguage.getRegionalLanguage().get(i).getUpdatedDated());
                    cv.put(activeStr,regionnalLanguage.getRegionalLanguage().get(i).getActive());
                    Long insertedPID= database.insertWithOnConflict(languageStr, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    Logger.logV(languageStr,"Regional Language  Saved Successfully " + cv.toString());
                    Logger.logV(languageStr,"Regional Language  p_ID are " + insertedPID);
                }
            }else{
                Logger.logV(languageStr,"Regional Language " + regionnalLanguage.getMessage());
            }
            database.close();
        }catch(Exception e){
            Logger.logE("Exception","in",e);
        }
    }

    /**
     * method to delete selected table when partner login changes
     */
    public void deleteAllRecords() {
        String query;
        database=this.getWritableDatabase();
        query="DELETE FROM Language";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for language"+query);
        query="DELETE FROM LanguageQuestion";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for languageQuestion"+query);
        query="DELETE FROM LanguageOptions";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for languageoptions"+query);
        query="DELETE FROM LanguageLabels";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for languagelabels"+query);
        query="DELETE FROM LanguageAssessment";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for languageassessmnet"+query);
        query="DELETE FROM LanguageBlock";
        database.execSQL(query);
        Logger.logD(TAG,"deleted  query for languageblock"+query);
    }

    public String getQuestionFromDb(String qid,int surveyId) {
        String question="";
        String pendingSurveyQuery = "select * from Question where id = " + qid + " and survey_id = " + surveyId;
        Cursor cursor=null;
        try {

            try {
                SQLiteDatabase database = openDataBase();
                cursor=database.rawQuery(pendingSurveyQuery,null);
                Logger.logD("blockquery","Get All Questions from Response Table" + pendingSurveyQuery);
                if(cursor.getCount()>0 && cursor.moveToFirst()){
                    question=cursor.getString(cursor.getColumnIndex("question_text"));
                }else{
                    question="";
                }
            }catch (Exception e){
                Logger.logE("Exception","getting questions based on blocks" , e);
            }finally {
                if(cursor!=null)
                    cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return question;
    }

    public String getAnswer(String qid,String ans_code,String surveyId ) {
        String question="";
        String pendingSurveyQuery = "select * from Options where question_pid = "+qid+" and id="+ans_code;
        Cursor cursor=null;
        try {
            SQLiteDatabase database = openDataBase();
            cursor=database.rawQuery(pendingSurveyQuery,null);
            Logger.logD("blockquery","Get All Questions from Response Table" + pendingSurveyQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                question=cursor.getString(cursor.getColumnIndex("option_text"));
            }else{
                question="";
            }
        }catch (Exception e){
            Logger.logE("Exception","getting questions based on blocks" , e);
        }finally {
            if(cursor!=null)
                cursor.close();
        }
        return question;

    }
}
