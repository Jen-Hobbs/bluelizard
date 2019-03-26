package ca.bcit.bluelizard;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Details activity that contains a map, text views of the description of the location, and a list view
 * that shows other locations on the map
 */
public class Details extends AppCompatActivity implements OnMapReadyCallback, GetWashroomsJSON.AsyncResponse, GetPlaygroundsJSON.AsyncPlayground, GetParksJSON.AsyncResponseParks,
        GetOffleashJSON.AsyncResponseLeash, GetFountainsJSON.AsyncFountain, GetSportsfieldsJSON.AsyncSportsfield {

    private GoogleMap dMap;
    private double[] lat;
    private double[] lon;
    private String name;
    private String category;
    private String[] activities;
    private String type;
    private String desName;
    private GetWashroomsJSON getWashroomsJSON;
    private GetPlaygroundsJSON getPlayGroundsJSON;
    private GetParksJSON getParksJSON;
    private GetOffleashJSON getOffleashJSON;
    private GetFountainsJSON getFountainsJSON;
    private GetSportsfieldsJSON getSportsfieldsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Grabbing all the intents and assigning them to variables
        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment2.getMapAsync(this);
        lat = getIntent().getDoubleArrayExtra("latitude");
        lon = getIntent().getDoubleArrayExtra("longitude");
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        category = getIntent().getStringExtra("category");
        activities = getIntent().getStringArrayExtra("activities");

        // Sets the name view for each category depending if the value is null or not
        // Description is set to "" if nothing is found
        TextView description = findViewById(R.id.nameView);
        if (name != null) {
            desName = "Name: " + name;
            description.setText(desName);
        } else if (category != null) {
            desName = "Category: " + category;
            description.setText(desName);
        } else if (activities != null){
            StringBuilder desAct = new StringBuilder();
            desAct.append("Activities: ");
            for (int i = 0; i < activities.length; i++) {
                if (!activities[i].equals("null")) {
                    desAct.append(activities[i]);
                    desAct.append(", ");
                }
            }
            description.setText(desAct);
        } else {
            description.setText("");
        }

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

        list.setSelector(R.color.LightGreen);
        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        // Allows different data to be shown on the map depending on the list item selected
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if (position == 0) {
                    clearSelected();
                    parksSelected();
                } else if (position == 1) {
                    clearSelected();
                    leashSelected();
                } else if(position == 2) {
                    clearSelected();
                    sportsfieldsSelected();
                } else if (position == 3) {
                    clearSelected();
                    playSelected();
                } else if (position == 4) {
                    clearSelected();
                    washroomSelected();
                } else if(position == 5){
                    clearSelected();
                    fountainsSelected();
                }

            }
        });

    }

    /**
     * Call back when the map is ready to use
     */
    @Override
    public void onMapReady(GoogleMap map) {
        dMap = map;
        clearSelected();
    }

    /**
     * Clears the selected markers on the map
     */
    public void clearSelected(){
        dMap.clear();
        if(lat.length == 1){
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[0], lon[0]), 15));
            dMap.addMarker(new MarkerOptions().position(new LatLng(lat[0], lon[0])));
        }else{
            double avgLat = 0;
            double avgLong = 0;
            int count = lat.length;
            LatLng[] point = new LatLng[lat.length];
            for (int i = 0; i < count; i++) {
                point[i] = new LatLng(lat[i], lon[i]);
                avgLat += lat[i];
                avgLong += lon[i];
            }
            dMap.addPolygon(
                    new PolygonOptions().add(point).fillColor(0x55588266).strokeWidth(3).strokeColor(Color.rgb(35, 91, 31))
            );
            avgLong /= count;
            avgLat /= count;
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 14));
        }
    }

    /**
     * Grabs the parks JSON information
     */
    public void parksSelected(){
        getParksJSON = new GetParksJSON();
        GetParksJSON.GetParks getp = getParksJSON.new GetParks();
        getParksJSON.delegate = this;
        getp.execute();
    }

    /**
     * Grabs the outdoor activities JSON information
     */
    public void sportsfieldsSelected(){
        getSportsfieldsJSON = new GetSportsfieldsJSON();
        GetSportsfieldsJSON.GetSportsfields getp = getSportsfieldsJSON.new GetSportsfields();
        getSportsfieldsJSON.delegate = this;
        getp.execute();
    }

    /**
     * Grabs the off leash dog areas JSON information
     */
    public void leashSelected(){
        getOffleashJSON = new GetOffleashJSON();
        GetOffleashJSON.GetParks getp = getOffleashJSON. new GetParks();
        getOffleashJSON.delegate = this;
        getp.execute();
    }

    /**
     * Grabs the playgrounds JSON information
     */
    public void playSelected(){
        getPlayGroundsJSON = new GetPlaygroundsJSON();
        GetPlaygroundsJSON.GetPlaygrounds get = getPlayGroundsJSON.new GetPlaygrounds();
        getPlayGroundsJSON.delegate = this;
        get.execute();
    }

    /**
     * Grabs the washrooms JSON information
     */
    public void washroomSelected(){
        getWashroomsJSON = new GetWashroomsJSON();
        GetWashroomsJSON.GetWashrooms get = getWashroomsJSON.new GetWashrooms();
        getWashroomsJSON.delegate = this;
        get.execute();
    }

    /**
     * Grabs the fountains JSON information
     */
    public void fountainsSelected(){
        getFountainsJSON = new GetFountainsJSON();
        GetFountainsJSON.GetFountains get = getFountainsJSON.new GetFountains();
        getFountainsJSON.delegate = this;
        get.execute();
    }

    /**
     * Places markers at each location of a washroom from a list of washroom coordinates
     */
    public void processFinish(){
        List<Washroom> washrooms = getWashroomsJSON.getWashroomList();
        for(int n = 0; n < washrooms.size(); n++) {
            Log.e("longitude", String.valueOf(getWashroomsJSON.getWashroomList().size()));
            dMap.addMarker(new MarkerOptions().position(new LatLng(washrooms.get(n).longitute, washrooms.get(n).lattitude)));
        }
    }

    /**
     * Places polygons at each location of a sports field from a list of sports field coordinates
     */
    public void processFinishSportsfield(){
        List<Sportsfield> sportsfields = getSportsfieldsJSON.getSportsfieldList();
        for(int n = 0; n < sportsfields.size(); n++) {
            dMap.addMarker(new MarkerOptions().position(new LatLng(sportsfields.get(n).longitude, sportsfields.get(n).latitude)));
        }
    }

    /**
     * Places markers at each location of a playground from a list of playground coordinates
     */
    public void processFinishPlayground(){
        List<Playground> playgrounds = getPlayGroundsJSON.getPlaygroundList();
        for(int n = 0; n < playgrounds.size(); n++) {
            Log.e("longitude", String.valueOf(getPlayGroundsJSON.getPlaygroundList().size()));
            dMap.addMarker(new MarkerOptions().position(new LatLng(playgrounds.get(n).longitude, playgrounds.get(n).latitude)));
        }
    }

    /**
     * Places markers at each location of a fountain from a list of fountain coordinates
     */
    public void processFinishFountain(){
        List<Fountain> fountains = getFountainsJSON.getFountainList();
        for(int n = 0; n < fountains.size(); n++) {
            dMap.addMarker(new MarkerOptions().position(new LatLng(fountains.get(n).longitude, fountains.get(n).latitude)));
        }
    }

    /**
     * Places polygons at each location of a off leash area from a list of off leash coordinates
     */
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

    /**
     * Places polygons at each location of a park from a list of park coordinates
     */
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
