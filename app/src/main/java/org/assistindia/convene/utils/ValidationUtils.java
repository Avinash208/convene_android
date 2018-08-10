package org.assistindia.convene.utils;

import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import org.assistindia.convene.database.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srs on 21/1/17.
 */
public class ValidationUtils {

    private static String hhMmSsStr = "HH:mm:ss";

    private ValidationUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void setInputType(EditText editText, String[] arrayValidation, RestUrl restUrl) {
        try {
            Logger.logD("Validation Utils","Expression for Edit text questions " + Arrays.toString(arrayValidation));
            if ("R".equalsIgnoreCase(arrayValidation[0]) || "o".equalsIgnoreCase(arrayValidation[0])) {
                InputFilter[] FilterArray;
                int maxLength;
                int maxLengthMobile;
                switch (arrayValidation[1]) {
                    case "A":
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        editText.setFilters(new InputFilter[]{StringUtils.filterALPHAFacility});
                        break;

                    case "N":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;

                    case "M":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxLength = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                        editText.setFilters(FilterArray);
                        break;
                    case "AG":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;
                    case "L":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;
                    case "P":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        maxLength = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                        editText.setFilters(FilterArray);
                        break;
                    case "LN":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxLength = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                        editText.setFilters(FilterArray);
                        break;
                    case "D":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        //maxLengthMobile = arrayValidation[3].length();
                        maxLengthMobile = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;
                    case "AN":
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        maxLength = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                        editText.setFilters(new InputFilter[]{StringUtils.filterAlpha});
                        break;
                    case "NOV":
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        maxLength = Integer.parseInt(arrayValidation[3]);
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                        editText.setFilters(FilterArray);
                        break;
                    case "AD":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;

                    case "VO":
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;
                    case "PAN":
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        maxLengthMobile = arrayValidation[3].length();
                        FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(maxLengthMobile);
                        editText.setFilters(FilterArray);
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            Logger.logE("ValidationUtils", "Exception in SetInputType ", e);
            restUrl.writeToTextFile("Exception on not getting the validation","","getValidation");
        }


    }

    public static boolean performNextValidation(String[] arrayValidation, String answerText) {

        if ("R".equalsIgnoreCase(arrayValidation[0]) || "O".equalsIgnoreCase(arrayValidation[0])) {
            try {
                switch (arrayValidation[1]) {
                    case "AN":                  /*AlphaNumeric */
                        if (answerText.length() > Integer.parseInt(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() >= Integer.parseInt(arrayValidation[2]);
                    case "A":                   /*Alphabets*/
                        if (answerText.length() > Integer.parseInt(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() >= Integer.parseInt(arrayValidation[2]);

                    case "NOV":               /*No Validation*/
                        if (answerText.length() >= Integer.parseInt(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() > Integer.parseInt(arrayValidation[2]);

                    case "D":
                        if (Double.parseDouble(answerText) > Double.parseDouble(arrayValidation[3])) {
                            return false;
                        }
                        return Double.parseDouble(answerText) >= Double.parseDouble(arrayValidation[2]);


                    case "AG":

                        long number = Long.parseLong(answerText);
                        if (number > Long.parseLong(arrayValidation[3])) {
                            return false;
                        }
                        if (number < Long.parseLong(arrayValidation[2])) {
                            return false;
                        }
                        if (number <= Long.parseLong(arrayValidation[3]) && number >= Long.parseLong(arrayValidation[2])) {
                            return true;
                        }
                        break;
                    case "LN":
                        if (answerText.length() > Long.parseLong(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() >= Long.parseLong(arrayValidation[2]);

                    case "M":
                        if (answerText.length() > Long.parseLong(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() >= Long.parseLong(arrayValidation[2]);

                    case "N":
                        number = Long.parseLong(answerText);
                        if (number >Long.parseLong(arrayValidation[3])) {
                            return false;
                        }
                        return number >= Long.parseLong(arrayValidation[2]);
                    case "E":
                        boolean value = EmailUtils.isEmailValid(answerText);
                        if (value) {
                            return true;
                        }
                        break;
                    case "P":
                        if (Double.parseDouble(answerText) > Double.parseDouble(arrayValidation[3])) {
                            return false;
                        }
                        return Double.parseDouble(answerText) >= Double.parseDouble(arrayValidation[2]);
                    case "L":
                        if (answerText.length() < (arrayValidation[3].length() - 1)) {

                            return false;
                        }
                        if (answerText.length() > (arrayValidation[3].length() - 1)) {
                            String phoneNumber = answerText;
                            System.out.println(phoneNumber.length());
                            String regex = "^\\+?[0-9. ()-]{10,25}$";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(phoneNumber);
                            if (matcher.matches()) {
                                return true;
                            } else {
                                System.out.println("Phone Number must be in the form XXX-XXXXXXX");
                            }
                        }
                        break;

                    case "VO":
                        if(answerText.length()>arrayValidation[3].length()){
                            return false;
                        }
                        if(answerText.length()<arrayValidation[2].length()){
                            return false;
                        }

                        return StringUtils.isValidVoterId(answerText);

                    case "AD":
                        if(answerText.length()>arrayValidation[3].length()){
                          return false;
                        }
                        if(answerText.length()<arrayValidation[2].length()){
                            return false;
                         }

                        return StringUtils.isValidAadharNumber(answerText);

                    case "PAN":
                        if(answerText.length()>arrayValidation[3].length()){
                            return false;
                        }
                        if(answerText.length()<arrayValidation[2].length()){
                            return false;
                        }

                        return StringUtils.isValidPanNumber(answerText);

                    default:
                        if (answerText.length() > Integer.parseInt(arrayValidation[3])) {
                            return false;
                        }
                        return answerText.length() >= Integer.parseInt(arrayValidation[2]);
                }
            } catch (Exception e) {
                Logger.logE("ValidationUtils", "Exception in PerformNextValidation", e);
            }
        }
        return false;

    }

    public static boolean dateInnerValidation(String ansText, String arrayValidation, DBHandler dbHandler, TextView errorText) {
        if (!"".equals(ansText.trim())) {
            String dateText = ansText.trim();
            /*array containing main equation*/
            String[] array = arrayValidation.split("#");
            /*array1 containing first equation which is splited from main equation*/
            String[] array1 = array[0].split(":");

            if (("R".equalsIgnoreCase(array1[0]) || "o".equalsIgnoreCase(array1[0])) && "D".equalsIgnoreCase(array1[1])) {
                if (array.length > 1) {
                    /*array2 containing second equation which is splited from first equation*/
                    String[] array2 = array[1].split(":");
                    String prev = DBHandler.getAnswerDateFromPrevious(array2[1], dbHandler);
                    if (CommonForAllClasses.checkMinMaxBased(dateText, array1[3], array1[4])) {
                        dateText = ansText.trim();
                        if (CommonForAllClasses.checkPreviousBased(dateText, prev)) {
                            return true;
                        } else {
                            errorText.setText(array2[3]);
                            return false;
                        }
                    } else {
                        errorText.setText(array1[5]);
                        return false;
                    }
                } else if ("00000000".equalsIgnoreCase(array1[3])) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyy", Locale.ENGLISH);
                    try {
                        Date selectedDate = sdf.parse(ansText);
                        Date minValue = sdf.parse(array1[4]);
                        if (minValue.after(selectedDate)) {

                            return true;
                        } else {
                            errorText.setText(array1[5]);
                            return false;
                        }
                    } catch (ParseException e) {
                        Logger.logE("","",e);
                    }

                }else if ("01011900".equalsIgnoreCase(array1[3])) {
                    return true;

                } else if ("00000000".equalsIgnoreCase(array1[4])) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyy", Locale.ENGLISH);
                    try {
                        Date selectedDate = sdf.parse(ansText);
                        Date minValue = sdf.parse(array1[3]);
                        if (minValue.after(selectedDate)) {

                            return true;
                        } else {
                            errorText.setText(array1[5]);
                            return false;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (CommonForAllClasses.checkMinMaxBased(dateText, array1[3], array1[4])) {
                        return true;
                    } else {
                        errorText.setText(array1[5]);
                        return false;
                    }
                }
            }
            if (("R".equalsIgnoreCase(array[0]) || "o".equalsIgnoreCase(array[0])) && "T".equalsIgnoreCase(array[1]) && "ISO".equalsIgnoreCase(array[2])) {

                dateText = ansText.trim();
                String minString = array[4].replace("-", ":");
                String maxString = array[5].replace("-", ":");
                try {
                    Date time1 = new SimpleDateFormat(hhMmSsStr,Locale.ENGLISH).parse(minString);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(time1);

                    Date time2 = new SimpleDateFormat(hhMmSsStr,Locale.ENGLISH).parse(maxString);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(time2);

                    Date d = new SimpleDateFormat(hhMmSsStr,Locale.ENGLISH).parse(dateText);
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(d);
                    Date x = calendar3.getTime();
                    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                        if (x.before(calendar1.getTime())) {
                            errorText.setText(array[6]);
                            return false;
                        } else {
                            return true;
                        }

                    } else {
                        errorText.setText(array[6]);
                        return false;
                    }
                } catch (ParseException e) {
                   Logger.logE("","",e);
                }
            } else {
                return true;
            }
            return false;
        }
        return false;
    }
}
