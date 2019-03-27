package ca.bcit.bluelizard;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to pull JSON data for Offleash areas coordinates
 */

public class GetOffleashJSON {
    public interface AsyncResponseLeash {
        void processFinishLeash();
    }
    public AsyncResponseLeash delegate = null;
    private String TAG = Information.class.getSimpleName();
    // URL to get offleash areas JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/off-leash-dog-areas/OFFLEASH_DOG_AREAS.json";
    private ArrayList<Park> offLeashList = new ArrayList<>();

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

                    // looping through All features
                    for (int i = 0; i < jsonArrayFeatures.length(); i++) {
                        JSONObject temp = jsonArrayFeatures.getJSONObject(i);
                        //Geometry node is a JSON Object
                        JSONObject jsonObjGeometry = temp.getJSONObject("geometry");
                        //Coordinates node is a JSON Array
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");
                        //There is a nested array
                        List<List<List<Double>>> list = new ArrayList<>();
                        List<List<Double>> innerList = new ArrayList<>();
                        List<Double> innerInnerList = new ArrayList<>();

                        for(int a = 0; a < jsonArrayCoordinates.length(); a++)
                        {
                            boolean isMultiple = false;
                            JSONArray jsonArrayCoordinatesInner = jsonArrayCoordinates.getJSONArray(a);
                            innerList = new ArrayList<>();
                            for(int b = 0; b < jsonArrayCoordinatesInner.length(); b++)
                            {
                                JSONArray jsonArrayCoordinatesInnerInner = jsonArrayCoordinatesInner.getJSONArray(b);
                                innerInnerList = new ArrayList<>();
                                for(int c = 0; c < jsonArrayCoordinatesInnerInner.length(); c++) {
                                    if(jsonArrayCoordinatesInnerInner.get(c) instanceof Double) {
                                        Double tempDouble = jsonArrayCoordinatesInnerInner.getDouble(c);
                                        Log.e("coord1st", tempDouble.toString());
                                        innerInnerList.add(tempDouble);
                                    } else {
                                        JSONArray jsonArrayCoordinatesInnerInnerInner = jsonArrayCoordinatesInnerInner.getJSONArray(c);
                                        for(int d = 0; d < jsonArrayCoordinatesInnerInnerInner.length(); d++)
                                        {
                                            isMultiple = true;
                                            Double tempDouble2 = jsonArrayCoordinatesInnerInnerInner.getDouble(d);
                                            Log.e("coord2nd", tempDouble2.toString());
                                            innerInnerList.add(tempDouble2);
                                        }if(isMultiple) {
                                            innerList.add(innerInnerList);
                                        }
                                    }
                                }
                                if(!isMultiple) {
                                    innerList.add(innerInnerList);
                                }
                            }
                            list.add(innerList);
                        }


                        JSONObject jsonObjProperties = temp.getJSONObject("properties");
                        String name = jsonObjProperties.getString("Name");
                        String category = jsonObjProperties.getString("Category");
                        Park park = new Park();
                        park.name = name;
                        park.category = category;
                        park.coordinates = list;
                        //add washroom object to arrayList
                        offLeashList.add(park);
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
            delegate.processFinishLeash();
        }
    }

    public ArrayList<Park> getParkList() {
        return offLeashList;
    }
}
