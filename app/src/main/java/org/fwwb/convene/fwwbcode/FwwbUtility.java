package org.fwwb.convene.fwwbcode;

import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List; /**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class FwwbUtility {


    public static List<TaskItemBean> sortList(List<TaskItemBean> itemBeanList, String dateFormat) {

        Collections.sort(itemBeanList, (arg0, arg1) -> {
            SimpleDateFormat format = new SimpleDateFormat(
                    dateFormat);
            int compareResult = 0;
            try {
                Date arg0Date = format.parse(arg0.getTrainingDate());
                Date arg1Date = format.parse(arg1.getTrainingDate());
                compareResult = arg0Date.compareTo(arg1Date);
            } catch (ParseException e) {
                e.printStackTrace();
                compareResult = arg0.getTrainingDate().compareTo(arg1.getTrainingDate());
            }
            return compareResult;
        });

        return itemBeanList;
    }
}
