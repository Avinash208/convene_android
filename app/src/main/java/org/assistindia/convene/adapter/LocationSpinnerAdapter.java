package org.assistindia.convene.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.assistindia.convene.BeenClass.boundarylevel.LocationType;

import java.util.List;

/**
 * Created by mahiti on 17/10/17.
 */

public class LocationSpinnerAdapter extends ArrayAdapter<LocationType> {

    Typeface font;

    public LocationSpinnerAdapter(Context context, int resource, List<LocationType> locationTypeList) {
        super(context, resource, locationTypeList);
        font = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
    }

    @NonNull
    @Override
    public View getView(int locationPosition, View convertView, @NonNull ViewGroup locationParent) {
        TextView view = (TextView) super.getView(locationPosition, convertView, locationParent);
        view.setTypeface(font);
        return view;
    }


    @Override
    public View getDropDownView(int locationPosition, View convertView, @NonNull ViewGroup locationParent) {
        TextView view = (TextView) super.getDropDownView(locationPosition, convertView, locationParent);
        view.setTypeface(font);
        return view;
    }
}
