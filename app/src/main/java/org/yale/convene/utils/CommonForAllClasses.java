package org.yale.convene.utils;

import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonForAllClasses {
    public static final int version = 38;
    public static final String UNAVAILABLE="UNAVAILABLE";
    public static final String dateFormat = "MM/dd/yyyy";


    private CommonForAllClasses(){

    }

    //next and previous buttons visiblity checking
    public static void showButtonVisible(Button next, Button previous){
        try {
            if(next!=null)
                next.setVisibility(View.VISIBLE);
            if(previous!=null)
                previous.setVisibility(View.VISIBLE);
        }
        catch (Exception e1)
        {
            Logger.logE("", "Exception in showButtonVisible method", e1);
        }
    }

    /**
     * checking date question based on Previous Question
     * @param current
     * @param previous
     * @return
     */
    public static boolean checkPreviousBased(String current, String previous){
        try {
            String currentVal= Utils.getINTODateformate(current);
            String preVal= Utils.getINTODateformate(previous);
            Date currentDate = new SimpleDateFormat(dateFormat).parse(currentVal);
            Date previousDate=new SimpleDateFormat(dateFormat).parse(preVal);
            return currentDate.after(previousDate);
        } catch (ParseException e) {
            Logger.logE("DateTimeFragment","Exception in check question based on Previous Question " , e);
        }
        return false;
    }

    /**
     * checking question based on Maximum and Minimum Values
     * @param date
     * @param current
     * @param previous
     * @return
     */
    public static boolean checkMinMaxBased(String date, String current, String previous){
        try {
            String minVal= Utils.getINTODateformate(date);
            String currentVal= Utils.getINTODateformate(current);
            String preVal= Utils.getINTODateformate(previous);
            Date dateText = new SimpleDateFormat(dateFormat).parse(minVal);
            Date currentDate = new SimpleDateFormat(dateFormat).parse(currentVal);
            Date date2 = new SimpleDateFormat(dateFormat).parse(preVal);
            return isDateInBetweenIncludingEndPoints(currentDate,date2,dateText);
        } catch (ParseException e) {
            Logger.logE("DateTimeFragment","Exception in check question based on Min date and Max Date " , e);
        }
        return false;
    }


    /**
     * comparing selected date from max and min
     * @param min
     * @param max
     * @param date
     * @return
     */
    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max, final Date date){
        return !(date.before(min) || date.after(max));
    }


    public static String getINTODateformate(String minValue) {
        Logger.logV("getINTODateformate----> ",minValue);
        String day=Character.toString(minValue.charAt(0))+Character.toString(minValue.charAt(1));
        String month=Character.toString(minValue.charAt(2))+Character.toString(minValue.charAt(3));
        String year=Character.toString(minValue.charAt(4))+Character.toString(minValue.charAt(5))+Character.toString(minValue.charAt(6))+Character.toString(minValue.charAt(7));
        Logger.logD("Dateforamt","date in validation" + day+"-"+month+"-"+year);
        return day+"-"+month+"-"+year;
    }
}
