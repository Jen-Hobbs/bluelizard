package ca.bcit.bluelizard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ListView list = (ListView) findViewById(R.id.list);
        String[] values = new String[]{"Park", "Off Leash Area", "Athletics", "Playground", "Washrooms"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if (position == 4) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    intent.putExtra("location", "washroom");
                    startActivityForResult(intent, 0);
                }
                if (position == 1) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    intent.putExtra("location", "park");
                    startActivityForResult(intent, 0);
                }
            }
        });
        //getWashrooms();
        getParks();
    } //end onCreate

    private void getParks()
    {
        GetParksJSON getParksJSON = new GetParksJSON();
        GetParksJSON.GetParks getp = getParksJSON.new GetParks();
        getp.execute();
    }

    private void getPlaygrounds()
    {
        GetPlaygroundsJSON getPlaygroundsJSON = new GetPlaygroundsJSON();
        GetPlaygroundsJSON.GetPlaygrounds getPlay = getPlaygroundsJSON.new GetPlaygrounds();
        getPlay.execute();
    }
}//end Information
