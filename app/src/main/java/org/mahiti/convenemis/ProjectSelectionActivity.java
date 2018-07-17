package org.mahiti.convenemis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection);
        initVariables();
    }

    private void initVariables() {
        TextView toolbarTextView=(TextView) findViewById(R.id.toolbarTitle);
        ImageView searchIcon= (ImageView) findViewById(R.id.imageMenu);
        searchIcon.setVisibility(View.VISIBLE);
        toolbarTextView.setText(R.string.projectandactivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
