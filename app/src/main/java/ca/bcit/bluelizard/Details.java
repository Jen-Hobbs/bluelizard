package ca.bcit.bluelizard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
        GetOffleashJSON.AsyncResponseLeash {

    private GoogleMap dMap;
    private double[] lat;
    private double[] lon;
    String type;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;
    private GetOffleashJSON getOffleashJSON;

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
                    new PolygonOptions().add(point).fillColor(0x55588266)
            );
            avgLong /= count;
            avgLat /= count;
            Log.e("latitiude of point poly", String.valueOf(avgLat));
            Log.e("longitude of point poly", String.valueOf(avgLong));
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 14));

        }
        parksSelected();
        leashSelected();
        playSelected();
        washroomSelected();
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
                        new PolygonOptions().add(point).fillColor(0x55588266)
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
                        new PolygonOptions().add(point).fillColor(0x55588266)
                );

            }


        }
    }

}
