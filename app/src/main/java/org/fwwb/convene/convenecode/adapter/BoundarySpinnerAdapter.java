package org.fwwb.convene.convenecode.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.fwwb.convene.convenecode.BeenClass.boundarylevel.Boundary;

import java.util.List;



public class BoundarySpinnerAdapter extends ArrayAdapter<Boundary> {
    Typeface font;


    /**
     *
     * @param boundaryContext
     * @param resource
     * @param boundaryList
     */
    public BoundarySpinnerAdapter(Context boundaryContext, int resource, List<Boundary> boundaryList) {
        super(boundaryContext, resource, boundaryList);
        font = Typeface.createFromAsset(boundaryContext.getAssets(),
                "fonts/Roboto-Light.ttf");
    }

    /**
     *
     * @param boundaryPosition
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int boundaryPosition, View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(boundaryPosition, convertView, parent);

        view.setTypeface(font);
        return view;
    }


    /**
     *
     * @param boundaryPosition
     * @param convertView
     * @param parent
     * @return
     */
    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int boundaryPosition, View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(boundaryPosition, convertView, parent);
        view.setTypeface(font);
        return view;
    }
}
