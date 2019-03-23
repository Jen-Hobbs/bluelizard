package ca.bcit.bluelizard;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng newWest = new LatLng(49.193788,-122.9314024);
       // List<Double> location = new List<Double>();

        /*
                [ -122.90167911202795, 49.210838475275544 ],
						[ -122.90108615046643, 49.211157364714765 ],
						[ -122.90119873560481, 49.2133490365784 ],
						[ -122.90169404851203, 49.21394149278283 ],
						[ -122.90855629898924, 49.21939798924417 ],
						[ -122.91193355755044, 49.21757116319086 ],
						[ -122.90406701107361, 49.21131726041395 ],
						[ -122.90299907265259, 49.21189067262306 ],
						[ -122.90167911202795, 49.210838475275544 ]
        */

        mMap.addMarker(new MarkerOptions().position(newWest).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newWest));
        /*
        mMap.addPolygon(
                new PolygonOptions().add(
                        sydney, new LatLng(-35, 151), new LatLng(-35, 150), new LatLng(-34, 150)
                ).fillColor(Color.GREEN)
        );
        */
    }
}
