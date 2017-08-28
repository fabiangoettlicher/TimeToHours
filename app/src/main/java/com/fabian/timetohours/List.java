package com.fabian.timetohours;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fabian.timetohours.Adapter.ListAdapter;
import com.fabian.timetohours.Utils.ObjectSerializer;

import java.io.IOException;

public class List extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mRvAdapter;
    RecyclerView.LayoutManager mRvLayoutManager;
    TextView mTvNoEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.TA_Blue));

        mTvNoEntries = (TextView) findViewById(R.id.tv_no_entries);
        checkIfListIsEmpty();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_tracking);
        mRvLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRvLayoutManager);

        mRvAdapter = new ListAdapter(new ListAdapter.OnListSizeChangedListener() {
            @Override
            public void onListSizeChanged() {
                checkIfListIsEmpty();
                addListToPrefs();
            }
        });
        mRecyclerView.setAdapter(mRvAdapter);
    }

    public void checkIfListIsEmpty() {
        if(MainActivity.mTrackingEntryList.size()==0){
            mTvNoEntries.setVisibility(View.VISIBLE);
            mTvNoEntries.setText("Keine Einträge verfügbar!");
        }
    }

    public void addListToPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF, 0);
        try {
            sharedPreferences.edit().putString(MainActivity.TRACKING_LIST, ObjectSerializer.serialize(MainActivity.mTrackingEntryList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
