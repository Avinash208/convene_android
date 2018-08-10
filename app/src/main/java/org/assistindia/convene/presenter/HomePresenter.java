package org.assistindia.convene.presenter;

import org.assistindia.convene.BeenClass.beneficiary.Datum;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.model.HomeModule;
import org.assistindia.convene.model.HomeModuleInterface;
import org.assistindia.convene.view.HomeViewInterface;

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
