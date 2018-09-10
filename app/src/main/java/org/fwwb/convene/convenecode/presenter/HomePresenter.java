package org.fwwb.convene.convenecode.presenter;

import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.model.HomeModule;
import org.fwwb.convene.convenecode.model.HomeModuleInterface;
import org.fwwb.convene.convenecode.view.HomeViewInterface;

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
