package org.yale.convene.model;

import org.yale.convene.database.ExternalDbOpenHelper;

import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public interface HomeModuleInterface {
    List<String> getExpandableHeadingList(ExternalDbOpenHelper dbhelper);
}
