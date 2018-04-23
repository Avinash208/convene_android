package org.mahiti.convenemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PreferenceConstants;


public class SupportClass {

    private static final String LOGGER_TAG = "SupportClass";
    Activity activity;
    Context context;

    /**
     * @param context
     * @param db
     * @param handler
     * @param surveyId
     */
    public void backButtonFunction(final SurveyQuestionActivity context, final net.sqlcipher.database.SQLiteDatabase db, final DBHandler handler, final int surveyId) {
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


    private static class RemoveTask extends AsyncTask<Context, Integer, String> {
        net.sqlcipher.database.SQLiteDatabase database;
        DBHandler dbhandler;
        int surveyID;
        SurveyQuestionActivity ctx;
        private SharedPreferences preferences;

        private RemoveTask(net.sqlcipher.database.SQLiteDatabase db, DBHandler handler, int survey_id, SurveyQuestionActivity context) {
            database = db;
            dbhandler = handler;
            surveyID = survey_id;
            ctx = context;
            preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        }

        @Override
        protected String doInBackground(Context... arg0) {
            String data = "";
            String selectQuery = "SELECT * FROM Response where survey_id=" + surveyID;
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
                        String removeData = "DELETE FROM Survey WHERE id = " + surveyID;
                        database.execSQL(removeData);
                        removeData = "DELETE FROM Response WHERE survey_id = " + surveyID;
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
}
