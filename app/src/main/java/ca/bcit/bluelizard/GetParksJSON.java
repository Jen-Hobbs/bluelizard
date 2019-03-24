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

public class GetParksJSON {
    private String TAG = Information.class.getSimpleName();
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
    private ArrayList<Park> parksList = new ArrayList<>();

    /**
     * Async task class to get json by making HTTP call
     */
    class GetParks extends AsyncTask<Void, Void, Void> {

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
                        /*
                        //Coordinates node is a JSON Array
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");
                        //store coordinates in a temporary ArrayList
                        List<List<List<Double>>> coordinates =  new ArrayList<>();
                        List<List<Double>> secondLevelList = new ArrayList<>();
                        List<Double> thirdLevelList = new ArrayList<>();
                        for(int a = 0; a < jsonArrayCoordinates.length(); a++)
                        {
                            JSONArray temp2 = jsonArrayCoordinates.getJSONArray(a);

                            for(int b = 0; b < temp2.length(); b++)
                            {
                                Double temp3 = jsonArrayCoordinates.getDouble(b);
                                thirdLevelList.add(temp3);
                            }

                        }

                        for(int j = 0; j < jsonArrayCoordinates.length(); j++)
                        {
                            Double temp2 = jsonArrayCoordinates.getDouble(j);
                            coordinates.add(temp2);
                        }
                        */

                        //Properties node is a JSON Object
                        JSONObject jsonObjProperties = temp.getJSONObject("properties");
                            //Name node is a JSON Object
                            String name = jsonObjProperties.getString("Name");
                            //String name = jsonObjProperties.toString();
                            Log.e("Parkname", name);

                            //Category node is a JSON Object
                            //JSONObject jsonObjCategory = jsonObjProperties.getJSONObject("Category");
                            //String category = jsonObjCategory.toString();
                            String category = jsonObjProperties.getString("Category");
                            Log.e("Category", category);


                        //make new Washroom object
                        Park park = new Park();
                        //add data to washroom datamembers (lat, long)
                        park.name = name;
                        park.category = category;
                        //add washroom object to arrayList
                        parksList.add(park);
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
        }
    }

    public ArrayList<Park> getParkList() {
        return parksList;
    }
}
