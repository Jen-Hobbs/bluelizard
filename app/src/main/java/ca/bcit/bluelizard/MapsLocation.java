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
    private String infoType;


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
            infoType = "playground";
            get.execute();
        }
        else if(info == 4) {
            getWashroomsJSON = new GetWashroomsJSON();
            GetWashroomsJSON.GetWashrooms get = getWashroomsJSON.new GetWashrooms();
            getWashroomsJSON.delegate = this;
            infoType = "washroom";
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
                    Intent intent = new Intent(MapsLocation.this, Details.class);
                    List<LatLng> latLngs = polygon.getPoints();
                    double[] lat = new double[latLngs.size()];
                    double[] lon = new double[latLngs.size()];
                    for(int i = 0; i < latLngs.size(); i++){
                        lat[i] = latLngs.get(i).latitude;
                        lon[i] = latLngs.get(i).longitude;
                    }
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("type", "leash");
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
                    Intent intent = new Intent(MapsLocation.this, Details.class);
                    List<LatLng> latLngs = polygon.getPoints();
                    double[] lat = new double[latLngs.size()];
                    double[] lon = new double[latLngs.size()];
                    for(int i = 0; i < latLngs.size(); i++){
                        lat[i] = latLngs.get(i).latitude;
                        lon[i] = latLngs.get(i).longitude;
                    }
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("type", "park");
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
                Intent intent = new Intent(MapsLocation.this, Details.class);
                List<LatLng> latLngs = new ArrayList<>();
                double[] lat = new double[1];
                double[] lon = new double[1];
                lat[0] = mark.getPosition().latitude;
                lon[0] = mark.getPosition().longitude;

                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lon);
                intent.putExtra("type", infoType);
                startActivity(intent);
            }
        }


        return false;
    }


}
