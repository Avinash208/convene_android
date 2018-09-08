package org.fwwb.convene.fwwbcode.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.fwwb.convene.fwwbcode.activities.TaskDetailsActivity;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskItemHolder>{

    List<TaskItemBean> taskItemBeanList = new ArrayList<>();
    Context context;

    public TaskAdapter(List<TaskItemBean> taskItemBeanList, Activity context) {
        this.taskItemBeanList = taskItemBeanList;
        this.context = context;
    }

    @Override
    public TaskItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_snippet, parent, false);
        return new TaskItemHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskItemHolder holder, int position) {
        if (holder == null)
            return;
        if (taskItemBeanList.get(position) == null)
            return;
        TaskItemBean taskItemBean = taskItemBeanList.get(position);
        holder.batchNameTv.setText(taskItemBean.getBatchName());
        holder.batchParticipantsTv.setText(""+taskItemBean.getBatchParticipants());
        holder.trainingDateTv.setText(taskItemBean.getTrainingDate());
        holder.trainingLocationTv.setText(taskItemBean.getTrainingStatus()+"");
        holder.trainingNameTv.setText(taskItemBean.getTrainingName());
        holder.taskTile.setOnClickListener(v -> {
         callDetailsActivity(taskItemBean);
        });
    }

    private void callDetailsActivity(TaskItemBean taskItemBean) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra("taskItem",taskItemBean);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return taskItemBeanList.size();
    }

    class TaskItemHolder extends RecyclerView.ViewHolder{

        TextView batchNameTv;
        TextView trainingNameTv;
        TextView trainingLocationTv;
        TextView batchParticipantsTv;
        TextView trainingDateTv;
        LinearLayout taskTile;
        public TaskItemHolder(View itemView) {
            super(itemView);
            batchNameTv = itemView.findViewById(R.id.batchNameTv);
            taskTile = itemView.findViewById(R.id.taskTile);
            trainingNameTv = itemView.findViewById(R.id.trainingNameTv);
            trainingLocationTv = itemView.findViewById(R.id.trainingLocationTv);
            batchParticipantsTv = itemView.findViewById(R.id.batchParticipantsTv);
            trainingDateTv = itemView.findViewById(R.id.trainingDateTv);

        }


    }
}
