package org.yale.convene.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * Created by mahiti on 28/12/15.
 */
public class LabelUtils {

    private LabelUtils(){

    }
    public static SpannableStringBuilder getQuestion(String q_numberRed, String blackQuestion){
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString redSpannable = new SpannableString(q_numberRed);
        redSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0,q_numberRed.length(), 0);
        builder.append(redSpannable);

        String blackDot = ".";
        SpannableString blackdotSpannable = new SpannableString(blackDot);
        blackdotSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0,blackDot.length(), 0);
        builder.append(blackdotSpannable);

        SpannableString whiteSpannable = new SpannableString(blackQuestion);
        whiteSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0,blackQuestion.length(), 0);
        builder.append(whiteSpannable);
        return builder;
    }

}
