package ca.bcit.bluelizard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        String[] values = new String[]{"Park", "Off Leash Area", "Athletics", "Playground"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if (position == 0) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    startActivityForResult(intent, 0);
                }
                if (position == 1) {
                    Intent intent = new Intent(v.getContext(), MapsLocation.class);
                    startActivityForResult(intent, 0);
                }
            }
        });
        GetWashroomsJSON getWashroomsJSON = new GetWashroomsJSON();
        GetWashroomsJSON.GetContacts get = getWashroomsJSON.new GetContacts();
        get.execute();

    } //end onCreate
}//end Information
