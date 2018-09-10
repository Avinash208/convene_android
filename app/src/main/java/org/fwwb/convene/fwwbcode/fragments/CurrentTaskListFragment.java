package org.fwwb.convene.fwwbcode.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.fwwb.convene.fwwbcode.FwwbUtility;
import org.fwwb.convene.fwwbcode.adapters.TaskAdapter;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListListeners;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListPresenters;

import java.util.ArrayList;
import java.util.List;


/**
     * A placeholder fragment containing a simple view.
     */
    public  class CurrentTaskListFragment extends Fragment implements TaskListListeners {

    public TextView emptyTextView;
    public RecyclerView listView;
    public List<TaskItemBean>  itemBeanList = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        TaskListPresenters presenters = new TaskListPresenters(getActivity(), this);
    }

    public CurrentTaskListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CurrentTaskListFragment newInstance() {
            CurrentTaskListFragment fragment = new CurrentTaskListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_task_selection_listing, container, false);

            emptyTextView = rootView.findViewById(R.id.emptyListError);
            listView = rootView.findViewById(R.id.taskList);
            TaskListPresenters presenters = new TaskListPresenters(getActivity(), this);
            setAdapter();
            return rootView;
        }


    public void setAdapter()
        {
            if (listView == null)
                return;
            if (itemBeanList== null ||itemBeanList.isEmpty())
            {
                listView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }
            else
            {
                itemBeanList =  FwwbUtility.sortList(itemBeanList, "dd-MM-yyyy");
                listView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                TaskAdapter adapter = new TaskAdapter(itemBeanList,getActivity());
                GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 1);
                listView.setLayoutManager(lLayout);
                listView.setAdapter(adapter);
            }

        }


        public void setTaskList(List<TaskItemBean>  itemBeanList)
        {
            this.itemBeanList = itemBeanList;
            setAdapter();
        }

    @Override
    public void currentTaskList(List<TaskItemBean> currentList) {
        this.itemBeanList = currentList;
        setAdapter();
    }

    @Override
    public void upcomingTaskList(List<TaskItemBean> upcomingList) {

    }

    @Override
    public void recentTaskList(List<TaskItemBean> recentList) {

    }
}
