package com.fabian.timetohours;

import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    private TimePicker mTpVon, mTpBis;
    private TextView mTvHours;
    private Calendar cal1, cal2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("pref", 0);

        mTpVon = (TimePicker) findViewById(R.id.tp_von);
        mTpBis = (TimePicker) findViewById(R.id.tp_bis);
        mTvHours = (TextView) findViewById(R.id.tv_hours);

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

    }

    public void getDif() {
        cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR, mTpVon.getHour());
        cal1.set(Calendar.MINUTE, mTpVon.getMinute());
        Long von = cal1.getTimeInMillis();

        cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR, mTpBis.getHour());
        cal2.set(Calendar.MINUTE, mTpBis.getMinute());
        Long bis = cal2.getTimeInMillis();

        Long dif = bis - von;
        Float hours = (float)dif/1000/60/60;
        String sHours = String.format("%.2f", hours);
        mTvHours.setText(sHours + " Stunden");
    }
}