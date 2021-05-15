package com.example.covid19symptommonitoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(int heartrate, int resprrate,
                       float nausea, float headache, float diarrhea, float sorethroat, float fever, float muscleache, float lossofsmell, float cough, float shortnessofbreath, float feelingtired ) {

        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.HEARTRATE_COL, heartrate);
        contentValue.put(DatabaseHelper.RESPRATE_COL, resprrate);
        contentValue.put(DatabaseHelper.NAUSEA_COL, nausea);
        contentValue.put(DatabaseHelper.HEADACHE_COL, headache);
        contentValue.put(DatabaseHelper.DIARRHEA_COL, diarrhea);
        contentValue.put(DatabaseHelper.SORETHROAT_COL, sorethroat);
        contentValue.put(DatabaseHelper.FEVER_COL, fever);
        contentValue.put(DatabaseHelper.MUSCLEACHE_COL, muscleache);
        contentValue.put(DatabaseHelper.LOSSOFSMELL_COL, lossofsmell);
        contentValue.put(DatabaseHelper.COUGH_COL, cough);
        contentValue.put(DatabaseHelper.SHORTNESSBREATH_COL, shortnessofbreath);
        contentValue.put(DatabaseHelper.TIRED_COL, feelingtired);

        long id = database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

}
