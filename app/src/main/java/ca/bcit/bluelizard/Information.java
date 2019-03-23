package ca.bcit.bluelizard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Information extends AppCompatActivity {

    private String TAG = Information.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/parks/";
    private ArrayList<Geometry> geometryList;
    private ArrayList<Properties> propertiesList;
    private ArrayList<Park> parkList;

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

        /**
         * Async task class to get json by making HTTP call
         */
        class GetContacts extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(Information.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(SERVICE_URL);

                Log.e(TAG, "Response from url: " + jsonStr);

                if (jsonStr != null) {
                    try {
                        //JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray parksJsonArray = null;
                        try {
                            parksJsonArray = new JSONArray(jsonStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // looping through All Contacts
                        for (int i = 0; i < parksJsonArray.length(); i++) {
                            JSONObject c = parksJsonArray.getJSONObject(i);

                            //Geometry
                            //String type = c.getString("type");
                            List<List<List<Double>>> coordinates = (List<List<List<Double>>>) c.get("coordinates");

                            //Properties
                            String strName = c.getString("StrName");
                            String strNum = c.getString("strNum");
                            int objectID = c.getInt("OBJECTID");
                            String name = c.getString("Name");
                            String category = c.getString("Category");
                            String neighbourhood = c.getString("neighbourhood");
                            String zoning = c.getString("Zoning");
                            String zone_Category = c.getString("Zone_Category");
                            String owner = c.getString("Owner");
                            String surveyed = c.getString("Surveyed");
                            double siteArea = c.getDouble("Site_Area");
                            String relationship = c.getString("Relationship");
                            double shapeLength = c.getDouble("SHAPE_Length");
                            double shapeArea = c.getDouble("SHAPE_Area");

                            //Park
                            //String parkType = c.getString("type");

                            // tmp hash map for single contact
                            //Toon toon = new Toon();
                            Geometry geometry = new Geometry();
                            Properties properties = new Properties();
                            Park park = new Park();


                            // adding each child node to HashMap key => value
                                //geometry
                                geometry.setCoordinates(coordinates);
                                //properties
                                properties.setStrName(strName);
                                properties.setStrNum(strNum);
                                properties.setOBJECTID(objectID);
                                properties.setName(name);
                                properties.setCategory(category);
                                properties.setNeighbourhood(neighbourhood);
                                properties.setZoning(zoning);
                                properties.setZone_Category(zone_Category);
                                properties.setOwner(owner);
                                properties.setSurveyed(surveyed);
                                properties.setSite_Area(siteArea);
                                properties.setRelationship(relationship);
                                properties.setSHAPE_Length(shapeLength);
                                properties.setSHAPE_Area(shapeArea);
                                //park
                                park.setGeometry(geometry);
                                park.setProperties(properties);

                            // adding contact to contact list
                            parkList.add(park);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                //return null;
            }


            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();

                //Toon[] toonArray = toonList.toArray(new Toon[toonList.size()]);

                //ToonsAdapter adapter = new ToonsAdapter(MainActivity.this, toonList);

                // Attach the adapter to a ListView
                //lv.setAdapter(adapter);
            }
        }
        return null;

    }//end OnCreate
}//end Information
