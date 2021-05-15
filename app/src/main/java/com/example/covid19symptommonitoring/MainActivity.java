package com.example.covid19symptommonitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private static final int DEFAULT_VALUE_VITALS = -1;
    private static final float DEFAULT_VALUE_SYMPTOMS = 0.0f;

    public static final String MYPREF = "mypref";
    public static final String USER_LASTNAME = "Sharma";

    private DBManager dbManager;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        dbManager = new DBManager(this);
        dbManager.open();

        Button vitalBtn = (Button) findViewById(R.id.measure_vital);
        Button symptomBtn = (Button) findViewById(R.id.measure_symptoms);
        Button uploadBtn = (Button) findViewById(R.id.upload_data);

        vitalBtn.setOnClickListener(v -> {
            Intent vitalActivity = new Intent(MainActivity.this, VitalsActivity.class);
            startActivity(vitalActivity);
        });

        symptomBtn.setOnClickListener(v -> {
            Intent vitalActivity = new Intent(MainActivity.this, SymptomsActivity.class);
            startActivity(vitalActivity);
        });

        uploadBtn.setOnClickListener(v -> saveToDb());

    }

    private void saveToDb() {
        dbManager.insert(
                sharedpreferences.getInt(VitalsActivity.HEARTRATE, DEFAULT_VALUE_VITALS),
                sharedpreferences.getInt(VitalsActivity.RESPRATE, DEFAULT_VALUE_VITALS),
                sharedpreferences.getFloat(SymptomsActivity.NAUSEA, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.HEADACHE, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.DIARRHEA, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.SORETHROAT, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.FEVER, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.MUSCLEACHE, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.LOSSOFSMELL, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.COUGH, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.SHORTNESSBREATH, DEFAULT_VALUE_SYMPTOMS),
                sharedpreferences.getFloat(SymptomsActivity.TIRED, DEFAULT_VALUE_SYMPTOMS)
        );

        Toast.makeText(this,"Saved to database",Toast.LENGTH_SHORT).show();
        //clear shared pref values after saving it to db
        sharedpreferences.edit().clear().apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}