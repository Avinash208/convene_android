package org.assistindia.convene.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by mahiti on 14/1/16.
 */
public class LanguageUtils {
    private LanguageUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void setLabelsLanguage(SharedPreferences.Editor engLanguageEditor, Context context){
        if(engLanguageEditor==null)
            return;
        engLanguageEditor.putString(ClusterInfo.TRAININGLABLE, "Training");
        engLanguageEditor.putString("SELECTED_LANGUAGE","1");
        engLanguageEditor.putString(ClusterInfo.LIVE, "Live");
        engLanguageEditor.putString(ClusterInfo.USERNAME, "UserName");
        engLanguageEditor.putString(ClusterInfo.LOGINBUTTON, "Login");
        engLanguageEditor.putString(ClusterInfo.FORGETPASS, "Forgot Password");
        engLanguageEditor.putString(ClusterInfo.signin, "Signing in");
        engLanguageEditor.putString(ClusterInfo.Next, "Next");
        engLanguageEditor.putString(ClusterInfo.Previous, "Previous");
        engLanguageEditor.putString(ClusterInfo.done, "Done");
        engLanguageEditor.putString(ClusterInfo.Processing, "Processing");
        engLanguageEditor.putString(ClusterInfo.caseId, "Customer Id");
        engLanguageEditor.putString(ClusterInfo.PatientName, "Patient");
        engLanguageEditor.putString(ClusterInfo.SiteName, "Site Name");
        engLanguageEditor.putString(ClusterInfo.ANCName, "ANC Name");
        engLanguageEditor.putString(ClusterInfo.Facility_Nodal_name, "Facility");
        engLanguageEditor.putString(ClusterInfo.Add_New_FIF, "Add New FIF");
        engLanguageEditor.putString(ClusterInfo.Add_New_STR, "Add New STR");
        engLanguageEditor.putString(ClusterInfo.Add_New_ANC, "Add New ANC");
        engLanguageEditor.putString(ClusterInfo.Add_New_MSD, "Add New MSD");
        engLanguageEditor.putString(ClusterInfo.Select_State, "Select State");
        engLanguageEditor.putString(ClusterInfo.Select_District, "Select District");
        engLanguageEditor.putString(ClusterInfo.Select_Town, "Select Town");
        engLanguageEditor.putString(ClusterInfo.submit, "Submit");
        engLanguageEditor.putString(ClusterInfo.continueLabel, "Continue");
        engLanguageEditor.putString(ClusterInfo.pauseSurvey, "Pause");
        engLanguageEditor.putString(ClusterInfo.abortSurvey, "Abort");
        engLanguageEditor.putString(ClusterInfo.activityLable, "Activity");
        engLanguageEditor.putString(ClusterInfo.Demographics, "Status"); // Changed this for KYC
        engLanguageEditor.putString(ClusterInfo.MINI, "MINI");
        engLanguageEditor.putString(ClusterInfo.SummaryReport, "Summary Report");
        engLanguageEditor.putString(ClusterInfo.Entered, "Completed");
        engLanguageEditor.putString(ClusterInfo.Pending_to_sync, "Pending to sync");
        engLanguageEditor.putString(ClusterInfo.Synchronized, "Synchronized");
        engLanguageEditor.putString(ClusterInfo.Add_new_data_to_survey, "Add new response");
        engLanguageEditor.putString(ClusterInfo.Sync_data, "Sync data");
        engLanguageEditor.putString(ClusterInfo.Clear_Sync_data, "Clear Sync data");
        engLanguageEditor.putString(ClusterInfo.Logout, "Logout");
        engLanguageEditor.putString(ClusterInfo.HelpManual, "Help Manual");
        engLanguageEditor.putString(ClusterInfo.SwitchLanguage, "Switch Language");
        engLanguageEditor.putString(ClusterInfo.SendDebugData, "Send Debug Data");
        engLanguageEditor.putString(ClusterInfo.Built_by_Mahiti, "Built by Mahiti");
        engLanguageEditor.putString(ClusterInfo.Location_Selection, "Location Selection Mode");
        engLanguageEditor.putString(ClusterInfo.Select_language, "Select language");
        engLanguageEditor.putString(ClusterInfo.Lao, "Lao");
        engLanguageEditor.putString(ClusterInfo.English, "English");
        engLanguageEditor.putString(ClusterInfo.ok, "Ok");
        engLanguageEditor.putString(ClusterInfo.Data_copied_successfully, "Data copied successfully");
        engLanguageEditor.putString(ClusterInfo.Synchronizing_pending_data, "Synchronizing pending data");

        engLanguageEditor.putString(ClusterInfo.agrred, "Agreed");
        engLanguageEditor.putString(ClusterInfo.refused, "Refused");
        engLanguageEditor.putString(ClusterInfo.pleasereasonForRefused, "Please enter the reason for consent status refused");
        engLanguageEditor.putString(ClusterInfo.reasonForRefused, "Reason for consent status refused");
        engLanguageEditor.putString(ClusterInfo.reasonforAbort, "Please enter the reason for abort");
        engLanguageEditor.putString(ClusterInfo.exitFromSurvey, "Do yo want to Exit?");
        engLanguageEditor.putString(ClusterInfo.logOutFromSurvey, "Do yo want to Logout?");
        engLanguageEditor.putString(ClusterInfo.yes, "Yes");
        engLanguageEditor.putString(ClusterInfo.no, "No");
        engLanguageEditor.putString(ClusterInfo.consentstatusMSG, "Please Select the Consent status");
        engLanguageEditor.putString(ClusterInfo.selectClusterMSG, "Please Select the cluster");
        engLanguageEditor.putString(ClusterInfo.caseIdExistMSG, "Case Id already exist please enter the new Id");
        engLanguageEditor.putString(ClusterInfo.caseIdMSG, "Please enter the Case Id");
        engLanguageEditor.putString(ClusterInfo.destroyCurrentRecord, "This form will not be saved!");
        engLanguageEditor.putString(ClusterInfo.proceed, "Proceed");
        engLanguageEditor.putString(ClusterInfo.cancel, "Cancel");
        engLanguageEditor.putString(ClusterInfo.pleasewait, "Please wait");
        engLanguageEditor.putString(ClusterInfo.enterReason, "Please enter the reason");
        engLanguageEditor.putString(ClusterInfo.enterAnswer, "Please enter the answer");
        engLanguageEditor.putString(ClusterInfo.wouldLikeContinue, "would you like to continue?");
        engLanguageEditor.putString(ClusterInfo.agegreaterthan18, "Please take age greater than 18");
        engLanguageEditor.putString(ClusterInfo.noNetConn, "No network connection");
        engLanguageEditor.putString(ClusterInfo.miniCompleted, "Mini is already completed");
        engLanguageEditor.putString(ClusterInfo.demographicsComplete, "Registration is already completed");
        engLanguageEditor.putString(ClusterInfo.noPendingRecToSync, "There is no pending records to Sync");
        engLanguageEditor.putString(ClusterInfo.ENTERPASS, "Please enter the password.");
        engLanguageEditor.putString(ClusterInfo.enterusername, "Please enter the username.");
        engLanguageEditor.putString(ClusterInfo.USERNAMEPASS, "Please enter the username and password.");
        engLanguageEditor.putString(ClusterInfo.connectionProb, "connectivity problem");
        engLanguageEditor.putString(ClusterInfo.probServerside, "Some thing went wrong. Please check the connectivity and try again");
        engLanguageEditor.putString(ClusterInfo.plsCheckNetConn, "Please check network Connection to Login");
        engLanguageEditor.putString(ClusterInfo.plsEnterSixDigit, "Please Enter minimum six Characters for password ");
        engLanguageEditor.putString(ClusterInfo.loggedInOffline, "Your are logged in as offline");
        engLanguageEditor.putString(ClusterInfo.noSurveyDataCapture, "No Data Captured");
        engLanguageEditor.putString(ClusterInfo.plsEnterAllAnswers, "Please enter all the answers");
        engLanguageEditor.putString(ClusterInfo.trainingMode, "Training Mode");
        engLanguageEditor.putString(ClusterInfo.checkNetCOnnFetchDetails, "Please check the network connection to fetch the user details");
        engLanguageEditor.putString(ClusterInfo.probConnTryLater, "There is a problem in network connectivity, please do it later");
        engLanguageEditor.putString(ClusterInfo.probOccurredWhileDataSend, "Some thing went wrong. Please check the connectivity and try again");
        engLanguageEditor.putString(ClusterInfo.select, "Select");
        engLanguageEditor.putString(ClusterInfo.selectOption, "Please select the option");
        engLanguageEditor.putString(ClusterInfo.selectChoice, "Please select the choice");
        engLanguageEditor.putString(ClusterInfo.enterValue, "Please enter the value");
        engLanguageEditor.putString(ClusterInfo.selectDate, "Please select the date");
        engLanguageEditor.putString(ClusterInfo.selectImage, "Select Image");
        engLanguageEditor.putString(ClusterInfo.selectCamera, "Take from camera");
        engLanguageEditor.putString(ClusterInfo.selectGallery, "Select from gallery");

        engLanguageEditor.putString(ClusterInfo.otherLangNotAvail, "Other languages are not available");
        engLanguageEditor.putString(ClusterInfo.langChanged, "Language has changed");
        engLanguageEditor.putString(ClusterInfo.plsClickAgain, "Please click again");
        engLanguageEditor.putString(ClusterInfo.probInstallApp, "Problem occurred while installing the application");
        engLanguageEditor.putString(ClusterInfo.fetchData, "Fetching data...");
        engLanguageEditor.putString(ClusterInfo.emptyResponse, "Empty Response");
        engLanguageEditor.putString(ClusterInfo.plsWaitSync, "Please wait data is synchronizing...");
        engLanguageEditor.putString(ClusterInfo.someThingWrong, "Something went wrong");
        engLanguageEditor.putString(ClusterInfo.SendDebugData, "Please wait, Sending the debug log");
        engLanguageEditor.putString(ClusterInfo.movNextSec, "Moving to next section. You can not come back to this section!");
        engLanguageEditor.putString(ClusterInfo.rmvDataSkip, "Do you want to remove the data in between the skip questions?");
        engLanguageEditor.putString(ClusterInfo.cusRegistration,"Customer Registration");
        engLanguageEditor.putString(ClusterInfo.dailyPeriodicMessage, "Form filling not allowed for this cluster today");
        engLanguageEditor.putString(ClusterInfo.reasonForDisAgree, "Please enter the reason for disagree");
        engLanguageEditor.putString(ClusterInfo.noSurveyDataAvail, "No data collection available");
        engLanguageEditor.putString(ClusterInfo.sixDigitCode, "Please enter six digit code");
        engLanguageEditor.putString(ClusterInfo.viewLog,"View Log");
        engLanguageEditor.putString(ClusterInfo.testServer,"Test Server Connection");
        engLanguageEditor.putString(ClusterInfo.connProper,"All test passed !");
        engLanguageEditor.putString(ClusterInfo.connError,"All test failed !");
        engLanguageEditor.putString(ClusterInfo.MissingData,"Missing Data");
        engLanguageEditor.putString(ClusterInfo.RateingQuestion,"Please rate all");
        engLanguageEditor.putString(ClusterInfo.RankingQuestion,"Please rank differently");

        engLanguageEditor.commit();
    }



}
