package ca.bcit.bluelizard;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapsLocation extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GetWashroomsJSON.AsyncResponse, GetPlaygroundsJSON.AsyncPlayground, GetParksJSON.AsyncResponseParks{

    private GoogleMap mMap;
    private Marker myMarker;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);
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
        if(info == 3) {
            getPlayGroundsJSON = new GetPlaygroundsJSON();
            GetPlaygroundsJSON.GetPlaygrounds get = getPlayGroundsJSON.new GetPlaygrounds();
            getPlayGroundsJSON.delegate = this;
            get.execute();
        }
        if(info == 4) {
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void processFinish(){
        List<Washroom> washrooms = getWashroomsJSON.getWashroomList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));

        for(int n = 0; n < washrooms.size(); n++) {
            Log.e("longitude", String.valueOf(getWashroomsJSON.getWashroomList().size()));
            mMap.addMarker(new MarkerOptions().position(new LatLng(washrooms.get(n).longitute, washrooms.get(n).lattitude)));
        }

    }
    public void processFinishPlayground(){
        List<Playground> playgrounds = getPlayGroundsJSON.getPlaygroundList();
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));

        for(int n = 0; n < playgrounds.size(); n++) {
            Log.e("longitude", String.valueOf(getPlayGroundsJSON.getPlaygroundList().size()));
            mMap.addMarker(new MarkerOptions().position(new LatLng(playgrounds.get(n).longitude, playgrounds.get(n).latitude)));
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

                        new PolygonOptions().add(point).fillColor(0x55588266)
                );
            }
            avgLong /= count;
            avgLat /= count;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 13));
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(avgLat, avgLong)));
            mMap.setOnMarkerClickListener(this);


        }




    }
    @Override
    public boolean onMarkerClick(final Marker marker){
        if(marker.equals(myMarker)){
            Intent intent = new Intent(MapsLocation.this, LocationInfo.class);
            startActivity(intent);
        }


        return false;
    }

}
