package org.fwwb.convene.convenecode.utils.multispinner;

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

import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SingleSpinnerSearch extends android.support.v7.widget.AppCompatSpinner implements OnCancelListener {
	private static final String TAG = SingleSpinnerSearch.class.getSimpleName();
	private List<Datum> items;
	private String defaultText = "";
	private String spinnerTitle = "";
	private SpinnerListener listener;
	MyAdapter adapter;
	public  AlertDialog.Builder builder;
	public  AlertDialog ad;

	public SingleSpinnerSearch(Context context) {
		super(context);
	}

	public SingleSpinnerSearch(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch);
		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.MultiSpinnerSearch_hintText) {
				spinnerTitle = a.getString(attr);
				defaultText = spinnerTitle;
				break;
			}
		}
		Log.i(TAG, "spinnerTitle: "+spinnerTitle);
		a.recycle();
	}

	public SingleSpinnerSearch(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}





	@Override
	public void onCancel(DialogInterface dialog) {
		// refresh text on spinner
		String spinnerText = null;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isSelected()) {
				spinnerText = items.get(i).getName();
				break;
			}
		}
		if (spinnerText==null){
			spinnerText = defaultText;
		}

		ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.textview_for_spinner, new String[] { spinnerText });
		setAdapter(adapterSpinner);

		if(adapter != null)
			adapter.notifyDataSetChanged();
		listener.onItemsSelected(items);
	}

	@Override
	public boolean performClick() {

		builder = new AlertDialog.Builder(getContext(), R.style.myDialog);
		builder.setTitle(spinnerTitle);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View view = inflater.inflate(R.layout.alert_dialog_listview_search, null);
		builder.setView(view);
		final ListView listView = view.findViewById(R.id.alertSearchListView);

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setFastScrollEnabled(false);
		adapter = new MyAdapter(getContext(), items);
		listView.setAdapter(adapter);
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
				adapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Logger.logD("SpinnerSearch","before text change SpinnerSearch");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Logger.logD("SpinnerSearch","before text change SpinnerSearch");
			}
		});

		builder.setOnCancelListener(this);
		ad = builder.show();
		return true;
	}

	public void setItems(List<Datum> items, int position, SpinnerListener listener) {

		this.items = items;
		this.listener = listener;

		for (Datum item : items){
			if(item.isSelected()){
				defaultText = item.getName();
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

		List<Datum> arrayList;
		List<Datum> mOriginalValues; // Original Values
		LayoutInflater inflater;

		public MyAdapter(Context context, List<Datum> arrayList) {
			this.arrayList = arrayList;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return arrayList.size();
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
			ViewHolder holder = new ViewHolder();
			view = inflater.inflate(R.layout.item_listview_single, parent, false);
			holder.textView = view.findViewById(R.id.alertTextView);
			view.setTag(holder);

			if(position%2==0){
				view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_even));
			}else{
				view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_odd));
			}

			final Datum data = arrayList.get(position);

			holder.textView.setText(data.getName());
			holder.textView.setTypeface(null, Typeface.NORMAL);

			view.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					int len = arrayList.size();
					for (int i = 0; i < len; i++)
					{
						arrayList.get(i).setSelected(false);
						if (i == position)
						{
							arrayList.get(i).setSelected(true);

							defaultText = arrayList.get(i).getName();
							Log.i(TAG, "On Click Selected Item : " + arrayList.get(i).getName() + " : " + arrayList.get(i).isSelected());
						}
					}
					ad.dismiss();
					SingleSpinnerSearch.this.onCancel(ad);
				}
			});

			if(data.isSelected()){
				holder.textView.setTypeface(null, Typeface.BOLD);
				holder.textView.setTextColor(Color.WHITE);
				view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
			}
			return view;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public Filter getFilter() {
			return new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {

					arrayList = (List<Datum>) results.values; // has the filtered values
					notifyDataSetChanged();  // notifies the data with new filtered values
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
					List<Datum> filteredArrList = new ArrayList<>();

					if (mOriginalValues == null) {
						mOriginalValues = new ArrayList<>(arrayList); // saves the original data in mOriginalValues
					}

					/*
					 * 
					 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
					 *  else does the Filtering and returns filteredArrList(Filtered)
					 *
					 ********/
					if (constraint == null || constraint.length() == 0) {

						// set the Original result to return  
						results.count = mOriginalValues.size();
						results.values = mOriginalValues;
					} else {
						for (int i = 0; i < mOriginalValues.size(); i++) {
							String data = mOriginalValues.get(i).getName();
							if((i==0)&& (filteredArrList.isEmpty())){
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