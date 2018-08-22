package org.yale.convene.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mahiti on 19/2/16.
 */
public class PeriodicityUtils {
    public static final String dateFormat = "MM/dd/yyyy"; // Maintaining as per the back end format
    private static final String TAG = "PeriodicityUtils :";
    private static String monthlyStr ="Monthly";
    private static String quaterlyStr = "Quarterly";
    private static String yearStr;

    private PeriodicityUtils(){
        // Not using this constructor
    }

    /**
     *
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getDailyList(int dayLimit, int featureAllow){
        List<String> dailyList = new ArrayList<>();
        dailyList = fillDateString(dailyList,dayLimit,3);
        // 2 is for feature allowed
        if(featureAllow==2){
            dailyList = fillDateString(dailyList,dayLimit,featureAllow);
        }
        Logger.logD(PeriodicityUtils.class.getSimpleName(), String.valueOf(dailyList));
        return dailyList;
    }

    /**
     *
     * @param existList
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> fillDateString(List<String> existList, int dayLimit, int featureAllow){
        for(int i=0;i<dayLimit;i++){
            Calendar cal= Calendar.getInstance();
            int currentDay=cal.get(Calendar.DAY_OF_MONTH);
            // 2 is for feature allowed
            if(featureAllow==2)
                cal.set(Calendar.DAY_OF_MONTH, currentDay + i); //Set the date to i days ago
            else
                cal.set(Calendar.DAY_OF_MONTH, currentDay - i); //Set the date to i days ago

            SimpleDateFormat formattedDate = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            existList.add(formattedDate.format(cal.getTime()));
        }
        return existList;
    }

    /**
     *
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getWeeklyList(int dayLimit, int featureAllow){
        List<String> weeklyList = new ArrayList<>();
        weeklyList = fillWeeklyString(weeklyList, dayLimit, 3);
        // 2 is for feature allowed
        if(featureAllow==2){
            weeklyList = fillWeeklyString(weeklyList, dayLimit, featureAllow);
        }
        Logger.logD(PeriodicityUtils.class.getSimpleName(), String.valueOf(weeklyList));
        return weeklyList;
    }

    /**
     *
     * @param existList
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> fillWeeklyString(List<String> existList, int dayLimit, int featureAllow){
        String value = "";
        String constantValue = " to ";
        for(int i=0,count=0;count<dayLimit;i=i+7){
            Calendar cal= Calendar.getInstance();
            int currentDay=cal.get(Calendar.DAY_OF_MONTH);
            // 2 is for feature allowed
            if(featureAllow==2)
                cal.set(Calendar.DAY_OF_MONTH, currentDay + i); //Set the date to i days ago
            else
                cal.set(Calendar.DAY_OF_MONTH, currentDay - i); //Set the date to i days ago

            SimpleDateFormat formattedDate = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
            String tempDate =formattedDate.format(cal.getTime());
            /*
              bellow logic is for generating the date in weekly like Ex: (Feb14 to Feb20) or one more String like (Feb21 to Feb27)
             */
            if(i==0){
                value = tempDate;
            }
            else
            {
                value = value + constantValue + tempDate;
                existList.add(value);
                value = tempDate;
                count++;
            }
        }
        return existList;
    }

    /**
     *
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getFortNightList(int dayLimit, int featureAllow){
        List<String> fortNightList = new ArrayList<>();
        fortNightList = fillFortNightStrings(fortNightList, dayLimit, 3);
        // 2 is for feature allowed
        if(featureAllow==2){
            fortNightList = fillFortNightStrings(fortNightList, dayLimit, featureAllow);
        }
        Logger.logD(PeriodicityUtils.class.getSimpleName(), String.valueOf(fortNightList));
        return fortNightList;
    }

    /**
     *
     * @param existList
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> fillFortNightStrings(List<String> existList, int dayLimit, int featureAllow){
        String secondHalf = " First Half";
        String firstHalf = " Second Half";
        Calendar currentCal = Calendar.getInstance();
        int presentDay = currentCal.get(Calendar.DAY_OF_MONTH);
        for(int i=0;i<dayLimit;i++) {
            Calendar cal = Calendar.getInstance();
            int currentDay = cal.get(Calendar.MONTH);
            // 2 is for feature allowed
            if (featureAllow == 2)
                cal.set(Calendar.MONTH, currentDay + i); //Set the date to i days ago
            else
                cal.set(Calendar.MONTH, currentDay - i); //Set the date to i days ago

            SimpleDateFormat formattedDate = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            String tempDate = formattedDate.format(cal.getTime());
            if(presentDay<=15){
                existList.add(tempDate+firstHalf);
                existList = getFortNightSubList(existList,tempDate,secondHalf,featureAllow);
            }
            else{
                existList.add(tempDate+secondHalf);
                existList.add(tempDate+firstHalf);
            }
            presentDay = 16; // assigning 16 is for the sake of failing the presentDay condition for second time run
        }
        return existList;
    }

    /**
     *
     * @param existList
     * @param tempDate
     * @param secondHalf
     * @param feature
     * @return
     */
    public static List<String> getFortNightSubList(List<String> existList, String tempDate, String secondHalf, int feature){
        if(feature==2) {
            existList.add(tempDate + secondHalf);
        }
        return existList;
    }

    /**
     *
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getMonthlyList(int dayLimit, int featureAllow){
        List<String> monthlyList = new ArrayList<>();
        monthlyList = getMonthlyList(monthlyList, dayLimit, 3);
        // 2 is for feature allowed
        if(featureAllow==2){
            monthlyList = getMonthlyList(monthlyList, dayLimit, featureAllow);
        }
        Logger.logD(PeriodicityUtils.class.getSimpleName(), String.valueOf(monthlyList));

        return monthlyList;
    }

    /**
     *
     * @param existList
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getMonthlyList(List<String> existList, int dayLimit, int featureAllow){
        for(int i=0;i<dayLimit;i++){
            Calendar cal= Calendar.getInstance();
            int currentDay=cal.get(Calendar.MONTH);
            // 2 is for feature allowed
            if(featureAllow==2)
                cal.set(Calendar.MONTH, currentDay + i); //Set the date to i days ago
            else
                cal.set(Calendar.MONTH, currentDay - i); //Set the date to i days ago

            SimpleDateFormat formattedDate = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            existList.add(formattedDate.format(cal.getTime()));
        }
        return existList;
    }

    /**
     *
     * @param dayLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getQuarterlyList(int dayLimit, int featureAllow){
        List<String> quarterlyList = new ArrayList<>();
        quarterlyList = getQuarterlyList(quarterlyList, dayLimit, 3);
        // 2 is for feature allowed
        if(featureAllow==2){
            quarterlyList = getQuarterlyList(quarterlyList, dayLimit, featureAllow);
        }
        Logger.logD(PeriodicityUtils.class.getSimpleName(), String.valueOf(quarterlyList));
        return quarterlyList;
    }

    /**
     *
     * @param existList
     * @param yearLimit
     * @param featureAllow
     * @return
     */
    public static List<String> getQuarterlyList(List<String> existList, int yearLimit, int featureAllow){
        String[] quarters = {" MQ - First Quarter", " JQ - Second Quarter", " SQ - Third Quarter", " DQ - Fourth Quarter"};
        /*
          yearLimit is 3 then the items will be current quarter and last 2 quarters will be displayed
         */
        for(int i=0,count=0;count<yearLimit;i=i+3,count++){
            Calendar cal= Calendar.getInstance();
            int currentDay=cal.get(Calendar.MONTH);
            // 2 is for feature allowed
            if(featureAllow==2)
                cal.set(Calendar.MONTH, currentDay + i); //Set the date to i days ago
            else
                cal.set(Calendar.MONTH, currentDay - i); //Set the date to i days ago

            int month = cal.get(Calendar.MONTH);
            int quarter = month % 3 == 0?  (month / 3): ( month / 3)+1;
            existList.add(cal.get(Calendar.YEAR)+quarters[quarter-1]);
        }
        return existList;
    }


    public static String getToday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYMMDD, Locale.US);
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public static boolean isEligibleForExtension(int pLimit, String periodicityFlag) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYMMDD, Locale.US);

        Calendar calendar = Calendar.getInstance();
        try {
            Logger.logD(Constants.P_LIMIT,pLimit+"");
            calendar.setTime(simpleDateFormat.parse(getPreviousPeriodLastDate(periodicityFlag)));
            calendar.add(Calendar.DATE, pLimit);
           } catch (ParseException e) {
            e.printStackTrace();
        }
        /*String cuurentDate =simpleDateFormat.format(new Date());
        String prevDate =simpleDateFormat.format(calendar.getTime());*/
        return (new Date().before(calendar.getTime()) || new Date().equals(calendar.getTime()));

    }
    public static Date getPreviousPeriodicityTime(String periodicity) {
        Calendar calendar = Calendar.getInstance();
        if (monthlyStr.equalsIgnoreCase(periodicity))
        {   calendar.add(Calendar.MONTH, -1);
            return calendar.getTime();
        }
        if (quaterlyStr.equalsIgnoreCase(periodicity))
        {
            calendar.add(Calendar.MONTH, -3);
            return calendar.getTime();
        }
        if (yearStr.equalsIgnoreCase(periodicity))
        {
            calendar.add(Calendar.YEAR, -1);
            return calendar.getTime();
        }
        return null;
    }

    public static String getPreviousPeriodLastDate(String periodicity) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYMMDD, Locale.US);

        Calendar calendar = Calendar.getInstance();
        if (monthlyStr.equalsIgnoreCase(periodicity))
        {   calendar.add(Calendar.MONTH,-1);
            calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        if (quaterlyStr.equalsIgnoreCase(periodicity))
        {
            calendar.add(Calendar.MONTH, -3);
            calendar.set(Calendar.MONTH,getQuaterlyLastMonth(calendar.get(Calendar.MONTH)));
            calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        }
        if (yearStr.equalsIgnoreCase(periodicity))
        {

            calendar.set(Calendar.MONTH,Calendar.MARCH);
            calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        }
        /*String date = simpleDateFormat.format(calendar.getTime());*/
        return simpleDateFormat.format(calendar.getTime());
    }

    public static int getQuaterly(int i) {
        if (i ==1 || i == 2 || i ==3)
            return 4;
        if (i ==4 || i == 5 || i ==6)
            return  1;
        if (i ==7 || i == 8 || i ==9)
            return 2;
        if (i ==10 || i == 11 || i ==12)
            return 3;
        return 0;
    }

    public static int getQuaterlyLastMonth(int month) {
        if (month ==1 || month == 2 || month == 0)
            return 2;
        if (month ==4 || month == 5 || month ==3)
            return  5;
        if (month ==7 || month == 8 || month ==6)
            return 8;
        if (month ==10 || month == 11 || month ==9)
            return 11;
        return 0;
    }

    public static boolean isCurrentPeriodicity(String captureDate, String periodicity) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYMMDD, Locale.US);
        String[] splitDate = captureDate.split("-");
        String cuurentDate = simpleDateFormat.format(new Date());
        String[] splitDateCurrentDate = cuurentDate.split("-");
        if (monthlyStr.equalsIgnoreCase(periodicity))
        {
            return (splitDate[1].equals(splitDateCurrentDate[1])) && (splitDate[0].equals(splitDateCurrentDate[0]));
        }
        if (quaterlyStr.equalsIgnoreCase(periodicity))
        {

            int quarterlyCurrent = PeriodicityUtils.getQuaterly(Integer.parseInt(splitDateCurrentDate[1]));
            int quarterlyCaptureDate = PeriodicityUtils.getQuaterly(Integer.parseInt(splitDate[1]));
            return (quarterlyCaptureDate == quarterlyCurrent && (splitDate[0].equals(splitDateCurrentDate[0])));

        }
        if (yearStr.equalsIgnoreCase(periodicity))
        {
            if (Integer.parseInt(splitDateCurrentDate[1]) <= 3)
            {
                String previousDate = String.valueOf(Integer.parseInt(splitDateCurrentDate[0])-1);
                return ((splitDate[0].equals(splitDateCurrentDate[0])&& Integer.parseInt(splitDate[1])<=3) ||(previousDate.equals(splitDateCurrentDate[0]) && Integer.parseInt(splitDate[1])>=4));
            }
            else
            {
                String previousDate = String.valueOf(Integer.parseInt(splitDateCurrentDate[0])+1);
                return ((splitDate[0].equals(splitDateCurrentDate[0]) && Integer.parseInt(splitDate[1])>=4) ||(previousDate.equals(splitDateCurrentDate[0])&& Integer.parseInt(splitDate[1])<=3));

            }

        }
        return false;
    }


    public static boolean isBelongsToPreviousPeriodicity(String periodicity, String captureDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYMMDD, Locale.US);
        String[] captureSplit = captureDate.split("-");
        Calendar calendar = Calendar.getInstance();
        String currentDate = simpleDateFormat.format(calendar.getTime());
        String[] currentSplit = currentDate.split("-");
        boolean logic = (captureSplit[0].equals(currentSplit[0]));
        if (monthlyStr.equalsIgnoreCase(periodicity))
        {
            return (Integer.valueOf(captureSplit[1]) == Integer.parseInt(currentSplit[1])-1);

        }
        if (quaterlyStr.equalsIgnoreCase(periodicity))
        {
            int quarterlyCurrent = PeriodicityUtils.getQuaterly(Integer.parseInt(currentSplit[1]) -1);
            int quarterlyCaptureDate = PeriodicityUtils.getQuaterly(Integer.parseInt(captureSplit[1]));
            if (PeriodicityUtils.getQuaterly(Integer.parseInt(currentSplit[1])) == 4)
                logic = ((Integer.parseInt(currentSplit[0]) -1) == Integer.parseInt((captureSplit[0])));
            return (quarterlyCaptureDate == quarterlyCurrent && logic);

        }
        if (yearStr.equalsIgnoreCase(periodicity))
        {
            if (Integer.parseInt(currentSplit[1]) <= 3)
            {
                return ((captureSplit[0].equals(String.valueOf(Integer.parseInt(currentSplit[0])-2))&& Integer.parseInt(captureSplit[1])>=3) ||(captureSplit[0].equals(String.valueOf(Integer.parseInt(currentSplit[0])-1)) && Integer.parseInt(captureSplit[1])<=3));
            }
            else
            {
                return ((captureSplit[0].equals(String.valueOf(Integer.parseInt(currentSplit[0])-1)) && Integer.parseInt(captureSplit[1])>=4) ||(captureSplit[0].equals(currentSplit[0]) && Integer.parseInt(captureSplit[1])<=3));

            }
        }
        return false;
    }
}