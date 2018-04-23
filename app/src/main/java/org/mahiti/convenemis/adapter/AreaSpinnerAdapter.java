package org.mahiti.convenemis.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.facilitiesBeen.FacilitiesAreaBeen;

import java.util.List;



public class AreaSpinnerAdapter extends ArrayAdapter<FacilitiesAreaBeen> {

    private Typeface font;


    /**
     *
     * @param areaContext
     * @param resource
     * @param areaBeenList
     */
    public AreaSpinnerAdapter(Context areaContext, int resource, List<FacilitiesAreaBeen> areaBeenList) {
        super(areaContext, resource, areaBeenList);
        font = Typeface.createFromAsset(areaContext.getAssets(),
                "fonts/Roboto-Light.ttf");
    }


    /**
     *
     * @param areaPosition
     * @param convertView
     * @param areaParent
     * @return
     */
    @NonNull
    @Override
    public View getView(int areaPosition, View convertView, @NonNull ViewGroup areaParent) {
        TextView view = (TextView) super.getView(areaPosition, convertView, areaParent);
        view.setTypeface(font);
        return view;
    }


    /**
     *
     * @param areaPosition
     * @param convertView
     * @param areaParent
     * @return
     */
    @Override
    public View getDropDownView(int areaPosition, View convertView, @NonNull ViewGroup areaParent) {
        TextView view = (TextView) super.getDropDownView(areaPosition, convertView, areaParent);
        view.setTypeface(font);
        return view;
    }
}
