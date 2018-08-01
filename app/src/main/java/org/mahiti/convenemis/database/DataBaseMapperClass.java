package org.mahiti.convenemis.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.AnswersPage;
import org.mahiti.convenemis.BeenClass.AssesmentBean;
import org.mahiti.convenemis.BeenClass.Page;
import org.mahiti.convenemis.BeenClass.Response;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.beansClassSetQuestion.Option;
import org.mahiti.convenemis.beansClassSetQuestion.RuleEngine;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;
import org.mahiti.convenemis.utils.RestUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class DataBaseMapperClass {
    private static final String TAG="DataBaseMapperClass";
    private static final String ORDER_BY_OPTION_ORDER=" ORDER BY a.option_order";
    private static final String ANSWERFORRADIO="answerForRadio";
    private static final String SELECT_FROM="SELECT * from ";
    private static final String ASSESSMENTS = "assessment";
    private static String cursorCountStr = "CursoreCount : ";
    private static String qBlockRefStr = "getting questions based on blocks";
    private static String andSurveyIdStr = " and survey_id=";
    private static String ansCodeStr = "ans_code";
    private static String ansTextStr = "ans_text";
    private static String questionQueryStr = "QuestionQuery";
    private static String primaryKeyStr = "primarykey";
    private static String gourpIdStr = "group_id";
    private static String primaryIdStr = "primary_id";
    private static String qTypeStr = "qtype";
    private static String selecOptionQuery = "select * from Options where assessment_pid=";
    private static String optionTextStra = "option_text";
    private static String assessmentPidStr = "assessment_pid";
    private static String selectOptionTextQuery = "select * from Options where option_text= '";
    private static String assessmentPidCondition = "'and assessment_pid=";

    private DataBaseMapperClass() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    /**
     * method to get the list of question ids from Question table based on the selected data collection form surveyid
     * @param database
     * @param surveyId
     * @param restUrl
     * @return
     */
    public static List<String> callDBForQuestionCode(SQLiteDatabase database, int surveyId, RestUrl restUrl) {  // getting Question Code from DB and Fill to Main List
        List<String> qCode=new ArrayList<>();
        String QuestionQuery="Select Question.id, Question.question_text from Question where Question.active = 2 and Question.sub_question = '0' and Question.survey_id = " + surveyId + " and  Question.id NOT IN (Select SkipData.question_id from SkipData, Question where SkipData.question_id=Question.id and Question.survey_id = " + surveyId + " ) order by question_code";
        Cursor cursor=null;
        int Count=0;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,"Get All Questions from Table" + QuestionQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    Logger.logV("SurveyQuestionActivity", cursorCountStr + cursor.getCount());
                    int qid = cursor.getInt(cursor.getColumnIndex("id"));
                    qCode.add(String.valueOf(qid));  // adding Qcode to MainList
                    Count=Count+1;
                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
            restUrl.writeToTextFile("Exception on getting all Question","","getAllQuestions");
        }
        return qCode;
    }


    /**
     * method to get the Question table count whether it is empty or not
     * @param surveyId
     * @param database
     * @return
     */
    public static int getQuestionCountFromDB(int surveyId, SQLiteDatabase database) {  // getting Question Code from DB and Fill to Main List
        String QuestionQuery="SELECT count (*) FROM Question where survey_id="+surveyId;
        Cursor cursor=null;
        int count=0;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            if (cursor.getCount()!=0){
                count =0;
            }else {
                count= 1;
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return count;
    }


    /**
     * method to get the list of questions id already answered from response table based on survey id
     * @param database
     * @param surveyId
     * @param restUrl
     * @return
     */
    public static List<String> getPendingAnsweredQuestionIds(net.sqlcipher.database.SQLiteDatabase database, String surveyId, RestUrl restUrl) {  // getting Question Code from DB and Fill to Main List
        List<String> answeredList=new ArrayList<>();
        String pendingSurveyQuery = "select * from Response where survey_id = '" + surveyId + "' order by q_id";
        Cursor cursor=null;
        String questionId;
        try {
            cursor=database.rawQuery(pendingSurveyQuery,null);
            Logger.logD(TAG,"Get All Questions from Response Table" + pendingSurveyQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    questionId = cursor.getString(cursor.getColumnIndex("q_id"));
                    answeredList.add(String.valueOf(questionId));  // adding qids  to Answered List

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
            restUrl.writeToTextFile("Exception on getting the question ids from response","","getQidsFromResponse");
        }
        return answeredList;
    }

    /**
     * method to get the answers of radio questions which is already answered and saved in Resonse table
     * @param questionNumber
     * @param db
     * @param restUrl
     * @return
     */
    public static HashMap<String,AnswersPage> getUserAnsweredResponseFromDB(int questionNumber, net.sqlcipher.database.SQLiteDatabase db, String surveyPrimaryKeyId, RestUrl restUrl) {
        HashMap<String,AnswersPage> getStoredAnswer= new HashMap<>();
        List<AnswersPage> allAnswersList = new ArrayList<>();
        try {
            String responseQuery="SELECT * from Response where  q_code="+questionNumber+andSurveyIdStr+"'"+surveyPrimaryKeyId+"'";
            Cursor questionCursor = db.rawQuery(responseQuery, null);
            if (questionCursor.moveToFirst()) {
                do {
                    String ansCode = questionCursor.getString(questionCursor.getColumnIndex(ansCodeStr));
                    String answer = questionCursor.getString(questionCursor.getColumnIndex(ansTextStr));
                    AnswersPage answerResponse= new AnswersPage(ansCode,answer,0,"",questionCursor.getInt(questionCursor.getColumnIndex(ansCodeStr)),0,0,"","");
                    allAnswersList.add(answerResponse);
                } while (questionCursor.moveToNext());

                if (!allAnswersList.isEmpty()){
                    AnswersPage AP=allAnswersList.get(allAnswersList.size()-1);
                    getStoredAnswer.put(String.valueOf(questionNumber),AP);
                }

            }
            questionCursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting the answer from response",e);
            restUrl.writeToTextFile("Exception on getting the answer from response","","getAnswerFromResponse");
        }

        return getStoredAnswer;
    }

    /**
     * method to get answeress of checkbox question from response table based on survey primary id and qid
     * @param questionNumber
     * @param db
     * @param surveyid
     * @param restUrl
     * @return
     */
    public static HashMap<String,List<AnswersPage>> getUserCheckBOxAnsweredResponseFromDB(int questionNumber, net.sqlcipher.database.SQLiteDatabase db, String surveyid, RestUrl restUrl) {
        Cursor questionCursor=null;
        HashMap<String,List<AnswersPage>> getStoredAnswer= new HashMap<>();
        List<AnswersPage> allAnswersList = new ArrayList<>();
        String ResponseQuery=SELECT_FROM +Constants.RESPONSE  + " where  q_code ="+questionNumber +  " and survey_id = '" + surveyid + "'";
        Logger.logD("Response Fetch","query"+ResponseQuery);
        try {
            questionCursor = db.rawQuery(ResponseQuery, null);
            if (questionCursor.moveToFirst()) {
                do {
                    String ansCode = questionCursor.getString(questionCursor.getColumnIndex(ansCodeStr));
                    String answer = questionCursor.getString(questionCursor.getColumnIndex(Constants.ANS_TEXT));
                    AnswersPage answerResponse= new AnswersPage(ansCode,answer,0,"",questionNumber,0,0,"","");
                    allAnswersList.add(answerResponse);
                    getStoredAnswer.put(String.valueOf(questionNumber),allAnswersList);

                } while (questionCursor.moveToNext());

            }
            questionCursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception in getUserCheckBOxAnsweredResponseFromDB " , e);
            restUrl.writeToTextFile("Exception in getUserCheckBOxAnsweredResponseFromDB","","getCheckBocAnswers");
        }
        return getStoredAnswer;
    }


    /**
     * method to get the set/page of question and related columns which is to be displaying in data collection form
     * @param qcode
     * @param database
     * @param survey_id
     * @param language_id
     * @param mainQlist
     * @return
     */
    public static List<Page> getQuestionOnBlocks(int qcode, SQLiteDatabase database, int survey_id, int language_id, List<String> mainQlist) {
        Logger.logV(TAG, "the user selected language id : " + language_id);
        List<Page> pages=new ArrayList<>();
        String QuestionQuery;

        boolean gridOrNot= checkGridOrNotGrid(database,String.valueOf(qcode));
        boolean checkLanguage= checkLanguageAviliablity(database,String.valueOf(qcode),mainQlist,language_id);
        if (gridOrNot){
            if (language_id==1)
                QuestionQuery="SELECT DISTINCT Question.help_text, Question.id, Question.block_id, Question.question_code ,Question.question_id, Question.answer_type,Question.mandatory, Question.question_text, Question.validation from Question , Options  where Question.id="+qcode+" and Question.active = 2 and Question.survey_id= " + survey_id;
            else
                QuestionQuery="SELECT DISTINCT Question.help_text, Question.id, Question.block_id, Question.question_code,Question.question_id,  Question.answer_type,Question.mandatory, Question.validation, LanguageQuestion.question_text from Question , Options,LanguageQuestion  where Question.id="+qcode+" and Question.active = 2 and Question.survey_id="+survey_id+" and LanguageQuestion.question_pid=Question.id and LanguageQuestion.language_id="+language_id;
        }
        else{
            if (language_id==1)
                QuestionQuery="SELECT DISTINCT  Question.id,Question.validation,Question.location_levels,Question.question_id, Question.block_id, Question.help_text, Question.question_code, Question.answer_type,  Question.mandatory, Question.question_text, Question.validation from Question , Options  where Question.id="+qcode+" and Question.id=Options.question_pid and Question.survey_id =" + survey_id;
            else if(language_id!=1 && checkLanguage)
                QuestionQuery = "SELECT DISTINCT  Question.id,Question.location_levels,Question.question_id, Question.block_id, Question.help_text, Question.question_code, Question.answer_type,  Question.mandatory, LanguageQuestion.question_text, Question.validation from Question ,LanguageQuestion  where Question.id=" + qcode + " and  LanguageQuestion.question_pid=Question.id and LanguageQuestion.language_id=" + language_id + " and  Question.survey_id= " + survey_id;
            else
                QuestionQuery="SELECT DISTINCT  Question.id,Question.location_levels,Question.question_id, Question.block_id, Question.help_text, Question.question_code, Question.answer_type,  Question.mandatory, Question.question_text, Question.validation from Question , Options  where Question.id="+qcode+"  and Question.survey_id =" + survey_id;

        }
        Cursor cursor=null;
        int Count=0;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if (cursor.getCount()<=0){
                QuestionQuery="SELECT DISTINCT Question.help_text,Question.location_levels,Question.question_id, Question.id, Question.block_id, Question.question_code, Question.answer_type,  Question.mandatory, Question.question_text, Question.validation from Question , Options  where Question.id="+qcode+" and Question.active = 2 and  Question.survey_id =" + survey_id;
                cursor=database.rawQuery(QuestionQuery,null);
            }
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    Logger.logV(TAG, cursorCountStr + Count);
                    int questionType = cursor.getInt(cursor.getColumnIndex(Constants.ANSWER_TYPE));
                    int questionId = cursor.getInt(cursor.getColumnIndex("id"));
                    int questionCode = cursor.getInt(cursor.getColumnIndex("question_code"));
                    int blockId=cursor.getInt(cursor.getColumnIndex("block_id"));
                    String questionName = cursor.getString(cursor.getColumnIndex(Constants.QUESTION_TEXT));
                    int mandatory = cursor.getInt(cursor.getColumnIndex("mandatory"));
                    String validation=cursor.getString(cursor.getColumnIndex("validation"));
                    String tooltip = cursor.getString(cursor.getColumnIndex("help_text"));
                    String locationLevels = cursor.getString(cursor.getColumnIndex("help_text"));
                    String partnerID = String.valueOf(cursor.getInt(cursor.getColumnIndex("question_id")));
                    Logger.logV(TAG, "tooltip : " + tooltip);
                    Logger.logV(TAG, "questionName : " + questionName);
                    Logger.logV(TAG, "questionCode : " + questionCode);
                    Page page=new Page();
                    page.setQuestionId(questionType);
                    page.setQuestionNumber(questionId);
                    page.setAnswerType(questionCode);
                    page.setQuestion(questionName);
                    page.setAnswer("");
                    page.setMultiple_entry(false);
                    page.setAnswersList(null);
                    page.setBlockId(blockId);
                    page.setSubQuestion("");
                    page.setTypologyId("");
                    page.setHelpText("");
                    page.setToolTip(tooltip);
                    page.setMandatory(String.valueOf(mandatory));
                    page.setValidation(validation);
                    page.setLocationLevels(locationLevels);
                    page.setPartnerId(partnerID);
                    pages.add(page);
                    Count=Count+1;
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return pages;
    }


    /**
     * method to check whether the question text availbale in LanguageQuestion table or not
     * @param database
     * @param s
     * @param mainQlist
     * @param language_id
     * @return
     */
    private static boolean checkLanguageAviliablity(SQLiteDatabase database, String s,List<String> mainQlist, int language_id) {
        boolean checkLanguage=false;
        String QuestionQuery="select LanguageQuestion.question_text  from LanguageQuestion, Question where LanguageQuestion.language_id="+language_id+" and Question.id=LanguageQuestion.question_pid and  Question.id='"+s + "'";
        Cursor cursor=null;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,cursor.getCount()+"");
            if (cursor.moveToFirst()){
                String questionName = cursor.getString(cursor.getColumnIndex("question_text"));
                checkLanguage = !"".equals(questionName);
            }else{
                checkLanguage=false;
            }
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }finally {
            if(cursor!=null)
                cursor.close();
        }
        return checkLanguage;
    }


    /**
     * method to get the list of options for radio,checkbox and dropdowm questopn order by question order and language id
     * @param questionNumber
     * @param database
     * @param selectedLangauge
     * @return
     */
    public static List<AnswersPage> getAnswerFromDB(int questionNumber, SQLiteDatabase database, int selectedLangauge) {
        List<AnswersPage> storeAnswer= new ArrayList<>();
        String QuestionQuery="";
        if (selectedLangauge==1){
            QuestionQuery="SELECT "+ Constants.OPTION_TEXT + ", a.id from Options a, Question q where a.question_pid=q.id and q.id="+questionNumber+ORDER_BY_OPTION_ORDER;
        }else if(selectedLangauge!=1){
            QuestionQuery="SELECT LanguageOptions.option_text,Options.id from Options, Question,  LanguageOptions where Options.id=LanguageOptions.option_pid and   Question.id= LanguageOptions.question_pid and Question.id="+questionNumber+"  and LanguageOptions.language_id="+selectedLangauge+" order BY Options.option_order";
        }else{
            QuestionQuery="SELECT "+ Constants.OPTION_TEXT + " from Options a, Question q where a.question_pid=q.id and q.id="+questionNumber+ORDER_BY_OPTION_ORDER;
        }

        Cursor cursor=null;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,"SpinnerQuestionQuery" + QuestionQuery);
            if (cursor.getCount()<=0){
                QuestionQuery="SELECT q.option_text, a.id from Options a, Question q where a.question_pid=q.id and q.id="+questionNumber+" and active = 2 ORDER BY a.option_order";
                cursor=database.rawQuery(QuestionQuery,null);
            }
            AnswersPage answersPageDefault=new AnswersPage("",Constants.SELECT,0,"",0,0,0,"","");
            storeAnswer.add(answersPageDefault);
            if(cursor.moveToFirst()){
                do{
                    Logger.logD(TAG,ANSWERFORRADIO + cursor.getString(cursor.getColumnIndex(Constants.OPTION_TEXT)));
                    Logger.logD(TAG,ANSWERFORRADIO + cursor.getInt(cursor.getColumnIndex("id")));
                    AnswersPage answersPage= new AnswersPage("",cursor.getString(cursor.getColumnIndex(Constants.OPTION_TEXT)),0,"",cursor.getInt(cursor.getColumnIndex("id")),0,0,"","");
                    storeAnswer.add(answersPage);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return storeAnswer;
    }


    /**
     * method to get the option for radio questions based on language id and question id
     * @param questionNumber
     * @param database
     * @param restUrl
     * @param languageId
     * @return
     */
    public static HashMap<String,List<AnswersPage>> getAnswerFromDBnew(int questionNumber, SQLiteDatabase database, RestUrl restUrl, int languageId) {
        List<AnswersPage> storeAnswer= new ArrayList<>();
        String QuestionQuery="";
        HashMap<String,List<AnswersPage>> backtoSetWidget= new HashMap<>();
        Cursor cursor=null;
        if (languageId!=1)
            QuestionQuery="SELECT a.id,l.option_text from Options a, Question q, LanguageOptions l  where a.question_pid=q.id and q.id="+questionNumber+" and l.option_pid=a.id and l.language_id="+languageId+ORDER_BY_OPTION_ORDER;
        else
            QuestionQuery="SELECT a.id,option_text from Options a, Question q where a.question_pid=q.id and q.id="+questionNumber+ORDER_BY_OPTION_ORDER;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if(cursor.moveToFirst()){
                do{
                    Logger.logD(TAG,ANSWERFORRADIO + cursor.getString(cursor.getColumnIndex(Constants.OPTION_TEXT)));
                    String answer = cursor.getString(cursor.getColumnIndex(Constants.OPTION_TEXT));
                    int pid=cursor.getInt(cursor.getColumnIndex("id"));
                    AnswersPage answersPage= new AnswersPage(String.valueOf(pid),answer,0,"",0,0,0,"","");
                    storeAnswer.add(answersPage);
                    backtoSetWidget.put(String.valueOf(questionNumber),storeAnswer);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
            restUrl.writeToTextFile("Exception on getting answers details from Options","","getOptionDetails");
        }
        return backtoSetWidget;
    }


    /**
     * method to get thr answercode for radio,dropdown and checkbox question based on langauge id and question id
     * @param questionNumber
     * @param database
     * @param answer
     * @param selectedLangauge
     * @return
     */
    public static String getAnswerCode(String questionNumber, SQLiteDatabase database, String answer, int selectedLangauge) {
        int ansCode=0;
        String QuestionQuery="";
        if (selectedLangauge!=1)
            QuestionQuery="SELECT option_pid as id from LanguageOptions where question_pid="+questionNumber+"  and option_text= '"+answer+"'";
        else
            QuestionQuery="SELECT id from Options where question_pid="+questionNumber+"  and option_text= '"+answer+"'";        Cursor cursor=null;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    ansCode = cursor.getInt(cursor.getColumnIndex("id"));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return String.valueOf(ansCode);
    }

    /**
     * method to get the option primary id from option table based on the selected widget answercode
     * @param questionNumber
     * @param database
     * @param answerCode
     * @param answer
     * @return
     */
    public static String getAnswerPid(String questionNumber, SQLiteDatabase database, String answerCode, String answer) {
        int ansCode=0;
        String QuestionQuery="SELECT * from Options where question_pid="+questionNumber+"  and id="+answerCode;
        Cursor cursor=null;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    ansCode = cursor.getInt(cursor.getColumnIndex("id"));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return String.valueOf(ansCode);
    }
    /**
     * method to get the answercode from option table based on question id
     * @param questionNumber
     * @param database
     * @param answer
     * @return
     */
    public static String getImageAnswerCode(String questionNumber, SQLiteDatabase database, String answer) {
        String ansCode="";
        String QuestionQuery=SELECT_FROM+ Constants.OPTIONS_TABLE + "   where " + Constants.QUESTION_PID + " ="+questionNumber;
        Cursor cursor=null;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do{
                    ansCode = cursor.getString(cursor.getColumnIndex(Constants.ANS_CODE));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return ansCode;
    }

    /**
     *
     * @param questionCode
     * @param database
     *
     * @param restUrl
     * @return
     */
    public static List<AnswersPage> getAnswersForQuestionFromDB(String questionCode, SQLiteDatabase database, RestUrl restUrl) {
        ArrayList<AnswersPage> allAnswersList = new ArrayList<>();
        Logger.logD(TAG, "the Question ID"+questionCode);
        boolean gridOrNot= checkGridOrNotGrid(database,questionCode);
        if (gridOrNot){
            Logger.logD(TAG, "Question is grid type NO Options ");
            AnswersPage answerspage = new AnswersPage("", "", 0, "", 0, 0, 0, "", "");
            allAnswersList.add(answerspage);
            return allAnswersList;
        }else {
            Logger.logD(TAG, "getting grid Option");
            String answer;
            String validation;
            String answerQuery = SELECT_FROM + Constants.OPTIONS_TABLE + " where " + Constants.QUESTION_PID + "  =" + questionCode + " and language_id = 1";
            Logger.logV(TAG, "query : " + questionCode + answerQuery);
            Cursor questionCursor = database.rawQuery(answerQuery, null);
            try {
                if (questionCursor.getCount() == 0) {
                    AnswersPage answerspage = new AnswersPage("", "", 0, "", 0, 0, 0, "", "");
                    allAnswersList.add(answerspage);
                    questionCursor.close();
                    return allAnswersList;
                }
                questionCursor.moveToFirst();
                if (questionCursor.getCount() != 0 && !questionCursor.isAfterLast()) {
                    do {
                        String ansCode = questionCursor.getString(questionCursor.getColumnIndex(Constants.ANS_CODE));
                        int ansFlag = questionCursor.getInt(questionCursor.getColumnIndex(Constants.ANS_FLAG));
                        int id = questionCursor.getInt(questionCursor.getColumnIndex("id"));
                        answer = questionCursor.getString(questionCursor.getColumnIndex(Constants.OPTION_TEXT));
                        validation = questionCursor.getString(questionCursor.getColumnIndex(Constants.VALIDATION));
                        AnswersPage answerspage = new AnswersPage(ansCode, answer, ansFlag, validation, id, 0, 0, "", "");
                        allAnswersList.add(answerspage);
                    } while (questionCursor.moveToNext());
                }
                questionCursor.close();
            } catch (Exception e) {
                Logger.logE(TAG, "getAnswersForQuestionFromDB ", e);
                restUrl.writeToTextFile("Exception on getting the Answers details based on qid","","getAnswersBasedonQid");
            }
        }
        return allAnswersList;
    }

    /**
     * method to check whether it is normal or grid type of question from QUestion table
     * @param database
     * @param questionCode
     * @return
     */
    private static boolean checkGridOrNotGrid(SQLiteDatabase database, String questionCode) {
        boolean gridOptionCheck=false;
        Cursor cursor = null;
        String QuestionQuery;
        QuestionQuery="select *  from Question  where id="+questionCode;
        int Count=0;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            Logger.logD(TAG,questionQueryStr + QuestionQuery);
            if(cursor.moveToFirst()){
                do{
                    Logger.logV(TAG, cursorCountStr + Count);
                    int questionType = cursor.getInt(cursor.getColumnIndex(Constants.ANSWER_TYPE));
                    gridOptionCheck = questionType == 16 || questionType == 14;

                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            Logger.logE(TAG,qBlockRefStr , e);
        }
        return gridOptionCheck;
    }

    /**
     * method to delete the particular question from response table
     * @param q_id
     * @param db
     */
    public static void deletePreviousSetOfQuestion(String q_id, net.sqlcipher.database.SQLiteDatabase db, String surveyID) {
        String ResponseQuery="Delete from Response where survey_id='"+surveyID+"' and  q_id="+q_id;
        Logger.logV(TAG, "Response Deleted Query" + ResponseQuery);
        db.execSQL(ResponseQuery);
    }

    /**
     * method to check block id exist or not in Question table
     * @param database
     * @param survey_id
     * @return
     */
    public static boolean checkBlockFromSurvey(SQLiteDatabase database, int survey_id) {
        String checkBlockQuery="Select block_id from Question where survey_id = " + survey_id;
        Cursor cursor;
        boolean flag=false;
        try {
            cursor=database.rawQuery(checkBlockQuery,null);
            Logger.logD(TAG,"Block Query from Question" + checkBlockQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                do {
                    int blockId=cursor.getInt(cursor.getColumnIndex("block_id"));
                    flag = blockId != 0;
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on Checking Blockid in Question table" , e);
        }
        return flag;
    }


    /**
     * method to get list of question based on block
     * @param database
     * @param survey_id
     * @param pagesetCount
     * @param notin
     * @return
     */
    public static List<String> getQuestionsFromBlocks(SQLiteDatabase database, int survey_id,int pagesetCount ,  String notin ){
        String blockQuery="Select * from Block where active = 2 and survey_id = " + survey_id + " ORDER BY block_order ASC";
        Logger.logD(TAG, "blockQuery : "+blockQuery);
        Cursor blockCursor;
        List<String> blockQuestionIds=new ArrayList<>();
        Constants.blockQids.clear();

        try {
            blockCursor = database.rawQuery(blockQuery, null);
            Logger.logD(TAG, "Block Cursor Query with Count" + blockCursor.getCount());
            if (blockCursor.getCount() > 0 && blockCursor.moveToFirst()) {
                do {
                    int blockId = blockCursor.getInt(blockCursor.getColumnIndex("id"));
                    Constants.blockQids.add(blockId);
                } while (blockCursor.moveToNext());
                Logger.logV(TAG, "blockId : " + Constants.blockQids.toString());
                blockQuestionIds=getQuestionsBasedOnBlocks(database,survey_id,pagesetCount);
            }
            blockCursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting Blocks " , e);
        }
        return blockQuestionIds;
    }


    /**
     *method to get the list of question based on block id in question table
     * @param database
     * @param survey_id
     * @param pageSetCount
     * @return
     */
    public static List<String>getQuestionsBasedOnBlocks(SQLiteDatabase database, int survey_id,int pageSetCount){
        List<String> blockQids=new ArrayList<>();
        Cursor blockQuestionCursor;
        String blockQuestionQuery="Select * from Question where active = 2 and question_code NOT IN(97) and survey_id = " + survey_id + " and sub_question=='' and block_id = " + Constants.blockQids.get(pageSetCount)  ;
        blockQuestionCursor=database.rawQuery(blockQuestionQuery,null);
        if(blockQuestionCursor.getCount()>0 && blockQuestionCursor.moveToFirst()){
            do {
                int questionId = blockQuestionCursor.getInt(blockQuestionCursor.getColumnIndex("id"));
                blockQids.add(String.valueOf(questionId));
            }while (blockQuestionCursor.moveToNext());
        }
        blockQuestionCursor.close();
        return blockQids;
    }


    /**
     * method to get the list of questions based on block id from Question with refernce to block table
     * @param database
     * @param survey_id
     * @param restUrl
     * @return
     */
    public static List<String> getQuestionBasedOnBlocks(SQLiteDatabase database, int survey_id, RestUrl restUrl) {
        String blockQuery="Select * from Block where active = 2 and survey_id = " + survey_id;
        Cursor blockCursor;
        List<String> blockQids=new ArrayList<>();
        List<Integer> blockids=new ArrayList<>();
        int Count=0;
        Cursor blockQuestionCursor;
        try {
            blockCursor=database.rawQuery(blockQuery,null);
            Logger.logD(TAG,"Block Cursor Query with Count" + blockCursor.getCount());
            if(blockCursor.getCount()>0 && blockCursor.moveToFirst()){
                do{
                    int blockId = blockCursor.getInt(blockCursor.getColumnIndex("id"));
                    blockids.add(blockId);

                }while (blockCursor.moveToNext());
                Logger.logV(TAG, "blockId : " + blockids.toString());

            }
            blockCursor.close();
            for(int i=0;i<blockids.size();i++){
                String blockQuestionQuery="Select * from Question where active = 2 and question_code NOT IN(97) and sub_question='0' and survey_id = " + survey_id + " and block_id = " + blockids.get(i) ;
                blockQuestionCursor=database.rawQuery(blockQuestionQuery,null);
                if(blockQuestionCursor.getCount()>0 && blockQuestionCursor.moveToFirst()){
                    do {
                        int questionId = blockQuestionCursor.getInt(blockQuestionCursor.getColumnIndex("id"));
                        blockQids.add(String.valueOf(questionId));
                        Count=Count+1;
                    }while (blockQuestionCursor.moveToNext());

                }
                blockQuestionCursor.close();
            }
        }catch (Exception e){
            Logger.logE(TAG,"Exception on Checking Blockid in Question table" , e);
            restUrl.writeToTextFile("Exception on getting all Question","","getAllQuestions");
        }
        return blockQids;
    }


    /**
     * method to get list of questions based on selected blocks
     * @param blockId
     * @param database
     * @param survey_id
     * @return
     */
    public static List<String> getBlockQuestions(int blockId,SQLiteDatabase database,int survey_id){
        List<String>blockQIds=new ArrayList<>();
        Cursor blockQuestionCursor;
        int Count=0;
        String blockQuestionQuery="Select * from Question where active = 2 and question_code NOT IN(97) and sub_question='' and survey_id = " + survey_id + " and block_id = " + blockId ;
        blockQuestionCursor=database.rawQuery(blockQuestionQuery,null);
        Logger.logD(TAG,"Block Question Query " + blockQuestionQuery);
        Logger.logD(TAG,"Block Cursor Count" + blockQuestionCursor.getCount());

        if(blockQuestionCursor.getCount()>0 && blockQuestionCursor.moveToFirst()){
            do {
                int questionId = blockQuestionCursor.getInt(blockQuestionCursor.getColumnIndex("id"));
                blockQIds.add(String.valueOf(questionId));
                Count=Count+1;
            }while (blockQuestionCursor.moveToNext());

        }
        blockQuestionCursor.close();
        return blockQIds;
    }

    /**
     * method to get the block name based on survey id and language id
     * @param database
     * @param survey_id
     * @param blockId
     * @param language_id
     * @return
     */
    public static String getBlockName(SQLiteDatabase database, int survey_id, int blockId,int language_id) {
        String blockTitleQuery;
        if (language_id==1)
            blockTitleQuery="Select block_name from Block where survey_id = " + survey_id + " and active = 2 and id = " +  blockId;
        else if (language_id==2) {
            blockTitleQuery="Select LanguageBlock.block_name from LanguageBlock , Block where LanguageBlock.block_pid=Block.id and LanguageBlock.language_id="+language_id+" and  LanguageBlock.block_pid="+blockId;
        }
        else
            blockTitleQuery="Select block_name from Block where survey_id = " + survey_id + " and active = 2 and id = " +  blockId;
        Cursor cursor;
        String blockName = null;

        try {
            cursor=database.rawQuery(blockTitleQuery,null);
            Logger.logD(TAG,"Block name Query" +  blockTitleQuery);
            if(cursor.getCount()>0 && cursor.moveToFirst()){
                blockName=cursor.getString(cursor.getColumnIndex("block_name"));
            }else {
                blockName="";
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on Checking block name in Block table" , e);
        }
        return blockName;
    }





    /**
     * method to get the list of assessmnets from Assessment table based on question id
     * @param q_code
     * @param database
     * @param language_id
     * @param restUrl
     * @return
     */
    public static List<AssesmentBean> getAssessments(int q_code, SQLiteDatabase database, int language_id, RestUrl restUrl) {
        List<AssesmentBean> pages = new ArrayList<>();
        String query;

        if (language_id==1)
            query = "select * from Assessment where active=2 and  question_pid = "+q_code;
        else if (language_id==2)
            query = "select Assessment.id,LanguageAssessment.assessment,Assessment.qtype,Assessment.mandatory,Assessment.question_pid,Assessment.group_validation from LanguageAssessment, Assessment where Assessment.active=2 and question_pid= "+q_code+" and  LanguageAssessment.language_id="+language_id+" and  Assessment.id =LanguageAssessment.assessment_pid";
        else
            query = "select * from Assessment where active=2 and  question_pid = "+q_code;

        Cursor questionCursor = database.rawQuery(query, null);
        try {
            if (questionCursor.moveToFirst()) {
                do {
                    int   qid = questionCursor.getInt(questionCursor
                            .getColumnIndex("id"));
                    String assesment = questionCursor.getString(questionCursor
                            .getColumnIndex(ASSESSMENTS));
                    String assessmentType = questionCursor.getString(questionCursor
                            .getColumnIndex(qTypeStr));
                    int assesmentId = questionCursor.getInt(questionCursor
                            .getColumnIndex("question_pid"));
                    int mandatoryCode = questionCursor.getInt(questionCursor
                            .getColumnIndex("mandatory"));
                    String groupValidation=questionCursor.getString(questionCursor.getColumnIndex("group_validation"));
                    AssesmentBean bean=new AssesmentBean();
                    bean.setQid(qid);
                    bean.setAssessmentId(assesmentId);
                    bean.setAssessment(assesment);
                    bean.setMandatory(mandatoryCode);
                    bean.setQtype(assessmentType);
                    bean.setGroupValidation(groupValidation);
                    pages.add(bean);
                } while (questionCursor.moveToNext());
            }
            questionCursor.close();
        } catch (Exception e){
            Logger.logE(TAG,"Exception on getting all assessment",e);
            restUrl.writeToTextFile("Exception on getting all assessment","","getAllAssessment");
        }
        return pages;
    }


    /**
     * method to get the sub questions from Question table
     * @param questionNumber
     * @param database
     * @param language_id
     * @return
     */
    public static List<Page> getSubquestionNew(int questionNumber, SQLiteDatabase database, int language_id) {
        Page page = null;
        List<Page> pages = new ArrayList<>();
        String query;
        if (language_id==1)
            query = "select * from Question where  sub_question= "+questionNumber+" order by sub_question ASC";
        else query = "select  Question.id, LanguageQuestion.question_text, Question.question_code, Question.answer from LanguageQuestion , Question where  Question.sub_question="+questionNumber+" and LanguageQuestion.question_pid=Question.id and LanguageQuestion.language_id="+language_id;
        Cursor questionCursor = database.rawQuery(query, null);
        try {
            if (questionCursor.moveToFirst()) {

                do {
                    int  currentPageId = questionCursor.getInt(questionCursor
                            .getColumnIndex(String.valueOf("id")));
                    int main_qid = questionCursor.getInt(questionCursor
                            .getColumnIndex(String.valueOf("question_code")));
                    String subQuestioncode = questionCursor.getString(questionCursor.getColumnIndex("question_text"));
                    String questionType = questionCursor.getString(questionCursor.getColumnIndex("answer"));
                    page = new Page(currentPageId, main_qid,
                            0, "",questionType , false,
                            null, 0, subQuestioncode, "", "", "","1","");
                    pages.add(page);
                } while (questionCursor.moveToNext());
            }
            questionCursor.close();
        } catch (Exception e){
            Logger.logE(TAG,"Exception on getting All SubQuestion",e);
          //  restUrl.writeToTextFile("Exception on getting All SubQuestion","","getAllSubQUestion");
        }
        return pages;
    }

    /**
     * method to get the option text,assessmnet pid nad id from Options table
     * @param qid
     * @param database
     * @param selectedLangauge
     * @return
     */
    public static List<AnswersPage> getOptionsAnswers(int qid, SQLiteDatabase database, int selectedLangauge) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery="";
        try{
            if (selectedLangauge==1)
                selectQuery = selecOptionQuery+qid;
            else selectQuery = "select Options.id,Options.question_pid,Options.option_code,LanguageOptions.option_text,Options.assessment_pid from LanguageOptions , Options where LanguageOptions.option_pid=Options.id and LanguageOptions.language_id="+selectedLangauge+" and  Options.assessment_pid="+qid;
            Cursor cursor = database.rawQuery(selectQuery, null);
            list.clear();
            if (cursor.getCount()<=0){
                selectQuery = selecOptionQuery+qid;
                cursor = database.rawQuery(selectQuery, null);
            }
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String optionText = cursor.getString(cursor.getColumnIndex(optionTextStra));
                    String assessmentID = cursor.getString(cursor.getColumnIndex(assessmentPidStr));

                    AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);
                    list.add(answer);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting options for grid question based on assessmentpid" ,e );
        }
        return list;
    }

    public static List<AnswersPage> getOptionsAnswersForSubquestionBased(int qid, SQLiteDatabase database) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery = "select * from Options where question_pid ="+qid;
        Cursor cursor = database.rawQuery(selectQuery, null);
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String optionText = cursor.getString(cursor.getColumnIndex("option_text"));
                String assessmentID = cursor.getString(cursor.getColumnIndex("assessment_pid"));
                AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);
                list.add(answer);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;
    }

    /**
     * method to get thr radio answers for Grid QUestion from Response tablle
     * @param qid
     * @param db
     * @param s
     * @return
     */
    public static List<Response> setAnswersForGridRadio(int qid, net.sqlcipher.database.SQLiteDatabase db, String s) {
        List<Response> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM Response where group_id="+qid+andSurveyIdStr+s;
        Cursor cursor = db.rawQuery(selectQuery, null);
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                String qId = cursor.getString(cursor.getColumnIndex("q_id"));
                String answer_ans_code = cursor.getString(cursor.getColumnIndex(ansCodeStr));
                String answer = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                Response answersObject = new Response(qId, answer, answer_ans_code,
                        cursor.getString(cursor.getColumnIndex("sub_questionId")),
                        cursor.getInt(cursor.getColumnIndex("q_code")),
                        cursor.getInt(cursor.getColumnIndex(primaryKeyStr)),
                        cursor.getString(cursor.getColumnIndex("typology_code")),
                        cursor.getInt(cursor.getColumnIndex(gourpIdStr)),
                        cursor.getInt(cursor.getColumnIndex(primaryIdStr)),
                        cursor.getString(cursor.getColumnIndex(qTypeStr)));
                list.add(answersObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;

    }


    /**
     * method to get the all the widget answerd from response table
     * @param questionNumber
     * @param db
     * @param surveyPrimaryKeyId
     * @return
     */
    public static List<AnswersPage> getUserAnsweredResponseInline(int questionNumber, net.sqlcipher.database.SQLiteDatabase db, int surveyPrimaryKeyId) {
        List<AnswersPage> allAnswersList = new ArrayList<>();
        String ResponseQuery="SELECT * from Response where  q_code="+questionNumber+andSurveyIdStr+surveyPrimaryKeyId;
        Cursor questionCursor = db.rawQuery(ResponseQuery, null);
        if (questionCursor.moveToFirst()) {
            do {
                String ansCode = questionCursor.getString(questionCursor.getColumnIndex(ansCodeStr));
                String answer = questionCursor.getString(questionCursor.getColumnIndex(ansTextStr));
                String subQuestioncount = questionCursor.getString(questionCursor.getColumnIndex(primaryKeyStr));
                AnswersPage answerResponse= new AnswersPage(ansCode,answer,0,"",questionNumber,0,Integer.valueOf(subQuestioncount),"","");
                allAnswersList.add(answerResponse);
            } while (questionCursor.moveToNext());

            questionCursor.close();
        }
        questionCursor.close();
        return allAnswersList;
    }

    public static List<AnswersPage> getOptionsAnswersForGrid(int qid, SQLiteDatabase database, String ansTxt, int languageid) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery="";
        if (languageid==1) {
            selectQuery = "select * from Options where option_text= '" + ansTxt + "'and assessment_pid=" + qid;
        } else if (languageid!=1){
            selectQuery = "select Options.id,Options.question_pid,Options.option_code,Options.assessment_pid, LanguageOptions.option_text  from Options , LanguageOptions where LanguageOptions.option_text= '"+ansTxt+"' and LanguageOptions.option_pid= Options.id and Options.assessment_pid="+qid+" and LanguageOptions.language_id="+languageid;
        }else{
            selectQuery = "select * from Options where option_text= '"+ansTxt+"'and assessment_pid="+qid;
        }
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount()<=0){
            selectQuery = "select * from Options where option_text= '" + ansTxt + "'and assessment_pid=" + qid;
            cursor = database.rawQuery(selectQuery, null);
        }
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String optionText = cursor.getString(cursor.getColumnIndex("option_text"));
                String assessmentID = cursor.getString(cursor.getColumnIndex("assessment_pid"));
                AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);

                list.add(answer);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;
    }

    /**
     * method to get thr option answers of radio,checkbox and dropdown from Options
     * @param qid
     * @param database
     * @param ansTxt
     * @param isRadioText
     * @return
     */
    public static List<AnswersPage> getOptionsAnswersForGridSubQBased(int qid, SQLiteDatabase database, String ansTxt, boolean isRadioText) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery;
        try {
            if(isRadioText)
                selectQuery = selectOptionTextQuery+ansTxt+"'and question_pid ="+qid;
            else
                selectQuery = "select * from Options where question_pid ="+qid;
            Cursor cursor = database.rawQuery(selectQuery, null);
            list.clear();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String optionText = cursor.getString(cursor.getColumnIndex(optionTextStra));
                    String assessmentID = cursor.getString(cursor.getColumnIndex(assessmentPidStr));
                    AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);

                    list.add(answer);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"Exception on getting option answers for grid sub question based",e);
         //   restUrl.writeToTextFile("Exception on getting option answers for grid sub question based","","getOptionGridSubQuestionBased");
        }
        return list;
    }

    /**
     *
     * @param questionNumber
     * @param db
     * @param surveyPrimaryKey
     * @return
     */
    public static List<Integer> getRowCount(int questionNumber, net.sqlcipher.database.SQLiteDatabase db, String surveyPrimaryKey) {
        List<Integer> listPrimaryKey = new ArrayList<>();
        String selectQuery = "SELECT distinct primarykey FROM  Response where  survey_id='"+surveyPrimaryKey+"' and q_id="+questionNumber;

        Cursor cursor = db.rawQuery(selectQuery, null);
        listPrimaryKey.clear();
        if ( cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                int primarykey = cursor.getInt(cursor.getColumnIndex(primaryKeyStr));
                Logger.logD(TAG," the primarykey-->"+primarykey);
                listPrimaryKey.add(primarykey);
                Logger.logD(TAG,"the primarykey added to list, the size is ->"+listPrimaryKey.size());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listPrimaryKey;
    }


    /**
     * method to get thr primary id from ResponseDumo table
     * @param db
     * @param surveyPrimaryKeyId
     * @param q_id
     * @return
     */
    public static int getPrimaryID(net.sqlcipher.database.SQLiteDatabase db, String surveyPrimaryKeyId, String q_id) {

        String selectQuery = "SELECT _id FROM  ResponseDump where  survey_id='"+surveyPrimaryKeyId+"' and q_id="+q_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int primarykey=0;
        if (cursor.moveToFirst()) {
            do {
                primarykey = cursor.getInt(cursor.getColumnIndex("_id"));
                Logger.logD(TAG," the primarykey-->"+primarykey);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return primarykey;
    }


    /**
     * method to delete the response of particluar qid from Response table
     * @param q_id
     * @param db
     * @param surveyPrimaryKeyId
     */
    public static void deletePreviousSetOfResponseJsonDump(String q_id, net.sqlcipher.database.SQLiteDatabase db, String surveyPrimaryKeyId) {
        String ResponseQuery="Delete from ResponseDump where survey_id='"+surveyPrimaryKeyId+"' and  q_id="+q_id;
        db.execSQL(ResponseQuery);
    }

    /**
     *
     * @param qid
     * @param autoSyncDatabase
     * @param autoSyncSurveyID
     * @param qidValueArray
     * @return
     */
    public static JSONArray getJsonObject(int qid, net.sqlcipher.database.SQLiteDatabase autoSyncDatabase, String autoSyncSurveyID, JSONArray qidValueArray) {

        String selectQuery = "SELECT * FROM Response where q_id = "+qid+" and  survey_id='"+autoSyncSurveyID+"'";
        Cursor cursor = autoSyncDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject getTempObject= new JSONObject();
                String subQuestionId = cursor.getString(cursor.getColumnIndex(primaryKeyStr));
                String autoSyncAnswer = cursor.getString(cursor.getColumnIndex(ansTextStr));
                String qtype = cursor.getString(cursor.getColumnIndex(qTypeStr));
                int groupID= cursor.getInt(cursor.getColumnIndex(gourpIdStr));
                int primaryID= cursor.getInt(cursor.getColumnIndex(primaryIdStr));

                try {
                    if ("T".equalsIgnoreCase(qtype) || "C".equalsIgnoreCase(qtype) || "D".equalsIgnoreCase(qtype) || "".equalsIgnoreCase(qtype))
                        getTempObject.put(qtype+"_"+subQuestionId+"_"+groupID,autoSyncAnswer);
                    else
                        getTempObject.put(qtype+"_"+subQuestionId+"_"+groupID,String.valueOf(primaryID));
                    qidValueArray.put(getTempObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return qidValueArray;
    }

    private static List<String> getSubQuestionList(int qid, String autoSyncSurveyID, net.sqlcipher.database.SQLiteDatabase autoSyncDatabase) {
        List<String> getTempList= new ArrayList<>();
        String selectQuery = "SELECT primarykey FROM Response where q_id = "+qid+"  and  survey_id='"+autoSyncSurveyID+"' group by  primarykey";
        Cursor cursor = autoSyncDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String subQuestionId = cursor.getString(cursor.getColumnIndex(primaryKeyStr));
                getTempList.add(subQuestionId);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return getTempList;
    }

    /**
     * method to get thr validation equation expression from Option tabke based on question pid
     * @param currentQuestionNumber
     * @param database
     * @return
     */
    public static String getValidationExpression(int currentQuestionNumber, SQLiteDatabase database) {
        String selectQuery = "select * from Options where question_pid="+currentQuestionNumber;
        String validation="";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                validation = cursor.getString(cursor.getColumnIndex(Constants.VALIDATION));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return validation;
    }

    /**
     * method to get thr validation exression from Question based on question pid
     * @param currentQuestionNumber
     * @param database
     * @return
     */
    public static String getValidationExpressionFromQuestion(int currentQuestionNumber, SQLiteDatabase database) {
        String selectQuery = "select * from Question where id="+currentQuestionNumber;
        String validation="";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                validation = cursor.getString(cursor.getColumnIndex(Constants.VALIDATION));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return validation;

    }

    /**
     *method to getthr option answers from Option s based on assessmentpid
     * @param qid
     * @param database
     * @param selectedLangauge
     * @return
     */
    public static List<AnswersPage> getOptionsAnswersTextBox(int qid, SQLiteDatabase database, int selectedLangauge) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery="";
        try{
            selectQuery = selecOptionQuery+qid;
            Cursor cursor = database.rawQuery(selectQuery, null);
            list.clear();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String optionText = cursor.getString(cursor.getColumnIndex(optionTextStra));
                    String assessmentID = cursor.getString(cursor.getColumnIndex(assessmentPidStr));
                    AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);
                    list.add(answer);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"" ,e );
        }
        return list;
    }

    /**
     * method to getthr answer from QUestion table based on survey id
     * @param surveyDatabase
     * @param currentPageLastQuestionId
     * @param surveysId
     * @return
     */
    public static String getAnswerTypeFromDb(SQLiteDatabase surveyDatabase, String currentPageLastQuestionId, int surveysId) {
        String answerType="";
        try{
            String query="SELECT * From Question where id = " + currentPageLastQuestionId + " and survey_id = "+ surveysId;
            Cursor cursor=surveyDatabase.rawQuery(query,null);
            if(cursor.moveToFirst()){
                answerType=cursor.getString(cursor.getColumnIndex("answer"));
            }else{
                answerType="";
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE(TAG,"getting answertype from question table",e);
        }
        return answerType;
    }

    /**
     * method to get the list of languages from Language table
     * @param database
     * @param selectedId
     * @return
     */
    public static List<String> getRegionalLanguage(SQLiteDatabase database, int selectedId) {
        List<String> list = new ArrayList<>();
        String selectQuery="";
        try{
            selectQuery = "select * from language";
            if (selectedId != -1)
                selectQuery = "select * from language where language_code="+selectedId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            list.clear();
            if (cursor.moveToFirst()) {
                do {
                    int language_code = cursor.getInt(cursor.getColumnIndex("language_code"));
                    String language_name = cursor.getString(cursor.getColumnIndex("language_name"));
                    list.add(language_code+"@"+language_name);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Logger.logE("","" ,e );
        }
        return list;
    }

    public static int getQuestionCountfromDB(int surveyId, SQLiteDatabase database) {
        String QuestionQuery="SELECT count (*) FROM Question where survey_id="+surveyId;
        Cursor cursor=null;
        int count=0;
        try {
            cursor=database.rawQuery(QuestionQuery,null);
            if (cursor!=null){
                count =0;
            }else {
                count= 1;
            }
        }catch (Exception e){
            Logger.logE(TAG,"getting questions based on blocks" , e);
        }finally {
            if(cursor!=null)
                cursor.close();
        }
        return count;
    }

    public static List<LevelBeen> getBenificiaryParentDetails(net.sqlcipher.database.SQLiteDatabase db, String questionid) {
       List<LevelBeen> getBeneficiaryParentList= new ArrayList<>();
        String selectQuery = "SELECT Response.ans_text, Survey.uuid FROM Response INNER JOIN Survey ON Response.survey_id = Survey.uuid where Response.q_code="+questionid;
        LevelBeen levelBeenDefault= new LevelBeen();
        levelBeenDefault.setUuid("0");
        levelBeenDefault.setName("Select Household");
        getBeneficiaryParentList.add(levelBeenDefault);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject getTempObject= new JSONObject();
                String surveyid = cursor.getString(cursor.getColumnIndex("uuid"));
                String name = cursor.getString(cursor.getColumnIndex("ans_text"));
                LevelBeen levelBeen= new LevelBeen();
                levelBeen.setUuid(surveyid);
                levelBeen.setName(name);
                getBeneficiaryParentList.add(levelBeen);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return getBeneficiaryParentList;
    }

    public static int getStateResponse(String tableName, String surveyPrimaryKeyId, net.sqlcipher.database.SQLiteDatabase db) {
       int getLevelID=0;
        String selectQuery = "select "+tableName+" from Survey where  uuid='"+surveyPrimaryKeyId+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                getLevelID = cursor.getInt(cursor.getColumnIndex(tableName));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return getLevelID;
    }


    /**
     * @param q_code
     * @param database
     * @param language_id
     * @return
     */
    public static List<AssesmentBean> getAssesements(int q_code, SQLiteDatabase database, int language_id) {
        AssesmentBean bean=null;
        List<AssesmentBean> pages = new ArrayList<>();
        String query;
        if (language_id==1)
            query = "select * from Assessment where active=2 and  question_pid = "+q_code;
        else if (language_id!=1)
            query = "select Assessment.id,LanguageAssessment.assessment,Assessment.qtype,Assessment.mandatory,Assessment.question_pid,Assessment.group_validation " +
                    "from LanguageAssessment, Assessment where Assessment.active=2 and question_pid= "+q_code+" and  LanguageAssessment.language_id="+language_id+" and  Assessment.id =LanguageAssessment.assessment_pid";
        else
            query = "select * from Assessment where active=2 and  question_pid = "+q_code;

        Cursor questionCursor = database.rawQuery(query, null);
        try {
            if (questionCursor.moveToFirst()) {
                do {
                    int   qid = questionCursor.getInt(questionCursor
                            .getColumnIndex("id"));
                    String assesment = questionCursor.getString(questionCursor
                            .getColumnIndex(ASSESSMENTS));
                    String assessmentType = questionCursor.getString(questionCursor
                            .getColumnIndex("qtype"));
                    int assesmentId = questionCursor.getInt(questionCursor
                            .getColumnIndex("question_pid"));
                    int mandatoryCode = questionCursor.getInt(questionCursor
                            .getColumnIndex("mandatory"));
                    String groupValidation=questionCursor.getString(questionCursor.getColumnIndex("group_validation"));
                    bean=new AssesmentBean();
                    bean.setQid(qid);
                    bean.setAssessmentId(assesmentId);
                    bean.setAssessment(assesment);
                    bean.setMandatory(mandatoryCode);
                    bean.setQtype(assessmentType);
                    bean.setGroupValidation(groupValidation);
                    pages.add(bean);
                } while (questionCursor.moveToNext());
            }
            return pages;
        } finally {
            if (questionCursor != null) {
                questionCursor.close();
            }
        }
    }

    public static List<AnswersPage> getOptionsAnswersTEXTBOX(int qid, SQLiteDatabase database) {
        List<AnswersPage> list = new ArrayList<>();
        String selectQuery="";
        try{
            selectQuery = "select * from Options where assessment_pid="+qid;
            Cursor cursor = database.rawQuery(selectQuery, null);
            list.clear();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String optionText = cursor.getString(cursor.getColumnIndex("option_text"));
                    String assessmentID = cursor.getString(cursor.getColumnIndex("assessment_pid"));
                    AnswersPage answer= new AnswersPage(String.valueOf(id),optionText,0,"",id,0,0,"",assessmentID);
                    list.add(answer);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }catch (Exception e){
            Logger.logE("","" ,e );
        }
        return list;
    }

    public static List<Response> setAnswersForGrid(int questionNumber, net.sqlcipher.database.SQLiteDatabase db, String survey_id) {
        List<Response> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM Response where group_id IN(select group_id from Response where survey_id='"+survey_id+"' and q_id = '" + questionNumber + "' ) and survey_id='"+survey_id+"' and q_id = '" + questionNumber + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        list.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String Qid = cursor.getString(cursor.getColumnIndex("q_id"));
                String answerAnsCode = cursor.getString(cursor.getColumnIndex("ans_code"));
                String answer = cursor.getString(cursor.getColumnIndex(PreferenceConstants.ANS_TEXT));
                Response answersObject = new Response(Qid, answer, answerAnsCode,
                        cursor.getString(cursor.getColumnIndex("sub_questionId")),
                        cursor.getInt(cursor.getColumnIndex("q_code")),
                        cursor.getInt(cursor.getColumnIndex("primarykey")),
                        cursor.getString(cursor.getColumnIndex("typology_code")),
                        cursor.getInt(cursor.getColumnIndex("group_id")),
                        cursor.getInt(cursor.getColumnIndex("primary_id")),
                        cursor.getString(cursor.getColumnIndex("qtype")));
                list.add(answersObject);
                Logger.logD(ASSESSMENTS,"answer - "+answer +" - qid "+Qid +" gId - "+cursor.getInt(cursor.getColumnIndex("group_id")));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return list;
    }

    public static String getTestQuestionAnswerCode(String currentPageLastQuestionId, SQLiteDatabase surveyDatabase,
                                                   String userEnterText) {
        String answerType = "";
        List<Option> listRuleEnginOption= new ArrayList<>();
        try {
            String query = "select * from Options where  Options.question_pid=" + currentPageLastQuestionId;
            Cursor cursor = surveyDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                  int  id = cursor.getInt(cursor.getColumnIndex("id"));
                  int  question_pid = cursor.getInt(cursor.getColumnIndex("question_pid"));
                  String  skip_code = cursor.getString(cursor.getColumnIndex("skip_code"));
                  String  rule_engin = cursor.getString(cursor.getColumnIndex("rule_engin"));
                    Option option= new Option();
                    option.setId(id);
                    option.setQuestionPid(question_pid);
                    option.setSkipCode(skip_code);
                    List<RuleEngine> getList= new ArrayList<>();
                    boolean isRuleEnginValid= false;
                    try {
                        isRuleEnginValid = bindRuleEnginFrmString(rule_engin,getList,Integer.parseInt(userEnterText));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (isRuleEnginValid)
                      return String.valueOf(id);
                  else
                      answerType=String.valueOf(id);
                    option.setRuleEngine(getList);
                    listRuleEnginOption.add(option);

                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE(TAG, "getting answertype from question table", e);
        }
        return answerType;
    }

    private static boolean bindRuleEnginFrmString(String rule_engin, List<RuleEngine> getList, int userEnterText) {
        if (!rule_engin.isEmpty()){
            try {
                RuleEngine ruleEngine= new RuleEngine();
                JSONArray jsonArray = new JSONArray(rule_engin);
                List<Boolean> validateList= new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject= jsonArray.getJSONObject(i);
                    ruleEngine.setDataType(jsonObject.getString("data_type"));
                    ruleEngine.setOperator(jsonObject.getString("operator"));
                    ruleEngine.setQuestionId(jsonObject.getInt("question_id"));
                    ruleEngine.setValue(jsonObject.getString("value"));
                    validateList.add(setRuleEnginScane(ruleEngine.getValue(),ruleEngine.getOperator(),userEnterText));
                    getList.add(ruleEngine);
                }
                if (!validateList.contains(false))
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean setRuleEnginScane(String getRuleEnginValue,String operator, int userText) {
        switch (operator){
            case "<=":
                if (userText<=Integer.parseInt(getRuleEnginValue))
                    return true;
                break;
            case ">=":
                if (userText>=Integer.parseInt(getRuleEnginValue))
                    return true;
                break;
            case "<":
                break;
            case ">":
                if (userText>Integer.parseInt(getRuleEnginValue))
                    return true;
                break;
            case "==":
                break;
            default:
                break;

        }
        return false;
    }

    public static String getResponseText(net.sqlcipher.database.SQLiteDatabase db, String surveyPrimaryKeyId,String questionID) {
        String getResponseText="";
        String selectQuery = "select ans_text from Response where Response.survey_id='"+surveyPrimaryKeyId+"' and Response.q_id="+questionID;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                getResponseText = cursor.getString(cursor.getColumnIndex("ans_text"));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return getResponseText;
    }
}
