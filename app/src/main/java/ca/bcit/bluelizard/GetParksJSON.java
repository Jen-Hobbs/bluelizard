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
    public interface AsyncResponseParks {
        void processFinishParks();
    }
    public AsyncResponseParks delegate = null;
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
                        List<List<List<Double>>> crazyList = new ArrayList<>();
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
                                        innerInnerList.add(tempDouble);
                                    } else {
                                        JSONArray jsonArrayCoordinatesInnerInnerInner = jsonArrayCoordinatesInnerInner.getJSONArray(c);
                                        for(int d = 0; d < jsonArrayCoordinatesInnerInnerInner.length(); d++)
                                        {
                                            isMultiple = true;
                                            Double tempDouble2 = jsonArrayCoordinatesInnerInnerInner.getDouble(d);
                                            innerInnerList.add(tempDouble2);
                                        }if(isMultiple) {
                                            innerList.add(innerInnerList);
                                        }
                                    }//end else
                                }//end for int c
                                if(!isMultiple) {
                                    innerList.add(innerInnerList);
                                }
                            }//end for int b
                            crazyList.add(innerList);
                        }

                        //grab properties
                        JSONObject jsonObjProperties = temp.getJSONObject("properties");
                        String name = jsonObjProperties.getString("Name");

                        String category = jsonObjProperties.getString("Category");
                        Park park = new Park();
                        //add data to washroom datamembers (lat, long)
                        park.name = name;
                        park.category = category;
                        park.coordinates = crazyList;
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
            delegate.processFinishParks();
        }
    }

    public ArrayList<Park> getParkList() {
        return parksList;
    }
}