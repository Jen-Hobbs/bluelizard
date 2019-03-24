package ca.bcit.bluelizard;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapsLocation extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GetWashroomsJSON.AsyncResponse, GetPlaygroundsJSON.AsyncPlayground, GetParksJSON.AsyncResponseParks,
        GetOffleashJSON.AsyncResponseLeash{

    private GoogleMap mMap;
    private ArrayList<Marker> marker;
    private Marker myMarker;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;
    private GetOffleashJSON getOffleashJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marker = new ArrayList<>();
        setContentView(R.layout.activity_maps_location);
        long info = getIntent().getLongExtra("location", 0);
        Log.e("location", "hi");
        Log.e("location", String.valueOf(info));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(info == 0){
            getParksJSON = new GetParksJSON();
            GetParksJSON.GetParks getp = getParksJSON.new GetParks();
            getParksJSON.delegate = this;
            getp.execute();
        }
        else if(info == 1){
            getOffleashJSON = new GetOffleashJSON();
            GetOffleashJSON.GetParks getp = getOffleashJSON. new GetParks();
            getOffleashJSON.delegate = this;
            getp.execute();
        }
        else if(info == 3) {
            getPlayGroundsJSON = new GetPlaygroundsJSON();
            GetPlaygroundsJSON.GetPlaygrounds get = getPlayGroundsJSON.new GetPlaygrounds();
            getPlayGroundsJSON.delegate = this;
            get.execute();
        }
        else if(info == 4) {
            getWashroomsJSON = new GetWashroomsJSON();
            GetWashroomsJSON.GetWashrooms get = getWashroomsJSON.new GetWashrooms();
            getWashroomsJSON.delegate = this;
            get.execute();
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

/*
        // Add a marker in Sydney and move the camera
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        List<Double> location = new ArrayList<Double>();
        List<List<Double>> polygon = new ArrayList();
        List<List<List<Double>>> multiPolygon = new ArrayList();
     //1
        location.add(-122.90167911202795);
        location.add(49.210838475275544);
        polygon.add(location);
        location = new ArrayList();
        location.add(-122.90108615046643);
        location.add(49.211157364714765);
        polygon.add(location);
//2
        location = new ArrayList();
        location.add(-122.90108615046643);
        location.add(49.211157364714765);
        polygon.add(location);
//3
        location = new ArrayList();
        location.add(-122.90119873560481);
        location.add(49.2133490365784);
        polygon.add(location);
//4
        location = new ArrayList();
        location.add(-122.90169404851203);
        location.add(49.21394149278283);
        polygon.add(location);
//5
        location = new ArrayList();
        location.add(-122.90855629898924);
        location.add(49.21939798924417);
        polygon.add(location);
//6
        location = new ArrayList();
        location.add(-122.91193355755044);
        location.add(49.21757116319086);
        polygon.add(location);
        //7
        location = new ArrayList();
        location.add(-122.90406701107361);
        location.add(49.21131726041395);
        polygon.add(location);
        //8
        location = new ArrayList();
        location.add(-122.90299907265259);
        location.add(49.21189067262306);
        polygon.add(location);
        //9
        location = new ArrayList();
        location.add(-122.90167911202795);
        location.add(49.210838475275544);
        polygon.add(location);
        multiPolygon.add(polygon);
        /*
             1  [ -122.90167911202795, 49.210838475275544 ],
		    		[ -122.90108615046643, 49.211157364714765 ],
						[ -122.90119873560481, 49.2133490365784 ],
						[ -122.90169404851203, 49.21394149278283 ],
						[ -122.90855629898924, 49.21939798924417 ],
						[ -122.91193355755044, 49.21757116319086 ],
						[ -122.90406701107361, 49.21131726041395 ],
						[ -122.90299907265259, 49.21189067262306 ],
						[ -122.90167911202795, 49.210838475275544 ]


        double avgLat = 0;
        double avgLong = 0;
        int count = 0;


        for(int i = 0; i < multiPolygon.size(); i++) {
            ArrayList<LatLng> locations = new ArrayList<>();

            for(int n = 0; n < multiPolygon.get(i).size(); n++) {

                locations.add(new LatLng(multiPolygon.get(i).get(n).get(1),multiPolygon.get(i).get(n).get(0)));
                avgLong += multiPolygon.get(i).get(n).get(0);
                avgLat += multiPolygon.get(i).get(n).get(1);
                count++;
            }

            LatLng[] point = locations.toArray(new LatLng[locations.size()]);
            mMap.addPolygon(

                    new PolygonOptions().add(point).fillColor(0x55588266)
            );
           // mMap.addMarker(new MarkerOptions().position(locations.get(0)));

        }
        avgLong /= count;
        avgLat /= count;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 13));
        myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(avgLat, avgLong)));
        mMap.setOnMarkerClickListener(this);

        /*

        });*/
    }


    public void processFinish(){
        List<Washroom> washrooms = getWashroomsJSON.getWashroomList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));

        for(int n = 0; n < washrooms.size(); n++) {
            Log.e("longitude", String.valueOf(getWashroomsJSON.getWashroomList().size()));
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(washrooms.get(n).longitute, washrooms.get(n).lattitude)));
            marker.add(myMarker);
            mMap.setOnMarkerClickListener(this);
        }


    }
    public void processFinishPlayground(){
        List<Playground> playgrounds = getPlayGroundsJSON.getPlaygroundList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));
        for(int n = 0; n < playgrounds.size(); n++) {
            Log.e("longitude", String.valueOf(getPlayGroundsJSON.getPlaygroundList().size()));
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(playgrounds.get(n).longitude, playgrounds.get(n).latitude)));
            marker.add(myMarker);
            mMap.setOnMarkerClickListener(this);
        }


    }
    public void processFinishLeash(){
        List<Park> park = getOffleashJSON.getParkList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));

        for(int j = 0; j < park.size(); j++) {
            double avgLat = 0;
            double avgLong = 0;
            int count = 0;
            for (int i = 0; i < park.get(j).coordinates.size(); i++) {
                ArrayList<LatLng> locations = new ArrayList<>();
                for (int n = 0; n < park.get(j).coordinates.get(i).size(); n++) {
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                    avgLong += park.get(j).coordinates.get(i).get(n).get(0);
                    avgLat += park.get(j).coordinates.get(i).get(n).get(1);
                    count++;
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                mMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).clickable(true)
                );

            }
            avgLong /= count;
            avgLat /= count;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 13));
            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
                public void onPolygonClick(Polygon polygon){
                    Intent intent = new Intent(MapsLocation.this, LocationInfo.class);
                    startActivity(intent);
                }
            });
        }
    }
    public void processFinishParks(){
        List<Park> park = getParksJSON.getParkList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));
        for(int j = 0; j < park.size(); j++){
            double avgLat = 0;
            double avgLong = 0;
            int count = 0;
            for(int i = 0; i < park.get(j).coordinates.size(); i++){
                ArrayList<LatLng> locations = new ArrayList<>();
                for(int n = 0; n < park.get(j).coordinates.get(i).size(); n++){
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                    avgLong += park.get(j).coordinates.get(i).get(n).get(0);
                    avgLat += park.get(j).coordinates.get(i).get(n).get(1);
                    count++;
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                mMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).clickable(true)
                );

            }
            avgLong /= count;
            avgLat /= count;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 13));
            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
                public void onPolygonClick(Polygon polygon){
                    Intent intent = new Intent(MapsLocation.this, LocationInfo.class);
                    startActivity(intent);
                }
            });



        }




    }

    @Override
    public boolean onMarkerClick(final Marker mark){
        Log.e("marker started", String.valueOf(mark.getPosition()));

        for(int i = 0; i < marker.size(); i++) {
            Log.e("mymarker started", String.valueOf(marker.get(i).getPosition()));

            if (mark.equals(marker.get(i))) {
                
                Log.e("marker started", String.valueOf(mark.getPosition()));
                Intent intent = new Intent(MapsLocation.this, LocationInfo.class);
                startActivity(intent);
            }
        }


        return false;
    }


}
