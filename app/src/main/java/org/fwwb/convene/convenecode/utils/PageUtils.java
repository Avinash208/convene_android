package org.fwwb.convene.convenecode.utils;

import android.os.Bundle;

import org.fwwb.convene.convenecode.BeenClass.Page;


/**
 * Created by mahiti on 31/12/15.
 */
public class PageUtils
{
    private PageUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static Page getPageUtils(Bundle args)
    {
        Page page=new Page(args.getInt("Q_ID"), args.getInt("Q_Number"),
                args.getInt("A_Type"), args.getString("QUESTION"),
                args.getString("ANSWER"), args.getBoolean("MULTI_ENTRY"),
                args.getStringArrayList("AnswersList"),
                args.getInt("BlockId"), args.getString("sub_question"),args.getString("typologyId"),
                args.getString("in_en"),args.getString("ht_en"),args.getString("mandatory"),args.getString("validation"));
        return page;
    }


}
