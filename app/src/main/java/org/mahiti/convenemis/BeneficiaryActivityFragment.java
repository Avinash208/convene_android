package org.mahiti.convenemis;

/**
 * Created by mahiti on 28/05/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.mahiti.convenemis.BeenClass.BeneficiaryLinkage;
import org.mahiti.convenemis.api.CallServerForApi;
import org.mahiti.convenemis.api.PushingResultsInterface;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeneficiaryActivityFragment extends Fragment{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";




    public BeneficiaryActivityFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BeneficiaryActivityFragment newInstance(int sectionNumber) {
        BeneficiaryActivityFragment fragment = new BeneficiaryActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beneficiarylinkages_activity, container, false);
        initVariable();


        return rootView;
    }

    private void initVariable() {

    }
}
