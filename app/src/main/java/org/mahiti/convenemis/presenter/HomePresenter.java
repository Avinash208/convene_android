package org.mahiti.convenemis.presenter;

import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.model.HomeModule;
import org.mahiti.convenemis.model.HomeModuleInterface;
import org.mahiti.convenemis.view.HomeViewInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public class HomePresenter implements HomePresenterInterface{
    HomeViewInterface homeViewInterface;
    HomeModuleInterface homeModuleInterface;
    public HomePresenter(HomeViewInterface homeViewInterface) {
        this.homeViewInterface=homeViewInterface;
        initModule();

    }

    private void initModule() {
        homeModuleInterface= new HomeModule() ;
    }

    @Override
    public void doExpandableListHeadingFunctionality(ExternalDbOpenHelper dbhelper) {
        List<String> listDataHeader=homeModuleInterface.getExpandableHeadingList(dbhelper);
        HashMap<String, List<Datum>> listDataChild=dbhelper.getSurveyHeading();
        homeViewInterface.getExpandableListHeading(listDataHeader,listDataChild);
    }
}
