package org.mahiti.convenemis.model;

import org.mahiti.convenemis.database.ExternalDbOpenHelper;

import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public class HomeModule implements HomeModuleInterface {
    @Override
    public List<String> getExpandableHeadingList(ExternalDbOpenHelper dbhelper) {
        return dbhelper.getHeading();
    }
}
