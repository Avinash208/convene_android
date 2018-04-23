package org.mahiti.convenemis.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static org.mahiti.convenemis.SurveyQuestionActivity.MY_PREFS_NAME;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */

public class FontUtils {

    private FontUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void setFontDialog(final Context context, Activity activity){
        final ArrayList<String> listFountSize =new ArrayList<>();
        listFountSize.add("Small");
        listFountSize.add("Medium");
        listFountSize.add("Large");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, listFountSize);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Font Size");
        builder.setAdapter(arrayAdapter, (dialog, item) -> {
            if (dialog == null)
                return;
            if (item == Constants.FONT_SMALL)
                setFontT0Preference(0, context);
            else if (item == Constants.FONT_MEDIUM)
                setFontT0Preference(1, context);
            else if (item == Constants.FONT_LARGE)
                setFontT0Preference(2, context);

        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void setFontT0Preference(int selectedFont, Context context) {
        SharedPreferences.Editor editor1 = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        String titleStr = "tittle";
        String questionStr="question";
        String answerStr="answer";
        switch(selectedFont){
            case 0:
                
                editor1.putString(titleStr, "10");

                editor1.putString(questionStr, "12");
                editor1.putString(answerStr, "10");
                editor1.apply();
                break;
            case 1:
                editor1.putString(titleStr, "20");
                editor1.putString(questionStr, "15");
                editor1.putString(answerStr, "15");
                editor1.apply();
                break;
            case 2:
                editor1.putString(titleStr, "30");
                editor1.putString(questionStr, "28");
                editor1.putString(answerStr, "20");
                editor1.apply();
                break;
            default:
                break;
        }
    }
}
