package org.yale.convene.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.yale.convene.BeenClass.facilitiesBeen.FacilitiesTypesBeen;

import java.util.List;

/**
 * Created by mahiti on 17/10/17.
 */

public class FacilitySpinnerAdapter extends ArrayAdapter<FacilitiesTypesBeen> {
    // Initialise custom font, for example:
    Typeface font;
    List<FacilitiesTypesBeen> facilityTypeList;


    /**
     *
     * @param context
     * @param resource
     * @param items
     */
    public FacilitySpinnerAdapter(Context context, int resource, List<FacilitiesTypesBeen> items) {
        super(context, resource, items);
        font = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
        this.facilityTypeList=items;
    }


    /**
     *
     * @param itemPosition
     * @param facilityConvertView
     * @param parent
     * @return
     */
    // Affects default (closed) state of the spinner
    @NonNull
    @Override
    public View getView(int itemPosition, View facilityConvertView, @NonNull ViewGroup parent) {
        TextView viewFacility = (TextView) super.getView(itemPosition, facilityConvertView, parent);
        viewFacility.setTypeface(font);
        return viewFacility;
    }


    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        return view;
    }
}
