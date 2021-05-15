package com.example.covid19symptommonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

public class VitalsActivity extends AppCompatActivity {

    private final static int REQUEST_ID = 101;
    private static final int VIDEO_TIME = 45;
    private static final String VIDEO_FILE_NAME = "heart_rate.mp4";

    public static final String HEARTRATE = "heartrate";
    public static final String RESPRATE = "resprate";

    SharedPreferences sharedpreferences;
    private TextView heartRateTv;
    private TextView respRateTv;
    int heartRate=0;
    int respRate=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        Button heartRateBtn = (Button) findViewById(R.id.measureHR);
        Button respRateBtn = (Button) findViewById(R.id.measureRR);
        Button saveBtn = (Button) findViewById(R.id.save_btn);
        heartRateTv = (TextView) findViewById(R.id.heartRateTv);
        respRateTv = (TextView) findViewById(R.id.respRateTv);

        heartRateBtn.setOnClickListener(v -> recordVideo());

        respRateBtn.setOnClickListener(v -> {
            Intent respRateService = new Intent(VitalsActivity.this, RespRateService.class);
            startService(respRateService);
            respRateTv.setText("Calculating ...");
        });

        sharedpreferences = getSharedPreferences(MainActivity.MYPREF, Context.MODE_PRIVATE);
        saveBtn.setOnClickListener(v -> {
            saveVitals();
            Toast.makeText(getApplicationContext(),"Vitals data saved",Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveVitals() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(HEARTRATE, heartRate);
        editor.putInt(RESPRATE, respRate);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(VitalsActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("RESP_RATE_SERVICE"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(VitalsActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            respRate = intent.getIntExtra("RESP_RATE",-1);
            respRateTv.setText(respRate+"");
        }
    };

    private void recordVideo() {
        File fileVideo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + VIDEO_FILE_NAME);

        Intent record = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        record.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_TIME);

        Uri fileUri = FileProvider.getUriForFile(this,
                "com.example.covid19symptommonitoring.provider", fileVideo);

        record.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(record, REQUEST_ID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != RESULT_OK) return;

        Toast.makeText(this,"Video Location is "+ Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + VIDEO_FILE_NAME,Toast.LENGTH_SHORT).show();

        new HeartRateAsync().execute();

    }

    private class HeartRateAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Toast.makeText(VitalsActivity.this,"Calculating...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            File videoFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),VIDEO_FILE_NAME);

            Uri videoFileUri=Uri.parse(videoFile.toString());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());

            MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);
            int time = mp.getDuration();

            int crpWidth = 100;
            int crpHeight = 100;

            ArrayList<Float> meanRedIntensity = new ArrayList<Float>();
            ArrayList<Float> diffList = new ArrayList<Float>();
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            for(int i=0; i<time ; i=i+100){

                //get a frame at every 100 milliseconds. Therefore 450 data-points for a 45 sec video.
                //croppedBitmap is a centered bitmap of crpWidth and crpHeight
                int sum = 0;
                Bitmap croppedBitmap = ThumbnailUtils.extractThumbnail(retriever.getFrameAtTime(i*1000,MediaMetadataRetriever.OPTION_CLOSEST), crpWidth, crpHeight);

                //stored pixels values in px[]
                int[] px = new int[crpWidth * crpHeight];
                croppedBitmap.getPixels(px,0, crpWidth,0,0,crpWidth,crpHeight);

                //get red value for a px. Calculate sum of all red values.
                for (int j = 0 ; j < crpWidth*crpHeight; j++) {
                    int redIntensity = (px[j] & 0xff0000) >> 16;
                    sum = sum + redIntensity;
                }
                //store average red color of each frame in meanRedIntensity
                meanRedIntensity.add((float)sum/(crpWidth*crpHeight));

                //progress calculation for heartRate TextView
                float perc = (i/(float)time)*100;
                String p = formatter.format(perc);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        heartRateTv.setText(p+"% processed");
                    }
                });
            }

            //create a diffList to store changes in redInstensity values in recorded frames.
            for(int i=0; i<meanRedIntensity.size()-1; i++) {
                diffList.add(Math.abs(meanRedIntensity.get(i)-meanRedIntensity.get(i+1)));
            }

            float noise = 0.1f;
            int peak = 0;

            //peak calulation
            for(int i=1; i<diffList.size(); i++) {
                if(diffList.get(i-1)>noise && diffList.get(i)<noise) {
                    peak = peak + 1;
                }
            }

            //heartRate upscaled to a min
            heartRate = (60*peak/(time/1000))/2;
            Log.d("Ankit","Heart rate = "+heartRate);
            retriever.release();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            heartRateTv.setText(heartRate + " bpm");
        }
    }
}