package com.charles.myroadsideassistant.Service;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.charles.myroadsideassistant.Components.MainActivity;
import com.charles.myroadsideassistant.Database.ContactsDb;
import com.charles.myroadsideassistant.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class LocationService extends Service {

    ContactsDb cdb;

    ArrayList<String> contactList = new ArrayList<>();
    String message = "";
    private final static int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            cdb = new ContactsDb(LocationService.this);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("LOCATION_UPDATE", latitude + "," + longitude);

                System.out.println("Latitude is: " + latitude);
                System.out.println("Longitude is: " + longitude);

                SQLiteDatabase db = cdb.getWritableDatabase();
                String query = "select * from contact";
                Cursor c1 = db.rawQuery(query, null);

                Set<String> contactSet = new LinkedHashSet<>();

                if (c1 != null && c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            contactSet.add(c1.getString(1));
                        } while (c1.moveToNext());
                    }
                }
                contactList.addAll(contactSet);

                message = "Help I need help... panic panic panic panic panic...\n\nMy current location is:\n\n" + "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
                ;

                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(message);

            //    requestSmsPermission();

                for (String s : contactList) {
                    sms.sendMultipartTextMessage(s, null, parts, null, null);
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        System.out.println("Sms not sent due to : " + e.getMessage());
                    }
                    sms.sendMultipartTextMessage(s, null, parts, null, null);
                    Toast.makeText(LocationService.this, "SMS SENT", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

   /* private void requestSmsPermission() {
        String permissions[]= {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 200);
    }   */

    private void startLocationService() {
        String chennelID = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), chennelID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(chennelID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(chennelID,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(40000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());

        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(callback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}