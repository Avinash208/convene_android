package org.mahiti.convenemis.adapter.spinnercustomadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.Beneficiarylinkages;
import org.mahiti.convenemis.ListingActivity;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.SurveyQuestionActivity;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by mahiti on 16/01/18.
 */


public class ListingGridViewAdapter extends RecyclerView.Adapter<ListingGridViewAdapter.ViewHolder> {

    Context context;
    Activity activity;
    List<StatusBean> statusbean;
    private  String qid;
    private  ConveneDatabaseHelper dbConveneHelper;
    DBHandler surveySummaryreportdbhandler;
    SharedPreferences sharedPreferences;
    SharedPreferences prefs;
    String surveyId;
    String getheading;



    public ListingGridViewAdapter(ListingActivity surveySummaryReport, List<StatusBean> statusBeanList, String qid, ConveneDatabaseHelper dbConveneHelper,
                                  DBHandler surveySummaryreportdbhandler, SharedPreferences sharedPreferences, SharedPreferences prefs, String id, String getHeading) {
        this.context = surveySummaryReport;
        statusbean = statusBeanList;
        activity = surveySummaryReport;
        this.qid=qid;
        this.dbConveneHelper=dbConveneHelper;
        this.surveySummaryreportdbhandler=surveySummaryreportdbhandler;
        this.sharedPreferences=sharedPreferences;
        this.prefs=prefs;
        this.surveyId=id;
        this.getheading=getHeading;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.surveysummary_detail_row, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ListingGridViewAdapter.ViewHolder viewHolder, final int position) {

        if(!"".equals(qid) && qid.contains(","))
        {
            String summary="";
            String[] qids=qid.split(",");
            for(int k=0;k<qids.length;k++)
            {
                summary= new StringBuilder().append(summary).append(DBHandler.getAnswerFromPreviousQuestion(qids[k], surveySummaryreportdbhandler, String.valueOf(statusbean.get(position).getSurveyId()))).toString();
            }
            Logger.logD("Getting many times","->"+position);
            viewHolder.anniversariesListDymanicLabel.removeAllViews();
            setParentView(viewHolder.dynamicImage,statusbean.get(position).getQuestionAnswerList(),
                    viewHolder.nameLabel);

        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguageTOSP(statusbean.get(position).getLanguage());
                Intent intent = new Intent(activity, Beneficiarylinkages.class);
                intent.putExtra("SurveyId", statusbean.get(position).getSurveyId());
                intent.putExtra(Constants.HEADER_NAME, getheading);
                intent.putExtra("parentID", String.valueOf(statusbean.get(position).getQuestionAnswerList().get(0).getParentId()));
                intent.putExtra("parent_form_primaryid", String.valueOf(statusbean.get(position).getParent_form_primaryid()));
                intent.putExtra(Constants.SURVEY_ID, surveyId);
                context.startActivity(intent);
            }
        });
        viewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguageTOSP(statusbean.get(position).getLanguage());
                Utilities.setSurveyStatus(sharedPreferences,"edit");
                Intent intent = new Intent(activity, SurveyQuestionActivity.class);
                intent.putExtra("SurveyId", statusbean.get(position).getSurveyId());
                intent.putExtra(Constants.SURVEY_ID, surveyId);
                context.startActivity(intent);
            }
        });
    }

    private void updateLanguageTOSP(String languageid) {
       if (languageid!=null && !languageid.isEmpty()){
           SharedPreferences.Editor editor = prefs.edit();
           editor.putInt(Constants.SELECTEDLANGUAGE, Integer.parseInt(languageid));
           editor.apply();
       }

    }


    @Override
    public int getItemCount() {
        return statusbean.size();
    }


    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameLabel;
        private LinearLayout anniversariesListDymanicLabel;
        private ImageView editbtn;
        private ImageView dynamicImage;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            anniversariesListDymanicLabel = (LinearLayout) view.findViewById(R.id.linearLayout);
            cardView = (CardView) view.findViewById(R.id.complete_cardview);
            nameLabel = (TextView) view.findViewById(R.id.name_label);
            editbtn = (ImageView) view.findViewById(R.id.editbtn);
            dynamicImage = (ImageView) view.findViewById(R.id.dynamic_image);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setParentView(ImageView imageView, List<QuestionAnswer> questionAnswersList, TextView nameLabel) {
        try {
            for (int i=0;i<questionAnswersList.size();i++){
                nameLabel.setText(questionAnswersList.get(0).getAnswerText());
                if (!questionAnswersList.get(i).getQuestionText().equalsIgnoreCase(Constants.Gender))
                    setImageDynamically(imageView,getheading,"");
                else
                    setImageDynamically(imageView,getheading,questionAnswersList.get(i).getAnswerText());


            }
        } catch (Exception e) {
            Logger.logE(TAG,"exception here",e);
        }
    }

    private void setImageDynamically(ImageView imageView, String headingLabel, String getGender) {
        switch (headingLabel){
            case Constants.GROUP:
                imageView.setBackgroundResource(R.drawable.group_icon);

                break;
            case Constants.FPO:
                imageView.setBackgroundResource(R.drawable.federation_icon);
                break;
            case Constants.HOUSEHOLDS:
               if (getGender.equalsIgnoreCase(Constants.MALE))
                    imageView.setBackgroundResource(R.drawable.household_male);
               else if (getGender.equalsIgnoreCase(Constants.FEMALE))
                   imageView.setBackgroundResource(R.drawable.household_female);
               else
                   imageView.setBackgroundResource(R.drawable.profile_none);
                break;
            case Constants.FARMERS:
                imageView.setBackgroundResource(R.drawable.farmer_icon);
                break;
             default:
                 imageView.setBackgroundResource(R.drawable.profile_none);
                 break;
        }

    }
}
