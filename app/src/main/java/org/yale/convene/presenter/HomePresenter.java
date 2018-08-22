package org.yale.convene.presenter;

import org.yale.convene.BeenClass.beneficiary.Datum;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.model.HomeModule;
import org.yale.convene.model.HomeModuleInterface;
import org.yale.convene.view.HomeViewInterface;

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
