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
        String[] values = new String[]{"Parks", "Off Leash Areas", "Athletics", "Playgrounds", "Washrooms", "Drinking Fountains"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                Intent intent = new Intent(v.getContext(), MapsLocation.class);
                intent.putExtra("location", l);
                startActivityForResult(intent, 0);

            }
        });
        //testSports();
    } //end onCreate
/*
    public void testSports() {
        GetFountainsJSON s = new GetFountainsJSON();
        GetFountainsJSON.GetFountains sg = s.new GetFountains();
        sg.execute();
    }
    */

}//end Information

