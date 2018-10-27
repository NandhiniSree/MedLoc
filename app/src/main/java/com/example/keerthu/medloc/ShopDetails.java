package com.example.keerthu.medloc;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShopDetails extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String ShopName;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        Bundle extras = getIntent().getExtras();
        ShopName = extras.getString("ShopName");
        latitude = extras.getDouble("Latitude");
        longitude = extras.getDouble("Longitude");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destination = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(destination).title(ShopName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
    }
}
