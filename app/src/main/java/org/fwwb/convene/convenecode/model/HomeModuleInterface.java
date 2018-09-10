package org.fwwb.convene.convenecode.model;

import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;

import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public interface HomeModuleInterface {
    List<String> getExpandableHeadingList(ExternalDbOpenHelper dbhelper);
}
