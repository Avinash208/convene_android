package org.mahiti.convenemis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class HomeTileAdapter extends RecyclerView.Adapter<HomeTileAdapter.MyTaskAdapterViews> {
    private  Context context;
    private  int selectedFragment;
    private List<QuestionAnswer> selectedList;
    private  List<QuestionAnswer> schoolTypeBeanList;
    private boolean checkBoxVisible = false;
    private ItemSelectedListener itemSelectedListenerListener;

    private List<QuestionAnswer> selectedItems = new ArrayList<>();




    public HomeTileAdapter(Context activity, List<QuestionAnswer> schoolTypeBeanList, int selectedFragment, ItemSelectedListener listener, List<QuestionAnswer> selectedList, boolean checkBoxVisible) {
        this.itemSelectedListenerListener = listener;
        this.context = activity;
        this.schoolTypeBeanList = schoolTypeBeanList;
        this.selectedFragment = selectedFragment;
        this.selectedList = selectedList;
        if (selectedList != null)
            this.selectedItems = selectedList;
        this.checkBoxVisible = checkBoxVisible;
        itemSelectedListenerListener.itemSelected(selectedItems);

    }

    @Override
    public MyTaskAdapterViews onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_home_tile, parent, false);

        return new MyTaskAdapterViews(view);
    }

    @Override
    public void onBindViewHolder(final MyTaskAdapterViews holder, final int position) {

                showSchoolVisitView(position,holder);

    }

    private void showSchoolVisitView(final int position, final MyTaskAdapterViews holder) {
        final QuestionAnswer schoolTypeBean = schoolTypeBeanList.get(position);
        if (isContains(selectedList,schoolTypeBean))
        {
            schoolTypeBean.setIsActive(1);
            checkBoxVisible = true;
        }
        if (checkBoxVisible)
            holder.checkBoxImage.setVisibility(View.VISIBLE);
        else
            holder.checkBoxImage.setVisibility(View.GONE);

        holder.textView.setText(schoolTypeBean.getAnswerText());
        holder.tileVillage.setText(MessageFormat.format("Village: {0}", schoolTypeBean.getQuestionText()));
        //holder.backgroundImage.setImageDrawable(context.getResources().getDrawable(R.drawable.school_yellow));
        checkBoxVisible = true;
        holder.checkBoxImage.setVisibility(View.VISIBLE);
        holder.checkBoxImage.setImageDrawable(context.getResources().getDrawable(R.drawable.iic_check_box_outline_blank_pink_400_24dp));

        if (schoolTypeBean.getIsActive() == 0)
        {
            holder.checkBoxImage.setImageDrawable(context.getResources().getDrawable(R.drawable.iic_check_box_outline_blank_pink_400_24dp));
            holder.checkBoxImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    schoolTypeBeanList.get(position).setIsActive(1);
                    insertItem(schoolTypeBean);
                    notifyItemChanged(position);
                }
            });

            if (checkBoxVisible)
            {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        schoolTypeBeanList.get(position).setIsActive(1);
                        insertItem(schoolTypeBean);
                        notifyItemChanged(position);
                    }
                });
            }
        }
        else
        {
            holder.checkBoxImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_box_pink_400_24dp));
            holder.checkBoxImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    schoolTypeBeanList.get(position).setIsActive(0);
                    notifyItemChanged(position);
                    removeItem(schoolTypeBean);
                }
            });

            if (checkBoxVisible)
            {
                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        schoolTypeBeanList.get(position).setIsActive(0);
                        notifyItemChanged(position);
                        removeItem(schoolTypeBean);
                    }
                });
            }
        }






    }
    private boolean isContains(List<QuestionAnswer> selectedList, QuestionAnswer schoolTypeBean) {
        for (QuestionAnswer schoolTypeBean1 : selectedList)
        {
            if (schoolTypeBean1.getAnswerText().equalsIgnoreCase(schoolTypeBean.getAnswerText()))
                return true;
        }

        return false;
    }

    private void removeItem(QuestionAnswer schoolTypeBean) {
       if (isContains(selectedItems,schoolTypeBean))
       {
           for (int i=0;i<selectedItems.size();i++){
               if(selectedItems.get(i).getAnswerText().equals(schoolTypeBean.getAnswerText())){
                   selectedItems.remove(i);
               }
           }
           itemSelectedListenerListener.itemSelected(selectedItems);
       }
    }

    private void insertItem(QuestionAnswer schoolTypeBean) {
        if (!isContains(selectedItems,schoolTypeBean))
            selectedItems.add(schoolTypeBean);
        itemSelectedListenerListener.itemSelected(selectedItems);
    }

    @Override
    public int getItemCount() {
        return schoolTypeBeanList.size();
    }

    class MyTaskAdapterViews extends RecyclerView.ViewHolder {

        ImageView checkBoxImage;

        TextView textView;
        TextView tileVillage;
        LinearLayout rootView;




        MyTaskAdapterViews(View itemView) {
            super(itemView);
            checkBoxImage = (ImageView) itemView.findViewById(R.id.tileCheckbox);

            textView = (TextView) itemView.findViewById(R.id.tileTitle);
            tileVillage = (TextView) itemView.findViewById(R.id.tileVillage);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootView);


        }
    }

    public void onBackPressed()
    {

    }

    public interface ItemSelectedListener {
       void itemSelected(List<QuestionAnswer> selectedList);

    }

}