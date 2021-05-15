package com.example.covid19symptommonitoring;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RespRateService extends Service implements SensorEventListener {

    int sampleSize = 225;
    int sampleTime = 45;
    float[] z = new float[sampleSize];
    int index = 0;
    boolean isDone;

    public RespRateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isDone = false;

        SensorManager accelManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accel = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        Toast.makeText(getApplicationContext(),"Recording respiratory rate",Toast.LENGTH_SHORT).show();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            if(index < sampleSize) {
                z[index] = event.values[2];
            } else if(index >= sampleSize) {
                if(!isDone)
                    calcRespRate();
                //to prevent onSensorChanged calling calRespRate() repeatedly
                isDone = true;
            }
            
        }
    }

    //this method was taught by professor in class. Changed noise value to 0.15 as this gave me correct results.
    private void calcRespRate() {

        float[] diff = new float[sampleSize-1];

        for(int i=0; i<sampleSize-1; i++) {
            diff[i] = Math.abs(z[i+1] - z[i]);
        }

        float noise = 0.15f;
        int peak = 0;

        for(int i=0; i<sampleSize-1; i++) {
            if(diff[i]<noise && diff[i-1]>noise) {
                peak = peak + 1;
            }
        }

        int breathingRate = (60*peak/sampleTime)/2;
        Log.d("Ankit","Breathing rate = "+breathingRate);
        sendDataToActivity(breathingRate);
        stopSelf();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendDataToActivity(int rate) {
        Intent in = new Intent();
        in.putExtra("RESP_RATE",rate);
        in.setAction("RESP_RATE_SERVICE");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }
}