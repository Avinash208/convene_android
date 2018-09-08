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

import org.fwwb.convene.convenecode.BeenClass.parentChild.LevelBeen;
import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SingleSpinnerSearchFilter extends android.support.v7.widget.AppCompatSpinner implements OnCancelListener {
	private static final String TAG = SingleSpinnerSearchFilter.class.getSimpleName();
	private List<LevelBeen> items;

	private String defaultText = "";
	private String spinnerTitle = "";
	private SpinnerListenerFilter listener;
	MyAdapter adapter;
	public static AlertDialog.Builder builder;
	public AlertDialog ad;

	public SingleSpinnerSearchFilter(Context context) {
		super(context);
	}

	public SingleSpinnerSearchFilter(Context arg0, AttributeSet arg1) {
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

	public SingleSpinnerSearchFilter(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}


	public List<LevelBeen> getSelectedItems() {
		List<LevelBeen> selectedItems = new ArrayList<>();
		for(LevelBeen item : items){
			if(item.isSelected()){
				selectedItems.add(item);
			}
		}
		return selectedItems;
	}

	public List<Integer> getSelectedIds() {
		List<Integer> selectedItemsIds = new ArrayList<>();
		for(LevelBeen item : items){
			if(item.isSelected()){
				selectedItemsIds.add(item.getId());
			}
		}
		return selectedItemsIds;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		String spinnerText = null;
		if ("".equals(defaultText)) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isSelected()) {
				spinnerText = items.get(i).getName();
				break;
			}
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

		final ListView listView = (ListView) view.findViewById(R.id.alertSearchListView);

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
		final TextView emptyText = (TextView) view.findViewById(R.id.empty);
		listView.setEmptyView(emptyText);

		EditText editText = (EditText) view.findViewById(R.id.alertSearchEditText);
		editText.addTextChangedListener(new TextWatcher() {


			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.getFilter().filter(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Logger.logD("onTextChanged", "beforeTextChanged");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Logger.logD("onTextChanged", "afterTextChanged");
			}
		});
		builder.setOnCancelListener(this);
		ad = builder.show();
		return true;
	}

	public void setItems(List<LevelBeen> items, int position, SpinnerListenerFilter listener) {

		this.items = items;
		this.listener = listener;

		for (LevelBeen item : items){
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
	public void setFilterItems(List<LevelBeen> items, int position, SpinnerListenerFilter listener) {

		this.items = items;
		this.listener = listener;

		for (LevelBeen item : items){
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



	public class MyAdapter extends BaseAdapter implements Filterable {

		List<LevelBeen> arrayList;
		List<LevelBeen> mOriginalValues;
		LayoutInflater inflater;

		public MyAdapter(Context context, List<LevelBeen> arrayList) {
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			Log.i(TAG, "getView() enter");
			final ViewHolder holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_listview_single, parent, false);
			holder.textView = (TextView) convertView.findViewById(R.id.alertTextView);
			convertView.setTag(holder);

//			if(position%2==0){
//				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_even));
//			}else{
//				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_odd));
//			}

			final LevelBeen data = arrayList.get(position);

			holder.textView.setText(data.getName());
			holder.textView.setTypeface(null, Typeface.NORMAL);
			Log.i(TAG, "selected item "+data.getName());
			convertView.setOnClickListener(new OnClickListener()
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
							if (!"".equals(defaultText) && defaultText!=null) {
								holder.textView.setText(defaultText);
								holder.textView.setTypeface(null, Typeface.NORMAL);
							}
						}
					}
					ad.dismiss();
					SingleSpinnerSearchFilter.this.onCancel(ad);
				}
			});

			if(data.isSelected()){
				holder.textView.setTypeface(null, Typeface.BOLD);
				holder.textView.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
			}
			return convertView;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public Filter getFilter() {
			return new Filter() {
				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {

					arrayList = (List<LevelBeen>) results.values;
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					List<LevelBeen> FilteredArrList = new ArrayList<>();

					if (mOriginalValues == null) {
						mOriginalValues = new ArrayList<>(arrayList);
					}
					if (constraint == null || constraint.length() == 0) {

						results.count = mOriginalValues.size();
						results.values = mOriginalValues;
					} else {
						constraint = constraint.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							Log.i(TAG, "Filter : " + mOriginalValues.get(i).getName() + " -> " + mOriginalValues.get(i).isSelected());
							String data = mOriginalValues.get(i).getName();
							if (data.toLowerCase().contains(constraint.toString())) {
								FilteredArrList.add(mOriginalValues.get(i));
							}
						}
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					return results;
				}
			};
		}
	}
}