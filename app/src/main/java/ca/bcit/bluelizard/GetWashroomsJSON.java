package ca.bcit.bluelizard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetWashroomsJSON {
    private String TAG = Information.class.getSimpleName();
    private ProgressDialog pDialog;
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/accessible-public-washrooms/WASHROOMS.json";
    private ArrayList<Washroom> washroomList;

    /**
     * Async task class to get json by making HTTP call
     */
    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(SERVICE_URL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting root JSON Array node
                    JSONArray jsonArrayFeatures = null;
                    try {
                        jsonArrayFeatures = jsonObj.getJSONArray("features");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // looping through All Contacts
                    for (int i = 0; i < jsonArrayFeatures.length(); i++) {
                        JSONObject temp = jsonArrayFeatures.getJSONObject(i);
                        //Geometry node is a JSON Object
                        JSONObject jsonObjGeometry = temp.getJSONObject("geometry");
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");

                        List<Double> coordinates =  new ArrayList<Double>();

                        //ADD ALL DOUBLES TO THE ARRAYLSIT AHHHH
                        for(int j = 0; j < jsonArrayCoordinates.length(); j++)
                        {
                            Double temp2 = jsonArrayCoordinates.getDouble(j);
                            coordinates.add(temp2);
                            Log.e("hi", temp2.toString());
                        }

                        //make new Washroom Objects
                        WashroomGeometry wg = new WashroomGeometry();
                        WashroomProperties wp = new WashroomProperties();
                        Washroom washroom = new Washroom();

                        //assign data to objects
                        wg.setCoordinates(coordinates);
                        washroom.setGeometry(wg);
                        washroom.setProperties(wp);

                        // adding contact to contact list
                        washroomList.add(washroom);
                            /*
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
                            */
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null; // return added
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Toon[] toonArray = toonList.toArray(new Toon[toonList.size()]);

            //ToonsAdapter adapter = new ToonsAdapter(MainActivity.this, toonList);

            // Attach the adapter to a ListView
            //lv.setAdapter(adapter);
        }
    }

}
