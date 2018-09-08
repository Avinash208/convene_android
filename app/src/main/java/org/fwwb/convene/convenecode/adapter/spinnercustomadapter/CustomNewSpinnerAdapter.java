package org.fwwb.convene.convenecode.adapter.spinnercustomadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;

import java.util.List;

public  class CustomNewSpinnerAdapter extends ArrayAdapter<Datum> {
    // Initialise custom font, for example:
    Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/Roboto-Light.ttf");


    public CustomNewSpinnerAdapter(Context context, int resource, List<Datum> houseHoldItems) {
        super(context, resource, houseHoldItems);
    }


    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);

        view.setTypeface(font);
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        return view;
    }
}