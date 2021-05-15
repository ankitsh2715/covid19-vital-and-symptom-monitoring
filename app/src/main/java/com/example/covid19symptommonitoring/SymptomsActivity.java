package com.example.covid19symptommonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class SymptomsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String NAUSEA = "nausea";
    public static final String HEADACHE = "headache";
    public static final String DIARRHEA = "diarrhea";
    public static final String SORETHROAT = "sorethroat";
    public static final String FEVER = "fever";
    public static final String MUSCLEACHE = "muscleache";
    public static final String LOSSOFSMELL = "lossofsmell";
    public static final String COUGH = "cough";
    public static final String SHORTNESSBREATH = "shortnessofbreath";
    public static final String TIRED = "feelingtired";

    RatingBar rb;
    String key;
    float value;
    HashMap<String, Float> map;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        initializeHashMap();

        //set up spinner adapter and listener
        Spinner spinner = (Spinner) findViewById(R.id.symptoms_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //set up rating bar and listener
        rb = (RatingBar) findViewById(R.id.rating_bar);
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //update hashmap value for current item (=key)
                map.put(getKey(),rating);
            }
        });

        //set upload symptoms button click listener
        sharedpreferences = getSharedPreferences(MainActivity.MYPREF, Context.MODE_PRIVATE);
        Button upload = (Button) findViewById(R.id.save_symptoms_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSymptomsData();
                Toast.makeText(getApplicationContext(),"Symptoms data saved",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void saveSymptomsData() {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putFloat(NAUSEA, map.get("Nausea"));
        editor.putFloat(HEADACHE, map.get("Headache"));
        editor.putFloat(DIARRHEA, map.get("Diarrhea"));
        editor.putFloat(SORETHROAT, map.get("Sore Throat"));
        editor.putFloat(FEVER, map.get("Fever"));
        editor.putFloat(MUSCLEACHE, map.get("Muscle Ache"));
        editor.putFloat(LOSSOFSMELL, map.get("Loss of Smell or Taste"));
        editor.putFloat(COUGH, map.get("Cough"));
        editor.putFloat(SHORTNESSBREATH, map.get("Shortness of Breath"));
        editor.putFloat(TIRED, map.get("Feeling Tired"));

        editor.apply();

    }

    private void initializeHashMap() {
        map = new HashMap<>();
        map.put("Nausea",0.0f);
        map.put("Headache",0.0f);
        map.put("Diarrhea",0.0f);
        map.put("Sore Throat",0.0f);
        map.put("Fever",0.0f);
        map.put("Muscle Ache",0.0f);
        map.put("Loss of Smell or Taste",0.0f);
        map.put("Cough",0.0f);
        map.put("Shortness of Breath",0.0f);
        map.put("Feeling Tired",0.0f);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String symptom = parent.getItemAtPosition(position).toString();
        //set key value to current item selected in spinner
        setKey(symptom);
        //update rating bar for value stored
        rb.setRating(map.get(symptom));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setKey(String k) {
        key = k;
    }

    private String getKey() {
        return key;
    }
}