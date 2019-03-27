package ca.bcit.bluelizard;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Class to pull JSON data for Water Fountain coordinates
 */
public class GetFountainsJSON {
    public interface AsyncFountain {
        void processFinishFountain();
    }
    public AsyncFountain delegate = null;
    private String TAG = Information.class.getSimpleName();
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/drinking-fountains/DRINKING_FOUNTAINS.json";
    private ArrayList<Fountain> fountainList = new ArrayList<>();

    /**
     * Async task class to get json by making HTTP call
     */
    class GetFountains extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(SERVICE_URL);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting features JSONArray node
                    JSONArray jsonArrayFeatures = null;
                    try {
                        jsonArrayFeatures = jsonObj.getJSONArray("features");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // looping through all features
                    for (int i = 0; i < jsonArrayFeatures.length(); i++) {
                        JSONObject temp = jsonArrayFeatures.getJSONObject(i);
                        //Geometry node is a JSON Object
                        JSONObject jsonObjGeometry = temp.getJSONObject("geometry");
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");

                        List<Double> coordinates =  new ArrayList<Double>();
                        for(int j = 0; j < jsonArrayCoordinates.length(); j++)
                        {
                            Double temp2 = jsonArrayCoordinates.getDouble(j);
                            //Log.e("hi", temp2.toString());
                            coordinates.add(temp2);
                        }

                        //Create a new Fountain object
                        Fountain fountain = new Fountain();
                        fountain.latitude = coordinates.get(0);
                        fountain.longitude = coordinates.get(1);
                        //add fountain to list
                        fountainList.add(fountain);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            delegate.processFinishFountain();
        }
    }

    public ArrayList<Fountain> getFountainList() {
        return fountainList;
    }
}
