package com.birjot.helprequest;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;

import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class HRActivity1 extends AppCompatActivity implements SensorEventListener , LocationListener {


   // TextView txtSensodData;
    TextView txtlocation;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor sensor;
    ProgressDialog progressDialog;
    String strLoc = "";


    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Notification notification;



    void showNotification(){
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Message Sent");
       // builder.setContentText("Location is: "+latitude+" - "+longitude);
        builder.setSmallIcon(R.drawable.helprequest);
        builder.setDefaults(Notification.DEFAULT_ALL); // VIBRATE Permission must be written in Manifest

        Intent intent = new Intent(HRActivity1.this,HRActivity1.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,222,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        notification = builder.build();

        notificationManager.notify(101,notification);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr1);
        //txtSensodData = (TextView) findViewById(R.id.textView);
        txtlocation = (TextView) findViewById(R.id.textViewloc);
        // display = (TextView)findViewById(R.id.textView3);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");



        sensorManager.registerListener(HRActivity1.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (ActivityCompat.checkSelfPermission(HRActivity1.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HRActivity1.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(HRActivity1.this, "Please Grant Permissions", Toast.LENGTH_LONG).show();
        } else {

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 5, HRActivity1.this);
                progressDialog.show();
            } else {
                Toast.makeText(HRActivity1.this, "Please Enable GPS", Toast.LENGTH_LONG).show();

                // Buil-In Intent
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }


        }
        //  Intent intent = new Intent("a.b.c.d");
        // sendBroadcast(intent);
    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;



        float x = values[0];
        float y = values[1];
        float z = values[2];
        float cal = ((x*x)+(y*y)+(z*z)) / (SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);
        if(cal>3){
            //txtSensodData.setText("Im in trouble. Please help me. \n  MY LOCATION IS:");
            String phone = "+91 9779912331 ";

            String msg =  /*txtSensodData.getText().toString()*/ "Im in trouble. Please help me. \n  MY LOCATION IS:" + "\n" +  txtlocation.getText().toString();
            // String msg = "Device Shake Done !!";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,msg,null,null);
            showNotification();

            sensorManager.unregisterListener(this);
        }else{
            //txtSensodData.setText(x+" - "+y+" - "+z);

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        strLoc = "Location: "+latitude+" - "+longitude;


        txtlocation.setText("Location is: "+latitude+" - "+longitude);

        try {
            // Reverse Geocoding : Fetch Address from Latitude and Longitude
            Geocoder geocoder = new Geocoder(this);
            List<Address> adrsList = geocoder.getFromLocation(latitude, longitude, 5);

            if(adrsList!=null && adrsList.size()>0){
                Address address = adrsList.get(0);
                StringBuffer buffer = new StringBuffer();

                for(int i=0;i<address.getMaxAddressLineIndex();i++){
                    buffer.append(address.getAddressLine(i)+"\n");
                }
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                txtlocation.setText(buffer.toString()+"\n"+ currentDateTimeString );
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        progressDialog.dismiss();

        //location.getSpeed(); mps

        locationManager.removeUpdates(this);
    }





    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}





