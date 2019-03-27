package ca.bcit.bluelizard;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * Class to pull JSON data for sportsfields
 */
public class GetSportsfieldsJSON {

    public interface AsyncSportsfield {
        void processFinishSportsfield();
    }
    public AsyncSportsfield delegate = null;

    private String TAG = Information.class.getSimpleName();
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/sports-fields/SPORTS_FIELDS.json";
    private ArrayList<Sportsfield> sportsfieldList = new ArrayList<>();

    /**
     * Async task class to get json by making HTTP call
     */
    class GetSportsfields extends AsyncTask<Void, Void, Void> {

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
                        JSONArray jsonArrayCoordinates = jsonObjGeometry.getJSONArray("coordinates");

                        List<Double> coordinates =  new ArrayList<Double>();

                        for(int j = 0; j < jsonArrayCoordinates.length(); j++)
                        {
                            Double temp2 = jsonArrayCoordinates.getDouble(j);
                            coordinates.add(temp2);
                        }

                        //properties node is a JSON Object
                        JSONObject jsonObjProperties = temp.getJSONObject("properties");
                        List<String> activities = new ArrayList<>();
                        String activitiesString = jsonObjProperties.getString("ACTIVITIES");

                       //Add all strings to the arraylist
                        Scanner s = new Scanner(activitiesString);
                        s.useDelimiter(";");
                        while(s.hasNext()){
                            String temp3 = s.next();
                            activities.add(temp3);
                        }
                        Sportsfield sportsfield = new Sportsfield();
                        sportsfield.latitude = coordinates.get(0);
                        sportsfield.longitude = coordinates.get(1);
                        sportsfield.activities = activities;
                        sportsfieldList.add(sportsfield);
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
            delegate.processFinishSportsfield();
        }
    }

    public ArrayList<Sportsfield> getSportsfieldList() {
        return sportsfieldList;
    }
}
