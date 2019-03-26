package ca.bcit.bluelizard;
/**
 * Map Activity that displays information about parks
 */

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;


import java.util.ArrayList;

import java.util.List;


public class MapsLocation extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GetWashroomsJSON.AsyncResponse, GetPlaygroundsJSON.AsyncPlayground, GetParksJSON.AsyncResponseParks,
        GetOffleashJSON.AsyncResponseLeash, GetSportsfieldsJSON.AsyncSportsfield{

    private GoogleMap mMap;
    private ArrayList<Marker> marker;
    private Marker myMarker;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;
    private GetOffleashJSON getOffleashJSON;
    private GetSportsfieldsJSON getSportsfieldsJSON;
    private String infoType;
    private List<Sportsfield> sportsfields;

    /**
     * creationg of map class. gets info from previous intent and based on that instantiates
     * different jsons to get data from either Parks, Offleash areas, athletics, playgournds,
     * or washrooms
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marker = new ArrayList<>();
        setContentView(R.layout.activity_maps_location);
        long info = getIntent().getLongExtra("location", 0);
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
        else if(info == 2){
            getSportsfieldsJSON = new GetSportsfieldsJSON();
            GetSportsfieldsJSON.GetSportsfields getp = getSportsfieldsJSON.new GetSportsfields();
            getSportsfieldsJSON.delegate = this;
            infoType = "sportsfield";
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
     * move Camera to newwest
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng newWest = new LatLng(49.193788,-122.9314024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newWest, 13));

    }

    /**
     * once the thread in json to retreave data is finished markers are made on the map for washromms
     * based on that data
     */
    public void processFinish(){
        List<Washroom> washrooms = getWashroomsJSON.getWashroomList();

        for(int n = 0; n < washrooms.size(); n++) {
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(washrooms.get(n).longitute, washrooms.get(n).lattitude)));
            marker.add(myMarker);
            mMap.setOnMarkerClickListener(this);
        }


    }
    /**
     * once the thread in json to retreave data is finished markers are made on the map for sportsfields
     * based on that data
     */
    public void processFinishSportsfield(){
        sportsfields = getSportsfieldsJSON.getSportsfieldList();

        for(int n = 0; n < sportsfields.size(); n++) {
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(sportsfields.get(n).longitude, sportsfields.get(n).latitude)));
            marker.add(myMarker);
            mMap.setOnMarkerClickListener(this);
        }


    }
    /**
     * once the thread in json to retreave data is finished markers are made on the map for playgrounds
     * based on that data
     */
    public void processFinishPlayground(){
        List<Playground> playgrounds = getPlayGroundsJSON.getPlaygroundList();

        for(int n = 0; n < playgrounds.size(); n++) {
            myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(playgrounds.get(n).longitude, playgrounds.get(n).latitude)));
            marker.add(myMarker);
            mMap.setOnMarkerClickListener(this);
        }


    }

    /**
     *once the thread in json to retreave data is finished polygons are made on the map for ofleash areas
     * based on that data
     */
    public void processFinishLeash(){
        final List<Park> park = getOffleashJSON.getParkList();

        for(int j = 0; j < park.size(); j++) {

            for (int i = 0; i < park.get(j).coordinates.size(); i++) {
                ArrayList<LatLng> locations = new ArrayList<>();
                for (int n = 0; n < park.get(j).coordinates.get(i).size(); n++) {
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                mMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).clickable(true).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
                );
            }
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
                    for(int i = 0; i < park.size(); i++){
                        for(int x = 0; x < park.get(i).coordinates.size(); x++) {
                            if (park.get(i).coordinates.get(x).get(0).get(1) == lat[0] && park.get(i).coordinates.get(x).get(0).get(0) == lon[0]) {
                                intent.putExtra("name", park.get(i).name);
                                intent.putExtra("category", park.get(i).category);
                            }
                        }
                    }
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("type", "leash");
                    startActivity(intent);
                }
            });
        }
    }
    /**
     *once the thread in json to retreave data is finished polygons are made on the map for park areas
     * based on that data
     */
    public void processFinishParks(){
        final List<Park> park = getParksJSON.getParkList();

        for(int j = 0; j < park.size(); j++){
            for(int i = 0; i < park.get(j).coordinates.size(); i++){
                ArrayList<LatLng> locations = new ArrayList<>();
                for(int n = 0; n < park.get(j).coordinates.get(i).size(); n++){
                    locations.add(new LatLng(park.get(j).coordinates.get(i).get(n).get(1), park.get(j).coordinates.get(i).get(n).get(0)));
                }
                LatLng[] point = locations.toArray(new LatLng[locations.size()]);
                mMap.addPolygon(
                        new PolygonOptions().add(point).fillColor(0x55588266).clickable(true).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
                );

            }
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
                    for(int i = 0; i < park.size(); i++){
                        for(int x = 0; x < park.get(i).coordinates.size(); x++) {
                            if (park.get(i).coordinates.get(x).get(0).get(1) == lat[0] && park.get(i).coordinates.get(x).get(0).get(0) == lon[0]) {
                                intent.putExtra("name", park.get(i).name);
                                intent.putExtra("category", park.get(i).category);
                            }
                        }
                    }
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("type", "park");
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * when a marker is selected it finds the data it relates to and creats a new intent on this
     * @param mark marker on map
     * @return false
     */
    @Override
    public boolean onMarkerClick(final Marker mark){
        for(int i = 0; i < marker.size(); i++) {

            if (mark.equals(marker.get(i))) {
                Intent intent = new Intent(MapsLocation.this, Details.class);
                double[] lat = new double[1];
                double[] lon = new double[1];
                lat[0] = mark.getPosition().latitude;
                lon[0] = mark.getPosition().longitude;
                if(infoType.equals("sportsfield")){
                    for(int x = 0; x < sportsfields.size(); x++){
                        if(sportsfields.get(i).latitude == lon[0] && sportsfields.get(i).longitude == lat[0]){
                            String[] activities = sportsfields.get(i).activities.toArray(new String[sportsfields.get(i).activities.size()]);
                            intent.putExtra("activities", activities);
                        }
                    }
                }
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lon);
                intent.putExtra("type", infoType);
                startActivity(intent);
            }
        }


        return false;
    }


}
