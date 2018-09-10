package org.fwwb.convene.convenecode.utils;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.fwwb.convene.convenecode.BeenClass.AssesmentBean;
import org.fwwb.convene.convenecode.BeenClass.Page;
import org.fwwb.convene.convenecode.BeenClass.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Constants {
    public static final String EDITSECONDARY_ADDRESS_FLAG = "editSecondaryAddress";
    public static final String HTMLSTRING = "<HTML>";
    public static final String ISADDITIONALADDRESS_FLAG = "isAddAdditionalAddress";
    public static final String CONVENE_USER_TABLE = "UserDetailsTable";
    public static final String CONVENE_USER_ID = "userId";
    public static final String CONVENE_USER_NAME = "userName";
    public static final String CONVENE_MD5 = "mdKey";
    public static final String CONVENE_LOGIN_DATA = "loginData";
    public static final String SELECTEDLANGUAGE="selectedLanguage";
    public static final String UPDATEDAPKVERSION = "UPDATE_APK_VERSION";
    public static final String APPVERSION = "appVersion";
    public static final String SAVE_DATA = "SaveDate";
    public static final String RULE_ENGINE = "rule_engine";
    public static final String SELECT ="Select" ;
    public static final String PERIODICITY_FLAG = "periodicity_flag";
    public static final String PARTNER = "partner_name";
    public static final String CONTACT_NO = "contact_no";
    public static final String AGE ="age" ;
    public static final String GENDER = "gender";
    public static final String FACILITY_SUB_TYPE = "facility_subtype";
    public static final String BOUNDARY_LEVEL = "boundaryLevel";
    public static final String SELECTEDVALUE = "selectedValue";
    public static final String SELECTEDLANGUAGELABEL = "selectedLanguageLabel";
    public static final String DO_NOTHING = "do nothing";
    public static final String CREATED_DATE = "createdDate";
    public static final String DATE_FORMAT_YY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FALSE = "false";
    public static final String CONVENE_DB ="CONVENE_DB";
    public static final String P_LIMIT = "pLimit";
    public static final String HEADER_NAME = "headerName";
    public static final String SURVEY_ID_COLUMN = "surveyId";
    public static final String DB_NAMES = "Convene.sqlite";
    public static final String BENEFICIARY_TYPE ="beneficiary_type" ;
    public static final String BENEFICIARY_IDS ="beneficiary_ids" ;
    public static final String THEMATIC_AREA_NAME = "thematic_area";
    public static final String THEMATIC_AREA_ID="thematic_area_id";
    public static final String LAST_MODIFIED="last_modified";
    public static final String MODIFIED_DATE="modified_date";
    public static final String MODIFIED="modified";
    public static final String PARENT="parent";
    public static final String PARENT_ID="parent_id";
    public static final String PARTNER_ID="partner_id";
    public static final String LOCATION_ID="location_id";
    public static final String UUID="uuid";
    public static final String LOCATION_TYPE="location_type";
    public static final String BENEFICIARY_TABLENAME="Beneficiary";
    public static final String SERVICE_NAME="service_name";
    public static final String FACILITY_IDS ="facility_ids" ;
    public static final String STATUS ="status" ;
    public static final String PARENT_UUID ="parent_uuid" ;
    public static final String BENEFICIARY_ARRAY ="beneficiaryArray" ;
    public static final String DOB = "dob";
    public static final String DOB_OPTION_KEY = "dob_option";
    public static final String YYMMDD = "yyyy-MM-dd";
    public static final String NO_INTERNET = "Please check Internet Connection";
    public static final String SURVEYSTATUSTYPR = "SurveyResponseType";
    public static final String YEARLY = "Yearly";
    public static final String GROUP = "Groups" ;
    public static final String CLUSTER = "Cluster" ;
    public static final String FPO = "Federations";
    public static final String HOUSEHOLDS = "Households";
    public static final String FARMERS = "Citizens";
    public static final String BATCH = "Batch";
    public static final String NAME = "Name";
    public static final String TAG = "Exception facing";
    public static final String FILTER = "filter";
    public static final String PROJECTFLOW = "ProjectFlow";
    public static final String O_LABLES = "lables";
    public static final String SELECTEDPROJECTID = "selectedActivity";
    public static final String ADDBUTTON = "addButton";
    public static final String BENEFICIARY_TOOLBAR_NAME = "beneficiary_toolbar_name";
    public static final String DESCRIPTION = "Description";
    public static final String BENEFICIARY_TOOLBAR_CLUSTERNAME = "beneficiary_Tool_ClusterName";
    public static final String SHOWTRAININGMODULEFLAG = "showTrainingModuleFlag";
    public static final String SHOWACTIVITYMODULEFLAG = "showActivityModuleFlag";
    public static final String SHOWPERIODICITYFLAG = "showPeriocityFlag";
    public static final String SURVEY_NAME_HOME = "SurveyNameFromHome";
    public static final String SURVEY_ID_HOME = "SurveyIdFromHome";
    public static List<Integer> blockQids=new ArrayList<>();

    //-------------------------*******************--------------------
    public static final int FONT_SMALL=0;
    public static final int FONT_MEDIUM=1;
    public static final int FONT_LARGE=2;
    public static double locationLatitude, locationLongitude;
    public static String lat_i = "0.0";
    public static  String lang_i = "0.0";



    /*======================================================================================*/
    public static final String OPTIONS_TABLE="Options";
    public static final String RESPONSE="Response";


    /*=====================================================================================*/
    /*************************QUESTION COLUMN NAME CONSTANTS*********************************/
    /*======================================================================================*/
    public static final String ANSWER_TYPE = "answer_type";
    public static final String SURVEY_ID = "survey_id";
    public static final String QUESTION_TEXT = "question_text";
    public static final String ACTIVE = "active";
    public static final String UPDATED_TIME = "updated_time";


    /*=====================================================================================*/
    /*************************OPTIONS COLUMN NAME CONSTANTS*********************************/
    /*======================================================================================*/
    public static final String QUESTION_PID = "question_pid";
    public static final String SKIP_CODE = "skip_code";
    public static final String VALIDATION = "validation";
    public static final String OPTION_TEXT = "option_text";
    public static final String ANS_TEXT = "ans_text";

    /*=====================================================================================*/
    /*************************ASSESSMENT COLUMN NAME CONSTANTS*********************************/
    /*======================================================================================*/
    public static final String ANS_FLAG = "option_flag";
    public static final String ANS_CODE = "option_code";

    //----------------------*************************-----------------------------------
    public static final String BENEFICIARY_NAME = "beneficiary_name" ;
    public static final String FACILITY_ID = "facility_id" ;
    public static final String BENEFICIARY_ID = "beneficiary_id" ;
    public static final String DBNAME = "SurveyLevel" ;
    public static final String TYPE_VALUE = "typeValue";
    public static final String BENEFICIARY_TYPE_ID="beneficiary_type_id";
    public static final String START_SURVEY_STATUS="start_survey_status";
    public static final String USER_ID="user_id";
    public static final String END_DATE="end_date";
    public static final String SURVEY_STATUS="survey_status";
    public static final String SYNC_STATUS="sync_status";
    public static final String SYNC_DATE="sync_date";
    public static final String SPECIMEN_ID="specimen_id";
    public static final String SURVEY_KEY="survey_key";
    public static final String LEVEL1_ID="level1_id";
    public static final String LEVEL2_ID="level2_id";
    public static final String LEVEL3_ID="level3_id";
    public static final String LEVEL4_ID="level4_id";
    public static final String LEVEL5_ID="level5_id";
    public static final String LEVEL6_ID="level6_id";
    public static final String LEVEL7_ID="level7_id";
    public static final String ADDRESS1="address1";
    public static final String ADDRESS2="address2";
    public static final String PINCODE="pincode";
    public static final String SURVEY_NAMe = "surveyName" ;
    public static final String FEATURE = "feature";
    public static final String PERIODICITY = "periodicity";
    public static final String LIMIT = "limit";
    public static final String LABEL = "label";
    public static final String VERSION = "version";
    public static final String CONFIG = "config" ;
    public static final String RD ="rd" ;
    public static final String O_LEAVEL = "o_leavel";
    public static final String CODE = "code";
    public static final String Q_CONFIGS = "q_configs";
    public static final String SERVER_PRIMARY = "server_primary";

    //---------------*********************----------------------------
    public static final String DB_NAME = "SurveyLevels.sqlite";
    public static final String ID = "id";
    public static final String language = "language";
    public static final String message = "message";
    public static final String QUESTION_VALIDATION = "question_validation";
    public static final String FACILITY_TYPE="Select facility type";
    public static final String CLUSTER_NAME="clusterName";
    public static final String FACILITY_TYPE_ID="facility_type_id";
    public static final String TYPE_NAME="typeName";

    public static final String SUB_TYPE="Select facility subtype";
    public static final String THEMATIC_AREA="Select thematic area";
    public static final String SELECT_GENDER="Select gender";
    public static final String ASSESSMENT = "Assessment";
    public static final String dateCaptureStr ="date_capture";



    public static HashMap<String,List<Page>> gridSubQuestionMapDialog= new HashMap<>();
    public static HashMap<String,List<AssesmentBean>> gridAssessmentMapDialog= new HashMap<>();
    public static HashMap<String,List<AssesmentBean>> mainGridAssessmentMapDialog= new HashMap<>();
    public static HashMap<String,Page> gridQuestionMapDialog= new HashMap<>();
    public static HashMap<String, List<LinearLayout>> gridRadioHashMap= new HashMap<>();
    public static HashMap<String,List<Response>> fillInlineRow= new HashMap<>();
    public static HashMap<String,List<String>> fillInlineHashMapKey= new HashMap<>();
    public static HashMap<String,List<Response>> GridResponseHashMap= new HashMap<>();
    public static HashMap<String,List<String>> GridResponseHashMapKeys= new HashMap<>();

    public static ArrayList<Button> bt= new ArrayList<>();
    public static ArrayList<Button> dateButton = new ArrayList<>();
    public static List<String> mainAcessmentList = new ArrayList<>();
    public static List<Integer> blockQidIntegers =new ArrayList<>();
    public static List<String> listHashMapKey= new ArrayList<>();

    public static HashMap<String,Button> buttonDynamicDateGrid = new HashMap<>();


    public static int rowInflater=0;
    public static final String LocationSurveyflag="LocationSurveyflag";
    public static final String constraints="constraints";
    public static final String Gender="Gender";
    public static final String FEMALE="Female";
    public static final String MALE="Male";

    public static Map<String ,Response> responselistBasedOnSkip = new HashMap<>();
    public static Map<String ,List<AssesmentBean>> assessmentListBasedOnSkip = new HashMap<>();
    public static Map<String ,List<View>> skipPageView= new HashMap<>();

    public static List<String> notTaggedQids= new ArrayList<>();
    public static int skipPageCount=0;

    private Constants(){
        // not using this constructor
    }
}
