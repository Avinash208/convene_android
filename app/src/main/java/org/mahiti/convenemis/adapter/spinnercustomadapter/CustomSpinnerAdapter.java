package org.mahiti.convenemis.adapter.spinnercustomadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;

import java.util.List;

public  class CustomSpinnerAdapter extends ArrayAdapter<LevelBeen> {
    Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/Roboto-Light.ttf");


    public CustomSpinnerAdapter(Context context, int resource, List<LevelBeen> districtItems) {
        super(context, resource, districtItems);
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