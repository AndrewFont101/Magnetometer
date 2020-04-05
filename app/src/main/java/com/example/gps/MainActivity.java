package com.example.gps; // Magnetometer Application

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private TextView t;

    private static SensorManager sensorManager;
    private Sensor sensor;
    private Math math;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView) findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float azimuth = Math.round(sensorEvent.values[0]);
        float pitch = Math.round(sensorEvent.values[1]);
        float roll = Math.round(sensorEvent.values[2]);

        double tesla = math.sqrt((azimuth * azimuth) + (pitch * pitch) + (roll * roll));

        //File internalStorageDirectory = getFilesDir();
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);

        String text = String.format("%.0f", tesla);
        t.setText(text + "μT");
        storeLocationData(text);

        String filename = "myfile.csv";
        String fileContents = "Hello world!";
        File file = new File(externalStorageDirectory, filename);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        t.append("\n\nExternal Media: readable="
                +mExternalStorageAvailable+" writable="+mExternalStorageWriteable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            configure_button();
        }
    }

    private void configure_button() {
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void storeLocationData(String locationData) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 10);
            }

        } else {

            // Find the root of the external storage.
            // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

            //File root = android.os.Environment.getExternalStorageDirectory();

            // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

            //get the path to sdcard
            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            //to this path add a new directory path and create new App dir (InstroList) in /documents Dir
            File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/documents/InstroList");
            // have the object build the directory structure, if needed.
            appDirectory.mkdirs();
            String myNewFileName = "MagnetometerLog.csv";
            File saveFilePath = new File(appDirectory, myNewFileName);


//                File dir = new File (root.getAbsolutePath() + "/download");
//                dir.mkdirs();
//                File file = new File(dir.getAbsolutePath(), "myData.txt");

            try {
                FileOutputStream f = new FileOutputStream(saveFilePath, true);
                OutputStreamWriter OutDataWriter = new OutputStreamWriter(f);
                OutDataWriter.append(locationData); //find out how to add all measurements rather than just the latest measurement
                OutDataWriter.close();
                f.flush();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Log.i(TAG, "******* File not found. Did you" +
                //" add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
    


