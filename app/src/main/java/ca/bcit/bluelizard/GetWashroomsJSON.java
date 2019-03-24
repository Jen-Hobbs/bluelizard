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
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/accessible-public-washrooms/WASHROOMS.json";
    private ArrayList<Washroom> washroomList = new ArrayList<>();

    /**
     * Async task class to get json by making HTTP call
     */
    class GetWashrooms extends AsyncTask<Void, Void, Void> {

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
                            Log.e("value", temp2.toString());
                        }

                        for(int k = 0; k < coordinates.size(); k++)
                        {
                            Log.e("local coordinates : ", coordinates.get(k).toString());
                        }

                        Washroom washroom = new Washroom();
                        washroom.lattitude = coordinates.get(0);
                        washroom.longitute = coordinates.get(1);
                        washroomList.add(washroom);
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

    public ArrayList<Washroom> getWashroomList() {
        return washroomList;
    }
}
