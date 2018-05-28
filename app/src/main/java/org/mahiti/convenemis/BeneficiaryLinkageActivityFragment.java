package org.mahiti.convenemis;

/**
 * Created by mahiti on 28/05/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.PreviewQuestionAnswerSet;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class BeneficiaryLinkageActivityFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    public BeneficiaryLinkageActivityFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BeneficiaryLinkageActivityFragment newInstance(int sectionNumber) {
        BeneficiaryLinkageActivityFragment fragment = new BeneficiaryLinkageActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beneficiarylinkages, container, false);

        return rootView;
    }
}
