package com.fabian.timetohours;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.crashlytics.android.Crashlytics;
import com.fabian.timetohours.Data.TrackingEntry;
import com.fabian.timetohours.Utils.ObjectSerializer;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Serializable{

    public static final String TRACKING_LIST = "trackinglist";
    public static final String SHARED_PREF = "prefs";

    private TimePicker mTpVon, mTpBis;
    private EditText mEtMinus, mEtMessage;
    private TextView mTvHours;
    private SharedPreferences sharedPreferences;
    public static ArrayList<TrackingEntry> mTrackingEntryList;
    private float hours, minus, hoursMinus;
    private String sHours, sHoursMinus;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREF, 0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.TA_Blue));

        try {
            mTrackingEntryList = (ArrayList<TrackingEntry>) ObjectSerializer.deserialize(sharedPreferences.getString(TRACKING_LIST, ObjectSerializer.serialize(new ArrayList<TrackingEntry>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mTrackingEntryList==null) mTrackingEntryList = new ArrayList<>();

        mTpVon = (TimePicker) findViewById(R.id.tp_von);
        mTpBis = (TimePicker) findViewById(R.id.tp_bis);
        mTvHours = (TextView) findViewById(R.id.tv_hours);
        mEtMinus = (EditText) findViewById(R.id.et_minus);
        mEtMessage = (EditText) findViewById(R.id.et_message);

        mTpVon.setIs24HourView(true);
        mTpBis.setIs24HourView(true);

        mTpVon.setHour(sharedPreferences.getInt("hour", 0));
        mTpVon.setMinute(sharedPreferences.getInt("min", 0));

        getDif();

        mTpVon.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int hour = mTpVon.getHour();
                int min = mTpVon.getMinute();
                sharedPreferences.edit().putInt("hour", hour).apply();
                sharedPreferences.edit().putInt("min", min).apply();
                getDif();
            }
        });

        mTpBis.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                getDif();
            }
        });

        mEtMinus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getDif();
            }
        });

        Button mBtnAdd = (Button) findViewById(R.id.btn_add);
        mBtnAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(addToList(v))
                    goToList(v);
                return false;
            }
        });
    }

    public void getDif() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR, mTpVon.getHour());
        cal1.set(Calendar.MINUTE, mTpVon.getMinute());
        Long von = cal1.getTimeInMillis();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR, mTpBis.getHour());
        cal2.set(Calendar.MINUTE, mTpBis.getMinute());
        Long bis = cal2.getTimeInMillis();

        Long dif = bis - von;
        hours = (float)dif/1000/60/60;
        hoursMinus = 0;
        minus = -1;
        if(mEtMinus.getText().length() > 0) {
            minus = Float.valueOf(mEtMinus.getText().toString());
            hoursMinus = hours - minus;
            sHours = String.format("%.2f", hours);
            sHoursMinus = String.format("%.2f", hoursMinus);
            mTvHours.setText(sHours + " - " + minus + " = " + sHoursMinus + " Stunden");
        } else {
            sHours = String.format("%.2f", hours);
            mTvHours.setText(sHours + " Stunden");
        }
    }

    public Boolean addToList (View v) {
        getDif();
        String time;

        String von = String.format("%02d:%02d", mTpVon.getHour(), mTpVon.getMinute());
        String bis = String.format("%02d:%02d", mTpBis.getHour(), mTpBis.getMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currDate = sdf.format(new Date());

        if(minus != -1) {
            time = "Von: " + von + " - Bis: " + bis + "\nMinus: " + minus + " -> Zeit: " + sHoursMinus + " Stunden";
        } else {
            time = "Von: " + von + " - Bis: " + bis + "\nZeit: " + sHours + " Stunden";
        }
        String mMessage = mEtMessage.getText().toString();
        if(mMessage.isEmpty()){
            Toast.makeText(this, "Bitte Message eingeben", Toast.LENGTH_SHORT).show();
        } else {
            if (minus != -1 && hoursMinus >= 0 || minus == -1 && hours >= 0) {
                mTrackingEntryList.add(new TrackingEntry(currDate, time, mMessage));
                mEtMessage.setText("");
                mEtMinus.setText("");
                addListToPrefs();
                Toast.makeText(this, "Zur Liste hinzugefügt", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "Stunden sind negativ", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public void addListToPrefs() {
        try {
            if(mTrackingEntryList!=null)
                sharedPreferences.edit().putString(TRACKING_LIST, ObjectSerializer.serialize(mTrackingEntryList)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void goToList (View v) {
        Intent intent = new Intent(this, List.class);
        startActivity(intent);
    }
}