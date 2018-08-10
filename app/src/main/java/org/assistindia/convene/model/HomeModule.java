package org.assistindia.convene.model;

import org.assistindia.convene.database.ExternalDbOpenHelper;

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
