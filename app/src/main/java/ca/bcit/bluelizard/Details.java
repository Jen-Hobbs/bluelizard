package ca.bcit.bluelizard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class Details extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap dMap;
    private double[] lat;
    private double[] lon;
    String type;

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
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[0], lon[0]), 13));
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
                avgLat += lat[i];
                avgLong = lon[i];
            }
            dMap.addPolygon(
                    new PolygonOptions().add(point).fillColor(0x55588266).clickable(true)
            );
            avgLong /= count;
            avgLat /= count;
            dMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLong), 13));
        }
    }
}
