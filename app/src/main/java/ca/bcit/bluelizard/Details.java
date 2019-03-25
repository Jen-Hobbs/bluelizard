package ca.bcit.bluelizard;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity implements OnMapReadyCallback, GetWashroomsJSON.AsyncResponse, GetPlaygroundsJSON.AsyncPlayground, GetParksJSON.AsyncResponseParks,
        GetOffleashJSON.AsyncResponseLeash, GetFountainsJSON.AsyncFountain {

    private GoogleMap dMap;
    private double[] lat;
    private double[] lon;
    String type;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;
    private GetOffleashJSON getOffleashJSON;
    private GetFountainsJSON getFountainsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment2.getMapAsync(this);
        lat = getIntent().getDoubleArrayExtra("latitude");
        lon = getIntent().getDoubleArrayExtra("longitude");
        type = getIntent().getStringExtra("type");

        // Sets the text view for each category
        TextView category = findViewById(R.id.textView);
        String cat = "Category: " + type;
        category.setText(cat);

        // List view of all the categories
        ListView list = findViewById(R.id.list2);
        String[] values = new String[]{"Parks", "Off Leash Areas", "Athletics", "Playgrounds", "Washrooms", "Drinking Fountains"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if (position == 0) {
                    parksSelected();
                } else if (position == 1) {
                    leashSelected();
                } else if (position == 3) {
                    playSelected();
                } else if (position == 4) {
                    washroomSelected();
                } else if (position == 5) {
                    fountainsSelected();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        dMap = map;
        if(lat.length == 1){
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[0], lon[0]), 15));
            //Log.e("longitude", String.valueOf(getPlayGroundsJSON.getPlaygroundList().size()));
            dMap.addMarker(new MarkerOptions().position(new LatLng(lat[0], lon[0])));
            //marker.add(myMarker);

        }

        else{
            double avgLat = 0;
            double avgLong = 0;
            int count = lat.length;
            LatLng[] point = new LatLng[lat.length];
            for (int i = 0; i < count; i++) {
                point[i] = new LatLng(lat[i], lon[i]);
                Log.e("latitiude of point poly", String.valueOf(lat[i]));
                Log.e("longitude of point poly", String.valueOf(lon[i]));
                avgLat += lat[i];
                avgLong += lon[i];
            }
            dMap.addPolygon(
                    new PolygonOptions().add(point).fillColor(0x55588266).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
            );
            avgLong /= count;
            avgLat /= count;
            Log.e("latitiude of point poly", String.valueOf(avgLat));
            Log.e("longitude of point poly", String.valueOf(avgLong));
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 14));

        }
        //parksSelected();
        //leashSelected();
        //playSelected();
        //washroomSelected();
        //fountainsSelected();
    }

    public void parksSelected(){
        getParksJSON = new GetParksJSON();
        GetParksJSON.GetParks getp = getParksJSON.new GetParks();
        getParksJSON.delegate = this;
        getp.execute();
    }
    public void leashSelected(){
        getOffleashJSON = new GetOffleashJSON();
        GetOffleashJSON.GetParks getp = getOffleashJSON. new GetParks();
        getOffleashJSON.delegate = this;
        getp.execute();
    }
    public void playSelected(){
        getPlayGroundsJSON = new GetPlaygroundsJSON();
        GetPlaygroundsJSON.GetPlaygrounds get = getPlayGroundsJSON.new GetPlaygrounds();
        getPlayGroundsJSON.delegate = this;
        get.execute();
    }
    public void washroomSelected(){
        getWashroomsJSON = new GetWashroomsJSON();
        GetWashroomsJSON.GetWashrooms get = getWashroomsJSON.new GetWashrooms();
        getWashroomsJSON.delegate = this;
        get.execute();
    }
    public void fountainsSelected(){
        getFountainsJSON = new GetFountainsJSON();
        GetFountainsJSON.GetFountains get = getFountainsJSON.new GetFountains();
        getFountainsJSON.delegate = this;
        get.execute();
    }
    public void processFinish(){
        List<Washroom> washrooms = getWashroomsJSON.getWashroomList();
        for(int n = 0; n < washrooms.size(); n++) {
            Log.e("longitude", String.valueOf(getWashroomsJSON.getWashroomList().size()));
            dMap.addMarker(new MarkerOptions().position(new LatLng(washrooms.get(n).longitute, washrooms.get(n).lattitude)));
        }


    }
    public void processFinishPlayground(){
        List<Playground> playgrounds = getPlayGroundsJSON.getPlaygroundList();
        for(int n = 0; n < playgrounds.size(); n++) {
            Log.e("longitude", String.valueOf(getPlayGroundsJSON.getPlaygroundList().size()));
            dMap.addMarker(new MarkerOptions().position(new LatLng(playgrounds.get(n).longitude, playgrounds.get(n).latitude)));
        }


    }
    public void processFinishFountain(){
        List<Fountain> fountains = getFountainsJSON.getFountainList();
        for(int n = 0; n < fountains.size(); n++) {
            dMap.addMarker(new MarkerOptions().position(new LatLng(fountains.get(n).longitude, fountains.get(n).latitude)));
        }
    }
    public void processFinishLeash(){
        List<Park> park = getOffleashJSON.getParkList();
        for(int j = 0; j < park.size(); j++) {
            for (int i = 0; i < park.get(j).coordinates.size(); i++) {
                ArrayList<LatLng> locations = new ArrayList<>();
                for (int n = 0; n < park.get(j).coordinates.get(i).size(); n++) {
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                dMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
                );

            }

        }
    }
    public void processFinishParks(){
        List<Park> park = getParksJSON.getParkList();
        for(int j = 0; j < park.size(); j++){
            for(int i = 0; i < park.get(j).coordinates.size(); i++){
                ArrayList<LatLng> locations = new ArrayList<>();
                for(int n = 0; n < park.get(j).coordinates.get(i).size(); n++){
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                dMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
                );

            }


        }
    }

}
