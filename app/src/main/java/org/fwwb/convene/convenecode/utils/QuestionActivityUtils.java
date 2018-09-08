package org.fwwb.convene.convenecode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mahiti on 1/2/16.
 */
public class QuestionActivityUtils {
    private static final String MY_PREFS_NAME_SURVEY = "MyPrefs";
    private static final String TAG = "QuestionActivityUtils";

    private QuestionActivityUtils(){

    }

    /**
     *
     * @param mainArrayColon
     * @return
     */
    public static boolean logicalExp(String[] mainArrayColon) {

        List<String> logicalExp = new LinkedList<>(Arrays.asList(mainArrayColon));
        try {
            for (int j = logicalExp.size() - 1; j >= 2; j = j - 2) {
                if ("&&".equals(logicalExp.get(j - 1))) {
                    checkLogicalAndExpression(logicalExp,j);
                } else if ("||".equals(mainArrayColon[j - 1])) {
                    checkLogicalOrExpression(logicalExp,j,mainArrayColon);
                }
            }
        }
        catch(Exception e){
            Logger.logE(TAG,"logicalExp",e);
        }
        return Boolean.parseBoolean(logicalExp.get(0));
    }

    /**
     *
     * @param logicalExp
     * @param j
     * @param mainArrayColon
     */
    private static void checkLogicalOrExpression(List<String> logicalExp, int j, String[] mainArrayColon) {
        if (Boolean.parseBoolean(mainArrayColon[j]) || Boolean.parseBoolean(mainArrayColon[j - 2])) {
            logicalExp.remove(j);
            logicalExp.remove(j - 1);
            logicalExp.remove(j - 2);
            logicalExp.add(String.valueOf(true));
        } else {
            logicalExp.remove(j);
            logicalExp.remove(j - 1);
            logicalExp.remove(j - 2);
            logicalExp.add(String.valueOf(false));
        }
    }

    /**
     *
     * @param logicalExp
     * @param j
     */
    private static void checkLogicalAndExpression(List<String> logicalExp, int j) {
        if (Boolean.parseBoolean(logicalExp.get(j)) && Boolean.parseBoolean(logicalExp.get(j - 2))) {
            logicalExp.remove(j);
            logicalExp.remove(j - 1);
            logicalExp.remove(j - 2);
            logicalExp.add(String.valueOf(true));
        } else {
            logicalExp.remove(j);
            logicalExp.remove(j - 1);
            logicalExp.remove(j - 2);
            logicalExp.add(String.valueOf(false));
        }
    }

    /**
     *
     * @param localArray
     * @param ansCodeList
     * @return
     */
    public static boolean listCheck(String[] localArray, List<String> ansCodeList){
        if ("=".equals(localArray[2])) {
            for (int i = 0; i < ansCodeList.size(); i++) {
                Logger.logD(TAG, "parseby" + ansCodeList.get(i));
            }
            if (ansCodeList.contains(localArray[3])) {
                return true;
            }
        } else if ("!=".equals(localArray[2]) && !ansCodeList.contains(localArray[3])) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param count
     * @param mainQList
     * @param database
     * @param setCount
     * @param surveyid
     * @param restUrl
     * @param dbOpenHelper
     * @param notTaggedQuestionList
     * @return
     */
    public static List<String> getQuestionFromMainList(int count, List<String> mainQList, SQLiteDatabase database, int setCount, int surveyid, RestUrl restUrl,
                                                       ConveneDatabaseHelper dbOpenHelper, List<String> notTaggedQuestionList) {
        List<String> qList=new ArrayList<>();
        String questionQuery=null;
        qList.clear();
        Cursor questionCursor = null;
        String qValidation;
        for(int i=count;i<mainQList.size();i++){
            try {
                questionQuery="select skip_code from Options where question_pid= " + mainQList.get(i)+" AND skip_code !=''";
                questionCursor=database.rawQuery(questionQuery,null);
                if(questionCursor.getCount()>0 && questionCursor.moveToFirst()){
                    qList.add(mainQList.get(i));
                    break;
                } else{
                    qList.add(mainQList.get(i));
                   if (mainQList.size()-1<=i) {
                       for (int k = 0; k < notTaggedQuestionList.size(); k++) {
                           if (Integer.parseInt(mainQList.get(i)) < Integer.parseInt(notTaggedQuestionList.get(k))) {
                               if (checkSkipCodeAgainstQuestionID(notTaggedQuestionList.get(k), database))
                                   if (!qList.contains(notTaggedQuestionList.get(k)))
                                         qList.add(notTaggedQuestionList.get(k));
                               else
                                   if (!qList.contains(notTaggedQuestionList.get(k)))
                                        qList.add(notTaggedQuestionList.get(k));
                               break;
                           }
                       }
                   }
                    SQLiteDatabase  surveyDatabase = dbOpenHelper.getWritableDatabase();
                    qValidation = QuestionActivityUtils.questionSkipLogic(mainQList.get(i), surveyDatabase);
                    if(!("").equals(qValidation)){
                        break;
                    }
                }
                questionCursor.close();
            }catch (Exception e){
                Logger.logE(TAG,"Exception in getting Qids" , e);
                restUrl.writeToTextFile("Exception on displaying question in Page","","gettingQuestionSetInPage");
            }
        }
        Logger.logV(TAG,"qListValues --->" + qList.toString());
        return qList;
    }

    private static boolean checkSkipCodeAgainstQuestionID(String questionId, SQLiteDatabase database) {
        String selectQuery = "select * from Options where  Options.question_pid=" + questionId;
        Cursor questionCursor = database.rawQuery(selectQuery, null);
        if (questionCursor != null && questionCursor.getCount() > 0 && questionCursor.moveToFirst()) {
            do {
              String  skip = questionCursor.getString(questionCursor.getColumnIndex(Constants.SKIP_CODE));
              if (!skip.isEmpty())
                  return false;
            } while (questionCursor.moveToNext());

        }
        return true;
    }

    /**
     *
     * @param qid
     * @param database
     * @param ansCode
     * @return
     * Method to get the skipcode from Options Table
     */
    public static String checkSkipCode(String qid, SQLiteDatabase database, String ansCode) {
        String skip = "";
       if (!ansCode.isEmpty()){
           String selectQuery = "select skip_code from Options where id = "+ ansCode + " and question_pid= '" + qid + "'";
           Logger.logD(TAG,"skip selection query : "+selectQuery);
           Cursor questionCursor = database.rawQuery(selectQuery, null);

           try {
               if(questionCursor!=null && questionCursor.getCount()!=0 && questionCursor.moveToFirst()){
                   do{
                       skip = questionCursor.getString(questionCursor.getColumnIndex(Constants.SKIP_CODE));
                       Logger.logV(TAG, "the skip is" + skip);
                       if ("-1".equals(skip))
                           break;
                   }while (questionCursor.moveToNext());
                   questionCursor.close();
               }
               if (skip == null) {
                   skip = "";

               }
           } finally {
               if (questionCursor != null) {
                   questionCursor.close();
               }
           }
       }
        return skip;
    }

    /**
     * @param qid
     * @return
     * Method to get the question validation from Skip Mandatory
     */
    public static String questionSkipLogic(String qid, SQLiteDatabase database) {
        String questionSkipLogic=null;
        String query = "Select " + Constants.QUESTION_VALIDATION + " from SkipMandatory where question_pid = '" + qid + "'";
        Cursor questionCursor = database.rawQuery(query, null);
        try {
            if (questionCursor != null && questionCursor.moveToFirst()) {
                questionSkipLogic = questionCursor.getString(questionCursor.getColumnIndex(Constants.QUESTION_VALIDATION));
                questionCursor.close();
            } else {
                questionSkipLogic = "";
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception in skiplogic Validation " , e);
        }
        return questionSkipLogic;
    }

    /**
     * @param qid
     * @return list
     * Method to get the question validation from Skip Mandatory
     */
    public static List<String> questionValidationSkipLogic(String qid, SQLiteDatabase database) {
        List<String> qValidationList = new ArrayList<>();
        String qValidationQuery = "Select question_validation from SkipMandatory where question_pid = " + qid;
        Logger.logD(TAG, "Skip Mandatory Query " + qValidationQuery);
        Cursor cursor = database.rawQuery(qValidationQuery, null);
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                qValidationList.add(cursor.getString(cursor.getColumnIndex("question_validation")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return qValidationList;
    }

    /**
     *
     * @param expression1
     * @param qid
     * @param context
     * @param surveyPrimaryKeyId
     * @return
     */
    public static boolean skipValidation(String expression1, String qid, Context context, int surveyPrimaryKeyId) {
        boolean valid = false;
        String[] drugsSkipLogic = expression1.split("#");
        String[] drugsQuestion = drugsSkipLogic[0].split("\\$");
        for (int a1 = 0; a1 < drugsQuestion.length; a1++) {
            String expression = drugsQuestion[a1];
            if (expression.contains("@")) {
                String[] mainArrayWithSkip = expression.split("@");
                String[] mainArrayComa = mainArrayWithSkip[0].split(",");
                for (int i = 0; i < mainArrayComa.length; i = i + 2) {
                    valid = colonBy(mainArrayComa[i],context,surveyPrimaryKeyId,qid);
                    mainArrayComa[i] = String.valueOf(valid);
                    Logger.logD(TAG,"mainArrayComa[i] " + mainArrayComa[i]);
                }
                if (mainArrayComa.length > 1) {
                    valid = QuestionActivityUtils.logicalExp(mainArrayComa);
                } else {
                    valid = Boolean.parseBoolean(mainArrayComa[0]);
                }
            }
        }
        return valid;
    }

    /**
     * @param expr

     * @param surveyPrimaryKeyId
     * @param qid
     * @return
     */
    public static  boolean colonBy(String expr, Context context, int surveyPrimaryKeyId, String qid) {
        boolean validation = false;
        String[] mainArrayColon = expr.split(":");
        for (int j = 0; j < mainArrayColon.length; j = j + 2) {
            validation = parseBy(mainArrayColon[j],context,surveyPrimaryKeyId,qid);
            mainArrayColon[j] = String.valueOf(validation);
        }
        if (mainArrayColon.length > 1) {
            validation = QuestionActivityUtils.logicalExp(mainArrayColon);
        } else {
            validation = Boolean.parseBoolean(mainArrayColon[0]);
        }
        return validation;
    }

    /**
     *
     * @param exp
     * @param context
     * @param surveyPrimaryKeyId
     * @param qid
     * @return
     */
    public static boolean   parseBy(String exp, Context context, int surveyPrimaryKeyId, String qid) {
        boolean checked = false;
        String ansCodeTemp;
        String queryPickData = "";
        String queryForKey;
        int currentPageId = 0;
        net.sqlcipher.database.SQLiteDatabase db = null;
        DBHandler handler;
        SharedPreferences preferences;
        ConveneDatabaseHelper dbOpenHelper;
        String[] localArray = new String[20];
        try {
            List<String> ansCodeList = new ArrayList();
            if (exp.contains(";")) {
                localArray = exp.split(";");

                 handler = new DBHandler(context);
                db = handler.getdatabaseinstance();
                 preferences= PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME_SURVEY, MODE_PRIVATE);
                 dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
                SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

                if ("0".equals(localArray[1])) {
                    queryForKey = "select  question_code,  id  from Question where survey_id = "+ prefs.getInt("survey_id",0) + " and id = " + qid;
                } else {
                    queryForKey = "select  question_code,  id  from Question where survey_id = "+ prefs.getInt("survey_id",0) + "  and id = " + qid;
                }
                Cursor questionCursor = database.rawQuery(queryForKey, null);
                if (questionCursor != null && questionCursor.moveToFirst()) {
                    currentPageId = questionCursor.getInt(questionCursor.getColumnIndex("id"));
                }

                // comparing the current question with the expression question
// change by charan

                    if ("0".equals(localArray[1])) {
                        queryPickData = "select ans_code from Response where q_code = '"
                                + localArray[0]
                                + "' and primarykey= '"
                                + surveyPrimaryKeyId + "'";
                    } else {
                        queryPickData = "select ans_code from Response where primarykey = '"
                                + currentPageId
                                + "' and sub_questionId = '"
                                + localArray[1]
                                + "' and survey_id = '"
                                + surveyPrimaryKeyId + "'";
                    }
                }
                if (!"".equals(queryPickData)) {

                    Cursor fromCursor = db.rawQuery(queryPickData, null);
                    if (fromCursor != null && fromCursor.moveToFirst()) {
                        do {
                            ansCodeTemp = fromCursor.getString(fromCursor.getColumnIndex("ans_code"));
                            ansCodeList.add(ansCodeTemp);
                        } while (fromCursor.moveToNext());
                        fromCursor.close();
                    }
                }
                // need to do the contains by comparing the ans_code array from expression
                if (localArray[3].contains("_")) {
                    String[] ansCodeExpr = localArray[3].split("_");
                    List<String> listFromExp = Arrays.asList(ansCodeExpr);
                    for (int i = 0; i < listFromExp.size(); i++) {
                        Logger.logV(QuestionActivityUtils.class.getSimpleName(), "list_frm_exp data " + listFromExp.get(i));
                    }
                    checked = false;
                    for (int k = 0; k < ansCodeList.size(); k++) {
                        if ("=".equals(localArray[2])) {
                            if (listFromExp.contains(ansCodeList.get(k).trim())) {
                                checked = true;
                                break;
                            }
                        } else if ("!=".equals(localArray[2])) {
                            if (!listFromExp.contains(ansCodeList.get(k).trim())) {
                                checked = true;
                                break;
                            }
                        } else if ("?".equals(localArray[2])) {
                            if (listFromExp.contains(ansCodeList.get(k).trim())) {
                                checked = false;
                                break;
                            } else {
                                checked = true;
                            }
                        } else if ("!?".equals(localArray[2])) {
                            if (listFromExp.contains(ansCodeList.get(k).trim())) {
                                checked = false;
                                break;
                            } else {
                                checked = true;
                            }
                        } else {
                            Logger.logD(QuestionActivityUtils.class.getSimpleName(), "else case in ----");
                        }
                    }

                } else {
                    checked = false;
                    checked = QuestionActivityUtils.listCheck(localArray, ansCodeList);
                    return checked;
                }

        } catch (Exception e) {
            Logger.logE(QuestionActivityUtils.class.getSimpleName(), "Exception in SurveyQuestionsActivity  Parseby method ", e);
        }
        return checked;
    }

    /**
     *
     * @param qid
     * @param database
     * @param restUrl
     * @return
     */
    public static String checkPendingSkipCode(String qid, SQLiteDatabase database, RestUrl restUrl) {
        String selectQuery = "select skip_code from Options where  question_pid= '" + qid + "'";
        Logger.logD(TAG,"skip selection query : "+selectQuery);
        Cursor questionCursor = database.rawQuery(selectQuery, null);
        String skip = null;
        try {
            questionCursor.moveToFirst();
            if (!questionCursor.isAfterLast()) {
                skip = questionCursor.getString(questionCursor.getColumnIndex(Constants.SKIP_CODE));
                Logger.logV(TAG, "the skip is" + skip);
            }
            if (skip == null) {
                skip = "";
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting the skipcode from options",e);
            restUrl.writeToTextFile("Exception on getting the skipcode from options","","gettingSkipCode");
        } finally {
            if (questionCursor != null) {
                questionCursor.close();
            }
        }
        return skip;
    }

    /**
     *
     * @param qType
     * @return
     */
    public static String getQuestionType(int qType){
        String questionType="";
        switch (qType){
            case  1:
                questionType = "T";
                break;
            case 4:
                questionType = "R";
                break;
            case  2:
                questionType = "C";
                break;
            case  5:
                questionType = "D";
                break;
            case  6:
                questionType = "S";
                break;
            case  9:
                questionType = "AW";
                break;
            case  10:
                questionType = "AI";
                break;
            default:
                questionType = "";
                break;
        }
        return questionType;
    }
}
