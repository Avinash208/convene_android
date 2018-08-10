package org.assistindia.convene.utils.multispinner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import org.assistindia.convene.BeenClass.parentChild.LevelBeen;
import org.assistindia.convene.R;
import org.assistindia.convene.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SpinnerClusterSearch extends android.support.v7.widget.AppCompatSpinner implements OnCancelListener {
	private static final String TAG = SpinnerClusterSearch.class.getSimpleName();
	private List<LevelBeen> items;
	private String defaultText = "";
	private String spinnerTitle = "";
	private SpinnerClusterListener listener;
	MyAdapter myAdapter;
	public  AlertDialog.Builder builder;
	public  AlertDialog ad;

	public SpinnerClusterSearch(Context context) {
		super(context);
	}

	public SpinnerClusterSearch(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		TypedArray typedArray = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch);
		final int N = typedArray.getIndexCount();
		for (int i = 0; i < N; ++i) {
			int attr = typedArray.getIndex(i);
			if (attr == R.styleable.MultiSpinnerSearch_hintText) {
				spinnerTitle = typedArray.getString(attr);
				defaultText = spinnerTitle;
				break;
			}
		}
		Log.i(TAG, "spinnerTitle: "+spinnerTitle);
		typedArray.recycle();
	}

	public SpinnerClusterSearch(Context context, AttributeSet attributeSet, int arg2) {
		super(context, attributeSet, arg2);
	}




	@Override
	public void onCancel(DialogInterface dialog) {
		// refresh text on spinner
		String spinnerTextString = null;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isSelected()) {
				spinnerTextString = items.get(i).getName();
				break;
			}
		}
		if (spinnerTextString==null){
			spinnerTextString = defaultText;
		}

		ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.textview_for_spinner, new String[] { spinnerTextString });
		setAdapter(stringArrayAdapter);

		if(myAdapter != null)
			myAdapter.notifyDataSetChanged();
		listener.onItemsSelected(items);
	}

	@Override
	public boolean performClick() {

		builder = new AlertDialog.Builder(getContext(), R.style.myDialog);
		builder.setTitle(spinnerTitle);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View view = inflater.inflate(R.layout.alert_dialog_listview_search_location, null);
		builder.setView(view);
		final ListView listView = view.findViewById(R.id.alertSearchListView);

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setFastScrollEnabled(false);
		myAdapter = new MyAdapter(getContext(), items);
		listView.setAdapter(myAdapter);
		for(int i =0;i<items.size();i++){
			if(items.get(i).isSelected()){
				listView.setSelection(i);
				break;
			}
		}
		final TextView emptyText = view.findViewById(R.id.empty);
		listView.setEmptyView(emptyText);

		EditText editText = view.findViewById(R.id.alertSearchEditText);
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				myAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Logger.logD("SpinnerClusterSearch","before text change ");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Logger.logD("SpinnerClusterSearch","after text change ");
			}
		});

		builder.setOnCancelListener(this);
		ad = builder.show();
		return true;
	}

	public void setItems(List<LevelBeen> items, int position, SpinnerClusterListener listener) {

		this.items = items;
		this.listener = listener;

		for (LevelBeen been : items){
			if(been.isSelected()){
				defaultText = been.getName();
				break;
			}
		}

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.textview_for_spinner, new String[] { defaultText });
		setAdapter(adapterSpinner);
		if(position != -1)
		{
			items.get(position).setSelected(true);
			onCancel(null);
		}
	}


	//Adapter Class
	public class MyAdapter extends BaseAdapter implements Filterable {

		List<LevelBeen> beenList;
		List<LevelBeen> mOriginalValues; // Original Values
		LayoutInflater inflater;
		Typeface face;

		private MyAdapter(Context context, List<LevelBeen> levelBeenList) {
			this.beenList = levelBeenList;
			inflater = LayoutInflater.from(context);
			face = Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Light.ttf");
		}

		@Override
		public int getCount() {
			return beenList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView textView;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			Log.i(TAG, "getView() enter");
			final ViewHolder viewHolder = new ViewHolder();
			View convertView = inflater.inflate(R.layout.item_listview_single, parent, false);
			viewHolder.textView = convertView.findViewById(R.id.alertTextView);
			convertView.setTag(viewHolder);

			if(position%2==0){
				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_even));
			}else{
				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_odd));
			}

			final LevelBeen data = beenList.get(position);

			viewHolder.textView.setText(data.getName());
			viewHolder.textView.setTypeface(null, Typeface.NORMAL);

			convertView.setOnClickListener(v -> {
                setSelectedItemView(viewHolder,beenList,position,face);
                ad.dismiss();
                SpinnerClusterSearch.this.onCancel(ad);
            });

			if(data.isSelected()){
				viewHolder.textView.setTypeface(face);
				viewHolder.textView.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
			}
			return convertView;
		}

		private void setSelectedItemView(MyAdapter.ViewHolder v, List<LevelBeen> beenList, int position, Typeface face) {
			int len = beenList.size();
			for (int i = 0; i < len; i++)
			{
				beenList.get(i).setSelected(false);
				if (i == position)
				{
					beenList.get(i).setSelected(true);
					Logger.logD("","selected search item" + beenList.get(i).isSelected());
					defaultText = beenList.get(i).getName();
					if (!"".equals(defaultText) && defaultText!=null) {
						v.textView.setText(defaultText);
						v.textView.setTypeface(face);
					}
				}
			}
		}

		@SuppressLint("DefaultLocale")
		@Override
		public Filter getFilter() {
			return new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {

					beenList = (List<LevelBeen>) results.values; // has the filtered values
					notifyDataSetChanged();  // notifies the data with new filtered values
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
					List<LevelBeen> filteredArrList = new ArrayList<>();

					if (mOriginalValues == null) {
						mOriginalValues = new ArrayList<>(beenList); // saves the original data in mOriginalValues
					}

					/*
					 * 
					 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
					 *  else does the Filtering and returns FilteredArrList(Filtered)  
					 *
					 ********/
					if (constraint == null || constraint.length() == 0) {

						// set the Original result to return  
						results.count = mOriginalValues.size();
						results.values = mOriginalValues;
					} else {
						constraint = constraint.toString().toLowerCase();
						Logger.logD(TAG,"constraint values" + constraint);
						for (int i = 0; i < mOriginalValues.size(); i++) {
							String data = mOriginalValues.get(i).getName();
							if(i==0 && (filteredArrList.isEmpty())){
									filteredArrList.add(mOriginalValues.get(0));
							}
							if (data.toLowerCase().contains(constraint.toString())&& i!=0) {
								filteredArrList.add(mOriginalValues.get(i));
							}else{
								mOriginalValues.get(i).setSelected(false);
							}
						}
						// set the Filtered result to return
						results.count = filteredArrList.size();
						results.values = filteredArrList;
					}
					return results;
				}
			};
		}
	}


}