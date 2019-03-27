package ca.bcit.bluelizard;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * Class to pull JSON data for washrooms
 */
public class GetWashroomsJSON {
    public interface AsyncResponse {
        void processFinish();
    }
    public AsyncResponse delegate = null;
    private String TAG = Information.class.getSimpleName();
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/accessible-public-washrooms/WASHROOMS.json";
    private static ArrayList<Washroom> washroomList = new ArrayList<>();

    /**
     * Async task class to get json by making HTTP call
     */
    class GetWashrooms extends AsyncTask<Void, Void, List<Washroom>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            washroomList = new ArrayList<>();

        }

        @Override
        protected List<Washroom> doInBackground(Void... arg0) {
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
                        //Coordinates node is a JSON Array
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");
                        //store coordinates in a temporary ArrayList
                        List<Double> coordinates =  new ArrayList<Double>();

                        for(int j = 0; j < jsonArrayCoordinates.length(); j++)
                        {
                            Double temp2 = jsonArrayCoordinates.getDouble(j);
                            coordinates.add(temp2);
                        }

                        Washroom washroom = new Washroom();
                        //add data to washroom datamembers (lat, long)
                        washroom.lattitude = coordinates.get(0);
                        washroom.longitute = coordinates.get(1);
                        //add washroom object to arrayList
                        washroomList.add(washroom);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return washroomList; // return added
        }

        @Override
        protected void onPostExecute(List<Washroom> result) {
            super.onPostExecute(result);
            delegate.processFinish();
        }
    }

    public ArrayList<Washroom> getWashroomList() {
        return washroomList;
    }
}
