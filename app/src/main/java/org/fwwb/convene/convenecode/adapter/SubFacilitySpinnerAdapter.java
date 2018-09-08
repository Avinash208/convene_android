package org.fwwb.convene.convenecode.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.fwwb.convene.convenecode.BeenClass.facilitiesBeen.FacilitySubTypeBeen;

import java.util.List;

/**
 * Created by mahiti on 17/10/17.
 */

public class SubFacilitySpinnerAdapter extends ArrayAdapter<FacilitySubTypeBeen> {
    // Initialise custom font, for example:
    Typeface font ;
    public SubFacilitySpinnerAdapter(Context context, int resource, List<FacilitySubTypeBeen> items) {
        super(context, resource, items);
        font = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
    }


    // Affects default (closed) state of the spinner
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);

        view.setTypeface(font);
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        return view;
    }
}
