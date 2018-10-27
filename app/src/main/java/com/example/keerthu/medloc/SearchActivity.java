package com.example.keerthu.medloc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    ListView listView;
    ScrollView scrollView;

    DatabaseReference ref;

    String address , medName="";
    int radius;
    double latitude, longitude;

    ArrayList<Double> Latitude = new ArrayList<>();
    ArrayList<Double> Longitude = new ArrayList<>();
    ArrayList<String> Medicines = new ArrayList<>();
    ArrayList<String> Names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        listView = (ListView) findViewById(R.id.listView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        medName = extras.getString("MedicineName");
        radius = extras.getInt("Radius");
        latitude = extras.getDouble("Latitude",0.0);
        longitude = extras.getDouble("Longitude",0.0);
        address = extras.getString("Address");

        ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> Shop = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                Iterator traverse = Shop.entrySet().iterator();
                while (traverse.hasNext()) {
                    Map.Entry record = (Map.Entry) traverse.next();
                    HashMap<String, String> hash = (HashMap) record.getValue();
                    Iterator traverse1 = hash.entrySet().iterator();

                    while (traverse1.hasNext()) {
                        Map.Entry record1 = (Map.Entry) traverse1.next();
                        HashMap<String, String> hash1 = (HashMap) record1.getValue();
                        Iterator traverse2 = hash1.entrySet().iterator();
                        while (traverse2.hasNext()) {
                            Map.Entry record2 = (Map.Entry) traverse2.next();
                            String d = (String) record2.getKey();
                            if (d.equals("Latitude")) {
                                Latitude.add((Double) record2.getValue());
                            } else if (d.equals("Longitude")) {
                                Longitude.add((Double) record2.getValue());
                            } else if (d.equals("Medicines")) {
                                Medicines.add((String) record2.getValue());
                            } else {
                                Names.add(String.valueOf(record2.getValue()));
                            }
                        }
                    }
                }
                displayShops(radius);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    boolean checkConnectivity(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            connected = false;
        return  connected;
    }

    void displayShops(int radius){
        ArrayList<Integer> al = new ArrayList<>();
        HashMap<String,Integer> hm = new HashMap<>();
        int size = Latitude.size();
        ArrayList<String> result = new ArrayList();
        for(int i=0;i<size;i++){
            if(((String)Medicines.get(i)).contains(medName)){
                int distance = (int)distanceFrom_in_Km((double)Latitude.get(i),(double)Longitude.get(i),latitude,longitude);
                if(distance<=radius+1){
                    //result.add((String)Names.get(i));
                    hm.put((String)Names.get(i),distance);
                    if(!al.contains(distance))
                    al.add(distance);
                }
            }
        }
        Collections.sort(al);
        for(int i=0;i<al.size();i++){
            for(Map.Entry m : hm.entrySet()){
                if(m.getValue()==al.get(i))
                    result.add((String)m.getKey());
            }
        }
        if(!checkConnectivity()){
            Toast.makeText(SearchActivity.this,"Kindly turn on the internet connection to locate!",Toast.LENGTH_LONG).show();
        }
        else {
            if (result.isEmpty()) {
                Toast.makeText(SearchActivity.this, "Sorry! No shops are available within the specified radius. Kindly increase the radius to locate again!", Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(homeIntent);
            } else {
                createListView(result);
            }
        }
    }


    void createListView(ArrayList result){
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(SearchActivity.this,
                android.R.layout.simple_list_item_1, result );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, ShopDetails.class);
                Bundle extra = new Bundle();
                extra.putString("ShopList", listView.getItemAtPosition(i).toString());
                int index = Names.indexOf(listView.getItemAtPosition(i).toString());
                extra.putDouble("Latitude",Latitude.get(index));
                extra.putDouble("Longitude",Longitude.get(index));
                intent.putExtras(extra);
                startActivity(intent);
            }
        });
        listView.setAdapter(mAdapter);
    }


    private float distanceFrom_in_Km(double lat1, double lng1, double lat2, double lng2) {

        double Rad = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        float distance = (float)(Rad * c);

        distance += distance/3.25;
        return distance;
    }




}
