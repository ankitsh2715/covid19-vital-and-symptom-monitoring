package com.example.covid19symptommonitoring;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = MainActivity.USER_LASTNAME;

    // Table columns
    public static final String _ID = "_id";
    public static final String HEARTRATE_COL = "heartrate";
    public static final String RESPRATE_COL = "resprate";
    public static final String NAUSEA_COL = "nausea";
    public static final String HEADACHE_COL = "headache";
    public static final String DIARRHEA_COL = "diarrhea";
    public static final String SORETHROAT_COL = "sorethroat";
    public static final String FEVER_COL = "fever";
    public static final String MUSCLEACHE_COL = "muscleache";
    public static final String LOSSOFSMELL_COL = "lossofsmell";
    public static final String COUGH_COL = "cough";
    public static final String SHORTNESSBREATH_COL = "shortnessofbreath";
    public static final String TIRED_COL = "feelingtired";

    // Database Information
    static final String DB_NAME = "COVID19_HEALTH_RECORD.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE =
            "create table " + TABLE_NAME
            + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HEARTRATE_COL + " INTEGER, "
            + RESPRATE_COL + " INTEGER, "
            + NAUSEA_COL + " REAL, "
            + HEADACHE_COL + " REAL, "
            + DIARRHEA_COL + " REAL, "
            + SORETHROAT_COL + " REAL, "
            + FEVER_COL + " REAL, "
            + MUSCLEACHE_COL + " REAL, "
            + LOSSOFSMELL_COL + " REAL, "
            + COUGH_COL + " REAL, "
            + SHORTNESSBREATH_COL + " REAL, "
            + TIRED_COL + " REAL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
