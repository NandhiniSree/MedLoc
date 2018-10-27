package com.example.keerthu.medloc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.content.Context;


public class MainActivity extends AppCompatActivity implements LocationListener{

    EditText edtMedicine, edtRadius;
    TextView txtMedicine, txtRadius, txtLocation;
    Button btnLocation, btnSearch;
    LocationManager locationManager;
    Double latitude, longitude;
    int radius;
    String address, medName;
    boolean checkLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        edtMedicine = (EditText) findViewById(R.id.edtMedicine);
        edtRadius = (EditText) findViewById(R.id.edtRadius);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtRadius = (TextView) findViewById(R.id.txtRadius);
        txtMedicine = (TextView) findViewById(R.id.txtMedicine);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnLocation = (Button) findViewById(R.id.btnLocation);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
     }

    public void onSearch(View view) {

        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);

        if (!checkLocation) {
            Toast.makeText(this, "Kindly click on the location button to detect!", Toast.LENGTH_LONG).show();
        } else {

            medName = edtMedicine.getText().toString();

            if (TextUtils.isEmpty(medName)) {

                Toast.makeText(this, "Please enter the medicine name!", Toast.LENGTH_SHORT).show();
            } else {

                String check_empty = edtRadius.getText().toString();

                if(check_empty.equals("")) {
                    Toast.makeText(this, "Please enter the radius to locate!", Toast.LENGTH_LONG).show();
                }
                else {
                    radius = Integer.parseInt(edtRadius.getText().toString());
                    Bundle extras = new Bundle();
                    extras.putString("MedicineName", medName);
                    extras.putInt("Radius", radius);
                    extras.putDouble("Latitude", latitude);
                    extras.putDouble("Longitude", longitude);
                    extras.putString("Address", address);
                    searchIntent.putExtras(extras);
                    startActivity(searchIntent);
                }
            }
        }
    }

     public void onLocation(View view){
        getLocation();
     }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        checkLocation = true;

        Toast.makeText(this,"Location read successfully!",Toast.LENGTH_LONG).show();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            address = addresses.get(0).getAddressLine(0);

        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void onECG(View view){
        Toast.makeText(this, "Happy healthy life!", Toast.LENGTH_SHORT).show();

    }

}
