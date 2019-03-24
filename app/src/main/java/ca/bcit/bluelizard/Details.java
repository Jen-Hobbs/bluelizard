package ca.bcit.bluelizard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Details extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap dMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment2.getMapAsync(this);
        double[] lat = getIntent().getDoubleArrayExtra("latitude");
        double[] lon = getIntent().getDoubleArrayExtra("longitude");
        String type = getIntent().getStringExtra("type");



    }

    @Override
    public void onMapReady(GoogleMap map) {
        dMap = map;
    }
}
