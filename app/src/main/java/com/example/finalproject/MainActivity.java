package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Button openCamera;
//    Button openGallery;
    TextView currentLocation;
    TextView readLocation;
    TextView distance;
    String geoURI;
    private FusedLocationProviderClient fusedLocationClient;
    Double targetLatitude;
    Double targetLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openCamera = findViewById(R.id.openCamera);
//        openGallery = findViewById(R.id.openGallery);
        currentLocation = findViewById(R.id.currentLocation);
        readLocation = findViewById(R.id.readLocation);
        distance = findViewById(R.id.distance);

        // Scan QR code
        Intent intent = getIntent();
        geoURI = intent.getStringExtra("LOCATION");
        if (geoURI == null || !geoURI.contains("geo")) {
            System.out.println("Wrong format");
            targetLatitude = -91.0;
            targetLongitude = -181.0;
        }
        else {
            String[] part = geoURI.split(",");
            targetLatitude = Double.parseDouble(part[0].substring(4));
            targetLongitude = Double.parseDouble(part[1]);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            StringBuffer sb = new StringBuffer();
            sb.append("QR: ");
            sb.append(decimalFormat.format(targetLatitude));
            sb.append(", ");
            sb.append(decimalFormat.format(targetLongitude));
            readLocation.setText(sb.toString());
        }

        // Geolocation service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
    }

public void getCurrentLocation() {
    if (ActivityCompat.
            checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
        System.out.println("Coarse and fine location permissions not granted.");
        String[] permission = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permission, 42);
        return;
    } else {
        System.out.println("Permission check succeeded.");
    }
    fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    StringBuffer sb = new StringBuffer();
                    sb.append("Current: ");
                    sb.append(decimalFormat.format(location.getLatitude()));
                    sb.append(", ");
                    sb.append(decimalFormat.format(location.getLongitude()));
                    currentLocation.setText(sb.toString());
                    updateDistance(location);
                }
            });
    }
    public void updateDistance(Location location) {
        if (Math.abs(targetLatitude) > 90 || Math.abs(targetLongitude) > 180) return;
        float[] result = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), targetLatitude, targetLongitude, result);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        StringBuffer sb = new StringBuffer();
        sb.append("Distance: ");
        sb.append(decimalFormat.format(result[0] / 1000.0));
        sb.append("km");
        distance.setText(sb.toString());
    }

    public void onClickOpenCamera(View view) {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(intent);
    }
}