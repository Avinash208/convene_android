package org.mahiti.convenemis.utils;

/**
 * Created by mahiti on 8/1/16.
 */
public class PreferenceConstants {

    public final static String typologyId = "typology_id"; // this is key is used in sharedPreferences for loading the questions and summary for a specific Survey or a Typology
    public final static String parentTypologyId = "parentTypologyId"; // this is key is used in sharedPreferences for getting parent SurveyId when the record having child survey's
    public final static String surveyIdPrimKey = "survey_id"; // this is key is used in sharedPreferences for inserting the data in database.

    public final static String surveyId = "SurveyId"; // this key is used to pass the survey primary id to SurveyQuestionActivity
    public final static String surveySkipFlag = "surveySkipValue"; // this key is used for understand that the new child survey has started (for updating db with additional values

    //SurveyQuestionsActivity class sharedPreferences
    public static final String SURVEY_ID = "survey_id";
    public static final String SURVEY_ID_HASH = "survey_id";
    public static final String ANS_TEXT = "ans_text";
    public static final String REASON_OFF_SURVEY = "reason_off_survey";
    public static final String RESUME_SURVEY = "resumeSurvey";
    public static final String SECTION_CHANGED = "SectionChanged";
    public static final String SURVEY_SKIP_VALUE = "surveySkipValue";
    public static final String SUB_CATEGORY_ID="sub_category_id";
    public static final String SUB_CATEGORY="sub_category";
    public static final String CLUSTER_ID="cluster_id";
    public static final String CLUSTER_CODE="cluster_code";
    public static final String CLUSTER_NAME="cluster_name";
    public static final String TOWN_NAME="town_name";


    private PreferenceConstants() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }
}
